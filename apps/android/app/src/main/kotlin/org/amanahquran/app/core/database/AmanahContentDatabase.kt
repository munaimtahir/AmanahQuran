package org.amanahquran.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.amanahquran.app.core.database.dao.AyahDao
import org.amanahquran.app.core.database.dao.ContentSourceDao
import org.amanahquran.app.core.database.dao.ContentValidationDao
import org.amanahquran.app.core.database.dao.FontInventoryDao
import org.amanahquran.app.core.database.dao.MushafLayoutReferenceDao
import org.amanahquran.app.core.database.dao.QuranTextDao
import org.amanahquran.app.core.database.dao.SearchIndexDao
import org.amanahquran.app.core.database.dao.SurahDao
import org.amanahquran.app.core.database.entity.AyahEntity
import org.amanahquran.app.core.database.entity.ContentSourceEntity
import org.amanahquran.app.core.database.entity.ContentValidationEntity
import org.amanahquran.app.core.database.entity.FontInventoryEntity
import org.amanahquran.app.core.database.entity.MushafLayoutReferenceEntity
import org.amanahquran.app.core.database.entity.QuranTextEntity
import org.amanahquran.app.core.database.entity.SearchIndexEntity
import org.amanahquran.app.core.database.entity.SurahEntity

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        QuranTextEntity::class,
        SearchIndexEntity::class,
        ContentSourceEntity::class,
        ContentValidationEntity::class,
        MushafLayoutReferenceEntity::class,
        FontInventoryEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AmanahContentDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun quranTextDao(): QuranTextDao
    abstract fun searchIndexDao(): SearchIndexDao
    abstract fun contentSourceDao(): ContentSourceDao
    abstract fun contentValidationDao(): ContentValidationDao
    abstract fun mushafLayoutReferenceDao(): MushafLayoutReferenceDao
    abstract fun fontInventoryDao(): FontInventoryDao

    companion object {
        const val DATABASE_NAME = "amanah_quran_content_v1_candidate.sqlite"
        const val ASSET_PATH = "database/amanah_quran_content_v1_candidate.sqlite"
    }
}
