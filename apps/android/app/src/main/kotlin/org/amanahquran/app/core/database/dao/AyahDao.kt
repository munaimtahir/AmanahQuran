package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.AyahEntity

@Dao
interface AyahDao {
    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getAyahCount(): Int

    @Query("SELECT * FROM ayahs WHERE ayah_key = :ayahKey")
    suspend fun getAyahByKey(ayahKey: String): AyahEntity?

    @Query("SELECT * FROM ayahs WHERE ayah_key IN (:ayahKeys)")
    suspend fun getAyahsByKeys(ayahKeys: List<String>): List<AyahEntity>

    @Query("SELECT page_number AS pageNumber, COUNT(*) AS ayahCount FROM ayahs GROUP BY page_number")
    suspend fun getAyahCountsByPage(): List<PageAyahCountRow>

    @Query("""
        SELECT
            ayah_key AS ayahKey,
            surah_number AS surahNumber,
            ayah_number AS ayahNumber,
            juz_number AS juzNumber,
            page_number AS pageNumber
        FROM ayahs
        WHERE ayah_key = :ayahKey
        LIMIT 1
    """)
    suspend fun resolveAyahReference(ayahKey: String): AyahReferenceRow?

    @Query("SELECT * FROM ayahs WHERE surah_number = :surahNumber ORDER BY ayah_number ASC")
    suspend fun getAyahsBySurah(surahNumber: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE page_number = :pageNumber ORDER BY surah_number ASC, ayah_number ASC")
    suspend fun getAyahsByPageIndopak(pageNumber: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE page_number = :pageNumber ORDER BY surah_number ASC, ayah_number ASC")
    suspend fun getAyahsByPageUthmani(pageNumber: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE juz_number = :juzNumber ORDER BY surah_number ASC, ayah_number ASC")
    suspend fun getAyahsByJuz(juzNumber: Int): List<AyahEntity>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key
        WHERE a.page_number = :pageNumber AND qt.script_type = :scriptType
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    suspend fun getAyahsByPage(pageNumber: Int, scriptType: String): List<AyahDisplayRow>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key
        WHERE a.juz_number = :juzNumber AND qt.script_type = :scriptType
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    suspend fun getAyahsByJuz(juzNumber: Int, scriptType: String): List<AyahDisplayRow>

    @Query("SELECT ayah_key FROM ayahs WHERE page_number = :pageNumber ORDER BY surah_number ASC, ayah_number ASC LIMIT 1")
    suspend fun getFirstAyahKeyForPage(pageNumber: Int): String?

    @Query("SELECT ayah_key FROM ayahs WHERE juz_number = :juzNumber ORDER BY surah_number ASC, ayah_number ASC LIMIT 1")
    suspend fun getFirstAyahKeyForJuz(juzNumber: Int): String?

    @Query("SELECT page_number FROM ayahs WHERE ayah_key = :ayahKey LIMIT 1")
    suspend fun getPageNumberForAyah(ayahKey: String): Int?

    @Query("SELECT juz_number FROM ayahs WHERE ayah_key = :ayahKey LIMIT 1")
    suspend fun getJuzNumberForAyah(ayahKey: String): Int?

    @Query("SELECT COUNT(*) FROM ayahs WHERE page_number = :pageNumber")
    suspend fun getAyahCountForPage(pageNumber: Int): Int

    @Query("SELECT COUNT(*) FROM ayahs WHERE juz_number = :juzNumber")
    suspend fun getAyahCountForJuz(juzNumber: Int): Int

    @Query("SELECT COUNT(*) FROM (SELECT ayah_key FROM ayahs GROUP BY ayah_key HAVING COUNT(*) > 1)")
    suspend fun getDuplicateAyahKeyCount(): Int

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key
        WHERE a.surah_number = :surahNumber 
          AND a.ayah_number >= :startAyahNumber 
          AND a.ayah_number <= :endAyahNumber 
          AND qt.script_type = :scriptType
        ORDER BY a.ayah_number ASC
    """)
    suspend fun getAyahsByRange(
        surahNumber: Int,
        startAyahNumber: Int,
        endAyahNumber: Int,
        scriptType: String
    ): List<AyahDisplayRow>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText,
            s.name_arabic AS surahNameArabic,
            s.name_simple AS surahNameSimple,
            s.ayah_count AS surahAyahCount
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key AND qt.script_type = :scriptType
        INNER JOIN surahs s ON s.number = a.surah_number
        WHERE a.surah_number >= :surahNumber
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    // READER-UX-02: continuous reading from Surah Index -- opening Surah N returns every ayah
    // from N through the end of the Quran (Surah 114), not just Surah N's own ayahs, so scrolling
    // past a surah's last ayah flows naturally into the next surah (with its own header) rather
    // than dead-ending. Surah/ayah ordering is monotonic with canonical Quran order, so this
    // simple range is equivalent to "from Surah N onward" with no separate global index needed.
    suspend fun getReaderAyahsBySurah(
        surahNumber: Int,
        scriptType: String,
    ): List<ReaderAyahRow>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText,
            s.name_arabic AS surahNameArabic,
            s.name_simple AS surahNameSimple,
            s.ayah_count AS surahAyahCount
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key AND qt.script_type = :scriptType
        INNER JOIN surahs s ON s.number = a.surah_number
        WHERE a.juz_number >= :juzNumber
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    // READER-UX-02: continuous reading from Juz Index -- same "from N through the end" widening
    // as getReaderAyahsBySurah above, applied to Juz.
    suspend fun getReaderAyahsByJuz(
        juzNumber: Int,
        scriptType: String,
    ): List<ReaderAyahRow>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText,
            s.name_arabic AS surahNameArabic,
            s.name_simple AS surahNameSimple,
            s.ayah_count AS surahAyahCount
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key AND qt.script_type = :scriptType
        INNER JOIN surahs s ON s.number = a.surah_number
        WHERE a.page_number = :pageNumber
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    suspend fun getReaderAyahsByPage(
        pageNumber: Int,
        scriptType: String,
    ): List<ReaderAyahRow>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText,
            s.name_arabic AS surahNameArabic,
            s.name_simple AS surahNameSimple,
            s.ayah_count AS surahAyahCount
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key AND qt.script_type = :scriptType
        INNER JOIN surahs s ON s.number = a.surah_number
        WHERE a.ayah_key = :ayahKey
        LIMIT 1
    """)
    suspend fun getReaderAyahByKey(
        ayahKey: String,
        scriptType: String,
    ): ReaderAyahRow?
}
