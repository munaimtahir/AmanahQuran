package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.amanahquran.app.core.database.entity.MushafPageEntity
import org.amanahquran.app.core.database.entity.MushafLineEntity

@Dao
interface MushafDao {
    @Query("SELECT * FROM mushaf_pages WHERE pageNumber = :pageNumber LIMIT 1")
    suspend fun getPage(pageNumber: Int): MushafPageEntity?

    @Query("""
        SELECT * FROM mushaf_lines
        WHERE pageNumber = :pageNumber
        AND scriptType = :scriptType
        ORDER BY lineNumber ASC
    """)
    suspend fun getPageLines(
        pageNumber: Int,
        scriptType: String
    ): List<MushafLineEntity>

    @Query("SELECT COUNT(*) FROM mushaf_pages")
    suspend fun getPageCount(): Int

    @Query("""
        SELECT * FROM mushaf_lines
        WHERE scriptType = :scriptType
        ORDER BY pageNumber ASC, lineNumber ASC
    """)
    suspend fun getAllLinesForScript(scriptType: String): List<MushafLineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: MushafPageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<MushafPageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLines(lines: List<MushafLineEntity>)

    @Query("DELETE FROM mushaf_pages")
    suspend fun clearPages()

    @Query("DELETE FROM mushaf_lines")
    suspend fun clearLines()
}
