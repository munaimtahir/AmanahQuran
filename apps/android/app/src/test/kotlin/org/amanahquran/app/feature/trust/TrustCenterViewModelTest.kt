package org.amanahquran.app.feature.trust

import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.repository.PackagedAssetVerification
import org.amanahquran.app.core.repository.TrustCenterRepository
import org.amanahquran.app.core.repository.TrustCenterUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class TrustCenterViewModelTest {
    @Test
    fun verificationFailureClearsProgressAndExposesOfflineError() = runTest {
        val repository = object : TrustCenterRepository {
            override suspend fun loadTrustCenterUiState(): TrustCenterUiState = TrustCenterUiState()

            override suspend fun verifyPackagedContent(): List<PackagedAssetVerification> {
                error("asset checksum unavailable")
            }
        }
        val viewModel = TrustCenterViewModel(repository, UnconfinedTestDispatcher(testScheduler))

        viewModel.verifyNow()

        assertFalse(viewModel.uiState.value.isVerifying)
        assertEquals("asset checksum unavailable", viewModel.uiState.value.verificationError)
        assertNull(viewModel.uiState.value.verificationCheckedAt)
    }

    @Test
    fun verificationSuccessPublishesResultsAndTimestamp() = runTest {
        val expected = PackagedAssetVerification("quran.db", "expected", "expected", true)
        val repository = object : TrustCenterRepository {
            override suspend fun loadTrustCenterUiState(): TrustCenterUiState = TrustCenterUiState()

            override suspend fun verifyPackagedContent(): List<PackagedAssetVerification> = listOf(expected)
        }
        val viewModel = TrustCenterViewModel(repository, UnconfinedTestDispatcher(testScheduler))

        viewModel.verifyNow()

        assertFalse(viewModel.uiState.value.isVerifying)
        assertEquals(listOf(expected), viewModel.uiState.value.verificationResults)
        assertEquals(null, viewModel.uiState.value.verificationError)
    }
}
