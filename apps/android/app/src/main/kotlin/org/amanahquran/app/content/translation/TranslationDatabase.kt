package org.amanahquran.app.content.translation

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TranslationMetadataEntity::class, TranslationAyahEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        const val DATABASE_NAME = "translation_urdu_junagarhi.db"
        const val ASSET_PATH = "content/translations/translation_urdu_junagarhi.db"
    }
}

object TranslationDatabaseProvider {
    @Volatile
    private var instance: TranslationDatabase? = null

    fun getDatabase(context: Context): TranslationDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                TranslationDatabase::class.java,
                TranslationDatabase.DATABASE_NAME,
            )
                .createFromAsset(TranslationDatabase.ASSET_PATH)
                .build()
                .also { instance = it }
        }
    }
}
