package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.MushafLayoutReferenceEntity

@Dao
interface MushafLayoutReferenceDao {
    @Query("SELECT * FROM mushaf_layout_references WHERE page_number = :pageNumber AND layout_name = :layoutName")
    suspend fun getReferencesForPage(pageNumber: Int, layoutName: String): List<MushafLayoutReferenceEntity>

    @Query("SELECT * FROM mushaf_layout_references WHERE layout_name = :layoutName ORDER BY page_number ASC")
    suspend fun getReferencesForLayout(layoutName: String): List<MushafLayoutReferenceEntity>

    @Query("SELECT DISTINCT page_number FROM mushaf_layout_references WHERE layout_name = :layoutName ORDER BY page_number ASC")
    suspend fun getDistinctPageNumbers(layoutName: String): List<Int>

    @Query("SELECT first_ayah_key FROM mushaf_layout_references WHERE page_number = :pageNumber AND layout_name = :layoutName ORDER BY id ASC LIMIT 1")
    suspend fun getFirstAyahKeyForPage(pageNumber: Int, layoutName: String): String?

    @Query("SELECT last_ayah_key FROM mushaf_layout_references WHERE page_number = :pageNumber AND layout_name = :layoutName ORDER BY id ASC LIMIT 1")
    suspend fun getLastAyahKeyForPage(pageNumber: Int, layoutName: String): String?
}
