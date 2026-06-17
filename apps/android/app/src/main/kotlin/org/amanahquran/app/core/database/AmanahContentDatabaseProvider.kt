package org.amanahquran.app.core.database

import android.content.Context
import androidx.room.Room

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
                .build()
            INSTANCE = instance
            instance
        }
    }
}
