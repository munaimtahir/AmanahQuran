package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FontInventoryDao {
    @Query("SELECT COUNT(*) FROM font_inventory")
    suspend fun getFontInventoryCount(): Int
}
