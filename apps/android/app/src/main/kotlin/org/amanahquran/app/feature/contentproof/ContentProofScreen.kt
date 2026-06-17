package org.amanahquran.app.feature.contentproof

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.amanahquran.app.content.validation.ContentValidationService
import org.amanahquran.app.content.validation.ContentValidationSnapshot
import org.amanahquran.app.core.database.AmanahContentDatabaseProvider
import org.amanahquran.app.core.trust.TrustCenterAssetLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentProofScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    var snapshot by remember { mutableStateOf<ContentValidationSnapshot?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(context) {
        runCatching {
            val database = AmanahContentDatabaseProvider.getDatabase(context)
            ContentValidationService(
                database = database,
                trustCenterAssetLoader = TrustCenterAssetLoader(context),
            ).validatePackagedContent()
        }.onSuccess {
            snapshot = it
        }.onFailure {
            error = it.message ?: it::class.java.simpleName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content Proof") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (error != null) {
                item {
                    Text("DB loaded: no", style = MaterialTheme.typography.titleMedium)
                    Text(error.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            } else if (snapshot == null) {
                item { Text("Loading content proof...", style = MaterialTheme.typography.bodyLarge) }
            } else {
                val proof = snapshot ?: return@LazyColumn
                item { ProofLine("DB loaded", proof.dbLoaded.yesNo()) }
                item { ProofLine("Surah count", proof.surahCount.toString()) }
                item { ProofLine("Ayah count", proof.ayahCount.toString()) }
                item { ProofLine("Uthmani row count", proof.uthmaniRows.toString()) }
                item { ProofLine("IndoPak row count", proof.indopakRows.toString()) }
                item { ProofLine("Search index row count", proof.searchIndexRows.toString()) }
                item { ProofLine("Content source count", proof.contentSourceRows.toString()) }
                item { ProofLine("Validation rows count", proof.validationRows.toString()) }
                item { ProofLine("Font inventory rows", proof.fontInventoryRows.toString()) }
                item { ProofLine("Sample ayah 1:1 Uthmani", proof.sampleUthmani11.orEmpty()) }
                item { ProofLine("Sample ayah 1:1 IndoPak", proof.sampleIndopak11.orEmpty()) }
                item { ProofLine("Sample ayah 2:255 Uthmani", proof.sampleUthmani2255.orEmpty()) }
                item { ProofLine("Sample ayah 2:255 IndoPak", proof.sampleIndopak2255.orEmpty()) }
                item { ProofLine("Trust Center JSON loaded", proof.trustJsonLoaded.yesNo()) }
                item {
                    ProofLine(
                        label = "No-modification statement preview",
                        value = proof.noModificationStatementPreview.orEmpty(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProofLine(
    label: String,
    value: String,
) {
    Text(
        text = "$label: $value",
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
    )
}

private fun Boolean.yesNo(): String = if (this) "yes" else "no"
