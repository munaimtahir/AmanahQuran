package org.amanahquran.app.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object AmanahContentDatabaseProvider {
    @Volatile
    private var INSTANCE: AmanahContentDatabase? = null

    fun getDatabase(context: Context): AmanahContentDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AmanahContentDatabase::class.java,
                AmanahContentDatabase.DATABASE_NAME
            )
                .createFromAsset(AmanahContentDatabase.ASSET_PATH)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ayahs_surah_ayah ON ayahs(surah_number, ayah_number)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ayahs_juz_order ON ayahs(juz_number, surah_number, ayah_number)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ayahs_page_order ON ayahs(page_number, surah_number, ayah_number)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ayahs_key ON ayahs(ayah_key)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS idx_quran_texts_ayah_script ON quran_texts(ayah_key, script_type)")
                        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_quran_texts_unique_ayah_script ON quran_texts(ayah_key, script_type)")
                    }
                })
                .build()
            INSTANCE = instance
            instance
        }
    }
}
