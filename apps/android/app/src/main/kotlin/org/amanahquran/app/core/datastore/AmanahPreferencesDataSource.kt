package org.amanahquran.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import java.io.File

private val Context.amanahPreferencesStore: DataStore<Preferences> by preferencesDataStore(
    name = "amanah_quran_user_state",
)

class AmanahPreferencesDataSource(
    val dataStore: DataStore<Preferences>,
)

fun amanahPreferencesDataSource(context: Context): AmanahPreferencesDataSource {
    return AmanahPreferencesDataSource(context.amanahPreferencesStore)
}

fun amanahPreferencesDataSourceForFile(file: File): AmanahPreferencesDataSource {
    return AmanahPreferencesDataSource(
        PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = emptyList(),
            scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO),
            produceFile = { file },
        ),
    )
}
