package org.amanahquran.app.core.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.robolectric.RuntimeEnvironment
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LastReadAndBookmarkRepositoryTest {
    private lateinit var tempFile: File
    private lateinit var dataSource: org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
    private lateinit var lastReadRepository: LastReadRepository
    private lateinit var bookmarkRepository: BookmarkRepository

    @Before
    fun setUp() {
        tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-user-state-${System.nanoTime()}.preferences_pb",
        )
        dataSource = amanahPreferencesDataSourceForFile(tempFile)
        lastReadRepository = LastReadRepositoryImpl(dataSource)
        bookmarkRepository = BookmarkRepositoryImpl(dataSource)
    }

    @Test
    fun lastReadSavesAyahKeyAndReloadsAfterRepositoryRecreation() = runTest {
        lastReadRepository.saveLastRead(
            LastReadState(
                ayahKey = "2:255",
                surahNumber = 2,
                ayahNumber = 255,
                pageNumber = 42,
                juzNumber = 3,
                scriptType = ScriptType.UTHMANI,
                updatedAt = 1234L,
            ),
        )

        val persisted = lastReadRepository.getLastRead().first()

        assertNotNull(persisted)
        assertEquals("2:255", persisted!!.ayahKey)
        assertEquals(2, persisted.surahNumber)
        assertEquals(255, persisted.ayahNumber)
        assertEquals(42, persisted.pageNumber)
        assertEquals(3, persisted.juzNumber)
        assertEquals(ScriptType.UTHMANI, persisted.scriptType)
    }

    @Test
    fun bookmarkAddToggleAndRemoveUseAyahKeyIdentity() = runTest {
        assertEquals(0, bookmarkRepository.getBookmarkCount())

        val added = bookmarkRepository.addAyahBookmark("2:255")
        assertTrue(added > 0)
        assertTrue(bookmarkRepository.isAyahBookmarked("2:255"))
        assertEquals(1, bookmarkRepository.getBookmarkCount())

        val toggledOff = bookmarkRepository.toggleAyahBookmark("2:255")
        assertFalse(toggledOff)
        assertFalse(bookmarkRepository.isAyahBookmarked("2:255"))
        assertEquals(0, bookmarkRepository.getBookmarkCount())

        bookmarkRepository.addAyahBookmark("2:255")
        bookmarkRepository.addAyahBookmark("2:255")
        assertEquals(1, bookmarkRepository.getBookmarkCount())

        bookmarkRepository.removeAyahBookmark("2:255")
        assertFalse(bookmarkRepository.isAyahBookmarked("2:255"))
        assertEquals(0, bookmarkRepository.getBookmarkCount())
    }

    @Test
    fun pageBookmarkUsesPageNumberIdentityAndPersists() = runTest {
        val added = bookmarkRepository.addPageBookmark(1, PageReferenceType.UTHMANI)
        assertTrue(added > 0)
        assertTrue(bookmarkRepository.isPageBookmarked(1, PageReferenceType.UTHMANI))

        val persisted = bookmarkRepository.getAllBookmarks().first()
        assertEquals(1, persisted.size)
        assertEquals(BookmarkType.PAGE, persisted.first().bookmarkType)
        assertEquals(1, persisted.first().pageNumber)
        assertEquals(PageReferenceType.UTHMANI, persisted.first().pageReferenceType)

        bookmarkRepository.removePageBookmark(1, PageReferenceType.UTHMANI)
        assertFalse(bookmarkRepository.isPageBookmarked(1, PageReferenceType.UTHMANI))
    }
}
