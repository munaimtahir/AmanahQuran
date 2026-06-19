package org.amanahquran.app.feature.reader

import android.content.Context
import org.amanahquran.app.core.database.AmanahContentDatabaseProvider
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl

internal fun quranContentRepository(context: Context): QuranContentRepository {
    val database = AmanahContentDatabaseProvider.getDatabase(context.applicationContext)
    return QuranContentRepositoryImpl(
        surahDao = database.surahDao(),
        ayahDao = database.ayahDao(),
        quranTextDao = database.quranTextDao(),
        mushafLayoutReferenceDao = database.mushafLayoutReferenceDao(),
    )
}
