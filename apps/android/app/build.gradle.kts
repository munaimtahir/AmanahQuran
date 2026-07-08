import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

val releaseKeystoreProperties = Properties().apply {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.isFile) {
        FileInputStream(keystorePropertiesFile).use { load(it) }
    }
}

fun releaseSigningValue(propertyName: String, envName: String): String? {
    val propertyValue = releaseKeystoreProperties.getProperty(propertyName)?.trim()?.takeIf { it.isNotEmpty() }
    if (propertyValue != null) return propertyValue
    return System.getenv(envName)?.trim()?.takeIf { it.isNotEmpty() }
}

val releaseStoreFilePath = releaseSigningValue("storeFile", "AMANAH_RELEASE_STORE_FILE")
val releaseStorePassword = releaseSigningValue("storePassword", "AMANAH_RELEASE_STORE_PASSWORD")
val releaseKeyAlias = releaseSigningValue("keyAlias", "AMANAH_RELEASE_KEY_ALIAS")
val releaseKeyPassword = releaseSigningValue("keyPassword", "AMANAH_RELEASE_KEY_PASSWORD")
val releaseSigningConfigured = listOf(
    releaseStoreFilePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it != null } && rootProject.file(releaseStoreFilePath!!).isFile

fun resolveRepoRootScript(relativePath: String): File {
    val repoRoot = rootProject.projectDir.parentFile?.parentFile
        ?: throw GradleException("BLOCKED: Unable to resolve repository root from ${rootProject.projectDir.absolutePath}")
    val scriptFile = repoRoot.resolve(relativePath)
    if (!scriptFile.isFile) {
        throw GradleException(
            "BLOCKED: Required script is missing. Expected ${scriptFile.absolutePath}",
        )
    }
    return scriptFile
}

fun selectedReleaseTrack(): String {
    val track = providers.gradleProperty("amanahReleaseTrack")
        .orElse("public")
        .get()
        .trim()
        .lowercase()
    if (track !in setOf("internal", "public")) {
        throw GradleException(
            "BLOCKED: Invalid amanahReleaseTrack='$track'. Use 'public' or 'internal'.",
        )
    }
    return track
}

val amanahReleaseTrack = selectedReleaseTrack()
val amanahReleaseLabel = if (amanahReleaseTrack == "internal") {
    "INTERNAL TESTING ONLY - NOT PUBLIC RELEASE APPROVED"
} else {
    "PUBLIC RELEASE TRACK"
}

android {
    namespace = "org.amanahquran.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.amanahquran.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFilePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            ndk {
                debugSymbolLevel = "NONE"
            }
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.register("verifyReleaseSigningConfigured") {
    group = "verification"
    description = "Fail release builds unless release signing credentials are configured."

    doLast {
        if (!releaseSigningConfigured) {
            throw GradleException(
                "BLOCKED: Release signing is not configured. Create apps/android/keystore.properties or set AMANAH_RELEASE_* environment variables.",
            )
        }
    }
}

val generateContentPipeline by tasks.registering(Exec::class) {
    group = "verification"
    description = "Generate the auditable Quran content pipeline."
    val scriptFile = resolveRepoRootScript("scripts/generate_content_pipeline.py")
    commandLine(
        "python3",
        scriptFile.absolutePath,
    )
}

val validateContentLicenses by tasks.registering(Exec::class) {
    group = "verification"
    description = "Validate source/license registry completeness."
    dependsOn(generateContentPipeline)
    val scriptFile = resolveRepoRootScript("scripts/validate_content_licenses.py")
    commandLine(
        "python3",
        scriptFile.absolutePath,
        "--profile",
        "internal",
        "--scope",
        "packaged",
    )
}

val validatePublicContentLicenses by tasks.registering(Exec::class) {
    group = "verification"
    description = "Validate public-release content licenses for packaged assets."
    dependsOn(generateContentPipeline)
    val scriptFile = resolveRepoRootScript("scripts/validate_content_licenses.py")
    commandLine(
        "python3",
        scriptFile.absolutePath,
        "--profile",
        "public",
        "--scope",
        "packaged",
    )
}

val scanPackagedContentAssets by tasks.registering(Exec::class) {
    group = "verification"
    description = "Scan Android packaged content assets."
    dependsOn(generateContentPipeline)
    val scriptFile = resolveRepoRootScript("scripts/scan_packaged_content_assets.py")
    commandLine(
        "python3",
        scriptFile.absolutePath,
        "--profile",
        amanahReleaseTrack,
        "--scope",
        "packaged",
    )
}

val validateQuranDatabase by tasks.registering(Exec::class) {
    group = "verification"
    description = "Validate the generated Quran SQLite database."
    dependsOn(generateContentPipeline)
    val scriptFile = resolveRepoRootScript("scripts/validate_quran_database.py")
    commandLine(
        "python3",
        scriptFile.absolutePath,
    )
}

val validateReleaseContent by tasks.registering {
    group = "verification"
    description = "Run the content pipeline gates required for release."
    dependsOn(
        validateQuranDatabase,
        scanPackagedContentAssets,
        if (amanahReleaseTrack == "internal") validateContentLicenses else validatePublicContentLicenses,
    )

    doLast {
        val finalDb = file("src/main/assets/database/quran.db")
        val trustJson = file("src/main/assets/trust/trust_center_content.json")
        val failures = mutableListOf<String>()
        if (!finalDb.isFile) {
            failures += "Final quran.db asset is missing from Android app assets"
        }
        if (!trustJson.isFile) {
            failures += "Trust Center JSON is missing from Android app assets"
        }
        if (failures.isNotEmpty()) {
            throw GradleException("BLOCKED: Release content validation failed:\n- ${failures.joinToString("\n- ")}")
        }

        val markerFile = layout.buildDirectory.file("reports/amanah-release/release_track.txt").get().asFile
        markerFile.parentFile.mkdirs()
        markerFile.writeText(
            buildString {
                appendLine("amanahReleaseTrack=$amanahReleaseTrack")
                appendLine("artifactLabel=$amanahReleaseLabel")
            },
            Charsets.UTF_8,
        )

        println("INFO: Amanah release track = $amanahReleaseTrack")
        println("INFO: Amanah artifact label = $amanahReleaseLabel")
    }
}

tasks.matching {
    it.name in setOf("assembleRelease", "bundleRelease", "packageRelease")
}.configureEach {
    dependsOn(validateReleaseContent)
}

tasks.matching { it.name.contains("Release", ignoreCase = true) && it.name != "verifyReleaseSigningConfigured" }.configureEach {
    dependsOn("verifyReleaseSigningConfigured")
}

dependencies {
    val roomVersion = "2.6.1"
    val coroutinesVersion = "1.8.1"
    val datastoreVersion = "1.1.1"

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.robolectric:robolectric:4.12.1")
    testImplementation("androidx.test:core:1.6.1")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

tasks.register<Exec>("validateQuranFonts") {
    group = "verification"
    description = "Validate Quran fonts glyph coverage against database unique characters."
    
    val scriptFile = file("${project.rootDir}/../../tools/validate_quran_font_coverage.py")
    val indopakFontFile = file("${project.projectDir}/src/main/res/font/digital_khatt_indopak.otf")
    val uthmaniPrimaryFontFile = file("${project.projectDir}/src/main/res/font/digital_khatt_v2.otf")
    val uthmaniFallbackFontFile = file("${project.projectDir}/src/main/res/font/indopak_nastaleeq.ttf")
    
    inputs.file(scriptFile)
    if (indopakFontFile.exists()) {
        inputs.file(indopakFontFile)
    }
    if (uthmaniPrimaryFontFile.exists()) {
        inputs.file(uthmaniPrimaryFontFile)
    }
    if (uthmaniFallbackFontFile.exists()) {
        inputs.file(uthmaniFallbackFontFile)
    }
    
    val indopakReport = file("${project.rootDir}/../../build/reports/indopak_glyph_coverage_report.txt")
    val uthmaniReport = file("${project.rootDir}/../../build/reports/uthmani_glyph_coverage_report.txt")
    outputs.file(indopakReport)
    outputs.file(uthmaniReport)
    
    commandLine("python3", scriptFile.absolutePath)
    
    isIgnoreExitValue = true
    
    doLast {
        val manifestFile = file("${project.rootDir}/../../projectdata/managed/font_manifest.json")
        if (!manifestFile.exists()) {
            throw GradleException("BLOCKED: Font manifest file not found at: ${manifestFile.absolutePath}")
        }
        
        val expectedIndopakSha = "59a5e78c530de236a365354d558b37706f37d782f7ee95c3c9b7fe9e0fad042a"
        val expectedUthmaniPrimarySha = "0935c48269a57c9808e52dfae47864c189396452901c689977156036a72dd217"
        val expectedUthmaniFallbackSha = "a6463e24e36651404e9eff52dae26e18e9ef0718eb620636a66a20026a75c563"
        
        fun getSha256(file: File): String {
            if (!file.exists()) return ""
            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(8192)
            file.inputStream().use { input ->
                var bytesRead = input.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = input.read(buffer)
                }
            }
            return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
        }
        
        if (!indopakFontFile.exists()) {
            throw GradleException("BLOCKED: IndoPak font file not found at: ${indopakFontFile.absolutePath}")
        }
        val actualIndopakSha = getSha256(indopakFontFile)
        if (actualIndopakSha != expectedIndopakSha) {
            throw GradleException("BLOCKED: IndoPak font checksum mismatch! Expected: $expectedIndopakSha, Got: $actualIndopakSha")
        }
        
        if (!uthmaniPrimaryFontFile.exists()) {
            throw GradleException("BLOCKED: Uthmani primary font file not found at: ${uthmaniPrimaryFontFile.absolutePath}")
        }
        val actualUthmaniPrimarySha = getSha256(uthmaniPrimaryFontFile)
        if (actualUthmaniPrimarySha != expectedUthmaniPrimarySha) {
            throw GradleException("BLOCKED: Uthmani primary font checksum mismatch! Expected: $expectedUthmaniPrimarySha, Got: $actualUthmaniPrimarySha")
        }

        if (!uthmaniFallbackFontFile.exists()) {
            throw GradleException("BLOCKED: Uthmani fallback font file not found at: ${uthmaniFallbackFontFile.absolutePath}")
        }
        val actualUthmaniFallbackSha = getSha256(uthmaniFallbackFontFile)
        if (actualUthmaniFallbackSha != expectedUthmaniFallbackSha) {
            throw GradleException("BLOCKED: Uthmani fallback font checksum mismatch! Expected: $expectedUthmaniFallbackSha, Got: $actualUthmaniFallbackSha")
        }
        
        val reports = listOf(Pair("IndoPak", indopakReport), Pair("Uthmani", uthmaniReport))
        reports.forEach { (name, report) ->
            if (report.exists()) {
                val text = report.readText()
                if (text.contains("Total Unsupported Characters:") && !text.contains("Total Unsupported Characters: 0")) {
                    val msg = "WARNING: $name font lacks some Quranic glyphs! Check: ${report.absolutePath}"
                    val isReleaseBuild = project.gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }
                    if (isReleaseBuild) {
                        throw GradleException("BLOCKED: $msg")
                    } else {
                        logger.warn("=========================================================================")
                        logger.warn(msg)
                        logger.warn("=========================================================================")
                    }
                } else {
                    logger.info("$name font glyph coverage check passed successfully.")
                }
            } else {
                logger.warn("WARNING: $name glyph coverage report was not generated.")
            }
        }
    }
}

tasks.matching { it.name.startsWith("compile") && it.name.endsWith("Kotlin") }.configureEach {
    dependsOn("validateQuranFonts")
}
