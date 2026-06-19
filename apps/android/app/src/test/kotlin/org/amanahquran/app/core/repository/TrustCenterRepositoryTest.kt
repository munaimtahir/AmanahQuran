package org.amanahquran.app.core.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.junit.After
import org.junit.Assert.assertFalse
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
    private lateinit var database: AmanahContentDatabase
    private lateinit var repository: TrustCenterRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.databaseBuilder(
            context,
            AmanahContentDatabase::class.java,
            "trust-test-${System.nanoTime()}-${AmanahContentDatabase.DATABASE_NAME}",
        )
            .createFromAsset(AmanahContentDatabase.ASSET_PATH)
            .allowMainThreadQueries()
            .build()
        repository = TrustCenterRepositoryImpl(
            context = context,
            contentSourceDao = database.contentSourceDao(),
            contentValidationDao = database.contentValidationDao(),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun loadsLocalAssetAndIncludesPrivacyAndNoModificationStatements() = runTest {
        val uiState = repository.loadTrustCenterUiState()

        assertNotNull(uiState.noModificationStatement)
        assertNotNull(uiState.privacyPledge)
        assertTrue(uiState.quranTextSourcesActuallyUsed.isNotEmpty())
        assertTrue(uiState.quranTextSourcesActuallyUsed.any { it.scriptType == "UTHMANI" })
        assertTrue(uiState.quranTextSourcesActuallyUsed.any { it.scriptType == "INDOPAK" })
        assertTrue(uiState.claimsNotMade.isNotEmpty())
        assertTrue(uiState.claimsNotMade.any { it.contains("public release ready", ignoreCase = true) })
    }
}
