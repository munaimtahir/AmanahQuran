package org.amanahquran.app.feature.reader

import android.content.Context
import org.amanahquran.app.core.database.AmanahContentDatabaseProvider
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.QuranContentRepositoryImpl

import org.amanahquran.app.core.database.DatabaseProvider
import org.amanahquran.app.core.repository.MushafRepository
import org.amanahquran.app.core.repository.MushafRepositoryImpl
import org.amanahquran.app.core.repository.bookmarkRepository

internal fun quranContentRepository(context: Context): QuranContentRepository {
    val database = AmanahContentDatabaseProvider.getDatabase(context.applicationContext)
    return QuranContentRepositoryImpl(
        surahDao = database.surahDao(),
        ayahDao = database.ayahDao(),
        quranTextDao = database.quranTextDao(),
        mushafLayoutReferenceDao = database.mushafLayoutReferenceDao(),
    )
}

internal fun mushafRepository(context: Context): MushafRepository {
    val contentDb = AmanahContentDatabaseProvider.getDatabase(context.applicationContext)
    val userDb = DatabaseProvider.getDatabase(context.applicationContext)
    return MushafRepositoryImpl(
        contentDatabase = contentDb,
        quranDatabase = userDb,
        bookmarkRepository = bookmarkRepository(context)
    )
}
