package org.amanahquran.app.core.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AmanahQuranDatabase? = null

    fun getDatabase(context: Context): AmanahQuranDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AmanahQuranDatabase::class.java,
                AmanahQuranDatabase.DATABASE_NAME
            )
            // .createFromAsset("db/amanah_quran.db") // Planned for later
            .fallbackToDestructiveMigration() // For development only
            .build()
            INSTANCE = instance
            instance
        }
    }
}
