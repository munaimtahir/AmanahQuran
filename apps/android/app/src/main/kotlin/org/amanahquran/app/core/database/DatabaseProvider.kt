package org.amanahquran.app.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
            .addMigrations(MIGRATION_3_4)
            .build()
            INSTANCE = instance
            instance
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS bookmark_collections " +
                    "(collectionId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, isDefault INTEGER NOT NULL)",
            )
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS bookmark_collection_membership " +
                    "(collectionId INTEGER NOT NULL, bookmarkId INTEGER NOT NULL, " +
                    "PRIMARY KEY(collectionId, bookmarkId))",
            )
            db.execSQL(
                "INSERT INTO bookmark_collections(name, createdAt, updatedAt, isDefault) " +
                    "SELECT 'Default', strftime('%s','now') * 1000, strftime('%s','now') * 1000, 1 " +
                    "WHERE NOT EXISTS (SELECT 1 FROM bookmark_collections WHERE isDefault = 1)",
            )
        }
    }
}
