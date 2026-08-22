package org.amanahquran.app.core.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TrustCenterRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: TrustCenterRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = TrustCenterRepositoryImpl(context = context)
    }

    @Test
    fun loadsLocalAssetAndIncludesPrivacyAndNoModificationStatements() = runTest {
        val uiState = repository.loadTrustCenterUiState()

        assertNotNull(uiState.noModificationStatement)
        assertNotNull(uiState.privacyPledge)
        assertTrue(uiState.quranTextSourcesActuallyUsed.isNotEmpty())
        assertTrue(uiState.quranTextSourcesActuallyUsed.any { it.scriptType == "UTHMANI" })
        assertTrue(uiState.quranTextSourcesActuallyUsed.any { it.scriptType == "INDOPAK" })
        assertTrue(uiState.sourceReferences.isNotEmpty())
        assertTrue(uiState.sourceReferences.any { it.licenseName?.contains("Creative Commons Attribution 3.0", ignoreCase = true) == true })
        assertTrue(uiState.sourceReferences.any { it.licenseName?.contains("SIL Open Font License 1.1", ignoreCase = true) == true })
        assertTrue(uiState.quranTextSourcesActuallyUsed.first { it.scriptType == "INDOPAK" }
            .referenceType?.contains("display text", ignoreCase = true) == true)
    }

    @Test
    fun publicReleaseIsAllowedWhenAllApprovalGatesPass() = runTest {
        val uiState = repository.loadTrustCenterUiState()

        assertTrue(uiState.publicReleaseAllowed)
        assertNotNull(uiState.productionApprovalStatement)
        assertTrue(
            uiState.productionApprovalStatement?.contains("verified and approved for open production release", ignoreCase = true) == true,
        )
    }

    @Test
    fun verifyPackagedContentRecomputesChecksumsAndMatchesRecordedValues() = runTest {
        val results = repository.verifyPackagedContent()

        assertTrue(results.isNotEmpty())
        val quranDb = results.first { it.assetName == "quran.db" }
        assertNotNull(quranDb.expectedChecksum)
        assertTrue(quranDb.matches)

        val translation = results.first { it.assetName == "translation_content.db" }
        assertNotNull(translation.expectedChecksum)
        assertTrue(translation.matches)
    }

    @Test
    fun translationTrustInfoReflectsActualImportedCounts() = runTest {
        val uiState = repository.loadTrustCenterUiState()

        assertEquals(2, uiState.translations.size)
        val manifest = uiState.translations.first { it.translationId == "TAHIR_QADRI_MANIFEST_EN" }
        val irfan = uiState.translations.first { it.translationId == "TAHIR_QADRI_IRFAN_UR" }

        assertEquals(6236, manifest.totalCanonicalCount)
        assertEquals(1, manifest.sourceMissingCount)
        assertEquals(6235, manifest.availableCount)
        assertEquals(142, manifest.footnoteCount)
        assertEquals("APPROVED", manifest.permissionStatus)

        assertEquals(6236, irfan.totalCanonicalCount)
        assertEquals(1, irfan.sourceMissingCount)
        assertEquals(6235, irfan.availableCount)
        assertEquals(45, irfan.footnoteCount)
        assertEquals("APPROVED", irfan.permissionStatus)
    }
}
