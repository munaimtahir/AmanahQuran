package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.MushafLayoutReferenceEntity

@Dao
interface MushafLayoutReferenceDao {
    @Query("SELECT * FROM mushaf_layout_references WHERE page_number = :pageNumber")
    suspend fun getReferencesForPage(pageNumber: Int): List<MushafLayoutReferenceEntity>
}
