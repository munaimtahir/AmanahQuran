package org.amanahquran.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.amanahquran.app.core.database.converter.CommonConverters
import org.amanahquran.app.core.database.dao.*
import org.amanahquran.app.core.database.entity.*

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        QuranTextEntity::class,
        SearchIndexEntity::class,
        BookmarkEntity::class,
        LastReadEntity::class,
        ContentSourceEntity::class,
        ContentValidationEntity::class,
        ContentManifestEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(CommonConverters::class)
abstract class AmanahQuranDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun quranTextDao(): QuranTextDao
    abstract fun searchIndexDao(): SearchIndexDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun lastReadDao(): LastReadDao
    abstract fun contentSourceDao(): ContentSourceDao
    abstract fun contentValidationDao(): ContentValidationDao
    abstract fun contentManifestDao(): ContentManifestDao

    companion object {
        const val DATABASE_NAME = "amanah_quran.db"
    }
}
