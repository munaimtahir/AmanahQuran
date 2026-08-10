package org.amanahquran.app.feature.reader

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.LastReadState
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.repository.bookmarkRepository
import org.amanahquran.app.core.repository.lastReadRepository
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.content.translation.TranslationRepository

class ReaderViewModel(
    private val repository: QuranContentRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val lastReadRepository: LastReadRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val translationRepository: TranslationRepository? = null,
    private val initialOpenMode: ReaderOpenMode,
    private val initialAyahKey: String? = null,
    private val initialAnchor: ReaderAnchor = initialOpenMode.toReaderAnchor(initialAyahKey),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ReaderUiState(
            openMode = initialOpenMode,
            anchor = initialAnchor,
            modeTitle = initialOpenMode.displayTitle(),
        ),
    )
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    private data class ReaderLoadResult(
        val surahNumber: Int,
        val surahName: String,
        val surahNameArabic: String,
        val ayahs: List<ReaderAyahUiModel>,
        val readerBlocks: List<ReaderStructuralItem>,
        val firstJuzNumber: Int,
    )

    init {
        ReaderPerfLogger.log("viewmodel_init_start")
        viewModelScope.launch(dispatcher) {
            val initialSettings = settingsRepository.settings.first()
            ReaderPerfLogger.log("viewmodel_init_settings_loaded")
            _uiState.update {
                it.copy(
                    selectedScript = initialSettings.selectedScript,
                    arabicFontSizeSp = initialSettings.arabicFontSizeSp,
                    elderModeEnabled = initialSettings.elderModeEnabled,
                    bookModeEnabled = initialSettings.bookModeEnabled,
                    translationEnabled = initialSettings.translationEnabled,
                    translationFontSizeSp = initialSettings.translationFontSizeSp,
                    arabicLineSpacingMultiplier = initialSettings.arabicLineSpacingMultiplier,
                    readerHorizontalPaddingDp = initialSettings.readerHorizontalPaddingDp,
                    zoomLevel = initialSettings.effectiveZoomLevel(),
                    autoScrollPace = initialSettings.autoScrollPace,
                    firstZoomHintShown = initialSettings.firstZoomHintShown,
                    pinchToResizeEnabled = initialSettings.pinchToResizeEnabled,
                    contentMode = initialSettings.readerContentMode,
                    translationZoomLevel = initialSettings.translationZoomLevel,
                    linkedZoomEnabled = initialSettings.linkedZoomEnabled,
                    readerHeaderFormat = initialSettings.readerHeaderFormat,
                    keepScreenAwakeEnabled = initialSettings.keepScreenAwakeEnabled,
                )
            }
            if (initialSettings.translationEnabled) loadTranslations()
            observeSettings()
            observeBookmarks()
            ReaderPerfLogger.log("viewmodel_init_load_open_mode")
            val resolvedAnchor = resolveAnchor(initialAnchor, initialSettings.selectedScript)
            loadOpenMode(
                openMode = resolvedAnchor.openMode,
                scriptType = initialSettings.selectedScript,
                selectedAyahKey = resolvedAnchor.selectedAyahKey,
                anchor = initialAnchor,
            )
        }
    }

    fun setArabicFontSize(arabicFontSizeSp: Float) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setArabicFontSize(arabicFontSizeSp)
        }
    }

    fun increaseZoom() {
        val current = _uiState.value
        viewModelScope.launch(dispatcher) {
            settingsRepository.increaseZoomLevel(current.selectedScript, current.elderModeEnabled)
        }
    }

    fun decreaseZoom() {
        val current = _uiState.value
        viewModelScope.launch(dispatcher) {
            settingsRepository.decreaseZoomLevel(current.selectedScript, current.elderModeEnabled)
        }
    }

    fun selectZoomLevel(level: ReaderZoomLevel) {
        val current = _uiState.value
        viewModelScope.launch(dispatcher) {
            settingsRepository.setZoomLevel(current.selectedScript, current.elderModeEnabled, level)
        }
    }

    fun resetZoom() {
        val current = _uiState.value
        viewModelScope.launch(dispatcher) {
            settingsRepository.resetZoomLevel(current.selectedScript, current.elderModeEnabled)
        }
    }

    fun setAutoScrollPace(pace: AutoScrollPace) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setAutoScrollPace(pace)
        }
    }

    /**
     * Content mode is purely a rendering choice -- the same [uiState] `ayahs`/`readerBlocks` this
     * ViewModel already loads are reused by both Ayah Mode and Continuous Mode, so switching never
     * re-queries the database or touches [loadOpenMode].
     */
    fun setContentMode(mode: ReaderContentMode) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setReaderContentMode(mode)
        }
    }

    fun setLinkedZoomEnabled(enabled: Boolean) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setLinkedZoomEnabled(enabled)
        }
    }

    fun setTranslationZoomLevel(level: ReaderZoomLevel) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setTranslationZoomLevel(level)
        }
    }

    fun increaseTranslationZoom() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.increaseTranslationZoomLevel()
        }
    }

    fun decreaseTranslationZoom() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.decreaseTranslationZoomLevel()
        }
    }

    fun resetTranslationZoom() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.resetTranslationZoomLevel()
        }
    }

    fun setFirstZoomHintShown(shown: Boolean) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setFirstZoomHintShown(shown)
        }
    }

    /**
     * Silently records the ayah currently under the reading position as last-read, without
     * forcing it into the "selected" highlighted state the way [selectAyah] does -- auto-scroll
     * passing through many ayahs on its way down the page should not visibly jump a highlight
     * from row to row, only persist where the user ended up when they paused/stopped/backgrounded.
     */
    fun updateReadingPosition(ayahKey: String) {
        val ayah = _uiState.value.ayahs.firstOrNull { it.ayahKey == ayahKey } ?: return
        persistLastRead(ayah, _uiState.value.selectedScript)
    }

    fun selectAyah(ayahKey: String) {
        val current = _uiState.value
        val selectedAyah = current.ayahs.firstOrNull { it.ayahKey == ayahKey } ?: return
        _uiState.update {
            it.copy(
                selectedAyahKey = ayahKey,
                ayahs = updateAyahSelection(it.ayahs, ayahKey),
                readerBlocks = updateReaderBlocks(it.readerBlocks, ayahKey),
                anchorScrollIndex = it.readerBlocks.indexOfAyah(ayahKey),
                anchorScrollRequestId = it.anchorScrollRequestId + 1,
            )
        }
        persistLastRead(selectedAyah, current.selectedScript)
    }

    fun clearSelectedAyah() {
        _uiState.update {
            it.copy(
                selectedAyahKey = null,
                ayahs = updateAyahSelection(it.ayahs, null),
                readerBlocks = updateReaderBlocks(it.readerBlocks, null),
            )
        }
    }

    fun selectAdjacentAyah(direction: Int) {
        val current = _uiState.value
        val currentIndex = current.ayahs.indexOfFirst { it.ayahKey == current.selectedAyahKey }
        val nextIndex = (if (currentIndex < 0) 0 else currentIndex + direction)
            .coerceIn(0, current.ayahs.lastIndex)
        current.ayahs.getOrNull(nextIndex)?.let { selectAyah(it.ayahKey) }
    }

    fun toggleBookmark(ayahKey: String) {
        viewModelScope.launch(dispatcher) {
            bookmarkRepository.toggleAyahBookmark(ayahKey)
        }
    }

    fun toggleCurrentPageBookmark() {
        val openMode = _uiState.value.openMode
        if (openMode !is ReaderOpenMode.Page) return
        viewModelScope.launch(dispatcher) {
            bookmarkRepository.togglePageBookmark(openMode.pageNumber, openMode.pageReferenceType)
        }
    }

    fun loadOpenMode(
        openMode: ReaderOpenMode = _uiState.value.openMode,
        scriptType: ScriptType = _uiState.value.selectedScript,
        selectedAyahKey: String? = _uiState.value.selectedAyahKey,
        anchor: ReaderAnchor = _uiState.value.anchor,
    ) {
        val openStartedAtMs = SystemClock.elapsedRealtime()
        ReaderPerfLogger.log("route_to_reader", openStartedAtMs, "mode=$openMode script=${scriptType.name}")
        ReaderPerfLogger.log("reader_load_start", openStartedAtMs, "anchor=$anchor")
        loadJob?.cancel()
        loadJob = viewModelScope.launch(dispatcher) {
            var resolvedMode = openMode
            val targetPageRefType = if (scriptType == ScriptType.UTHMANI) PageReferenceType.UTHMANI else PageReferenceType.INDOPAK
            // READER-UX-02: a bare Surah/Juz open (Surah Index, Juz Index, or "Continue Reading"
            // at Surah/Juz granularity -- anchor is SurahStart/JuzStart, no specific ayah) always
            // stays in the user's list-based reading mode (Ayah or Continuous) and never silently
            // redirects into the Page Mode pager, even when Page Mode is globally enabled -- that
            // redirect used to make "open Surah 1" actually open "Page 1", which legitimately also
            // contains the start of Surah 2 on a real Mushaf page, and looked like a bug ("opening
            // Surah 1 shows Surah 2's first ayah"). Jumping to one *specific* ayah (bookmark/search
            // navigation, anchor is ExactAyah, which also resolves through ReaderOpenMode.Surah --
            // see resolveAnchor below) still opens that ayah's real page in Page Mode as before,
            // since there the pager's per-page fidelity is the whole point, not a surprise.
            if (_uiState.value.bookModeEnabled) {
                if (openMode is ReaderOpenMode.Surah && anchor is ReaderAnchor.ExactAyah) {
                    val targetAyahKey = selectedAyahKey ?: anchor.ayahKey
                    val page = repository.getPageForAyah(targetAyahKey, targetPageRefType) ?: 1
                    resolvedMode = ReaderOpenMode.Page(page, targetPageRefType)
                } else if (openMode is ReaderOpenMode.Juz && anchor is ReaderAnchor.ExactAyah) {
                    val targetAyahKey = selectedAyahKey ?: anchor.ayahKey
                    val page = repository.getPageForAyah(targetAyahKey, targetPageRefType) ?: 1
                    resolvedMode = ReaderOpenMode.Page(page, targetPageRefType)
                } else if (openMode is ReaderOpenMode.Page) {
                    if (openMode.pageReferenceType != targetPageRefType) {
                        val firstAyahKey = repository.getFirstAyahForPage(openMode.pageNumber, openMode.pageReferenceType)
                        val page = firstAyahKey?.let { repository.getPageForAyah(it, targetPageRefType) } ?: 1
                        resolvedMode = ReaderOpenMode.Page(page, targetPageRefType)
                    }
                } else if (openMode is ReaderOpenMode.AyahTarget) {
                    val page = repository.getPageForAyah(openMode.ayahKey, targetPageRefType) ?: 1
                    resolvedMode = ReaderOpenMode.Page(page, targetPageRefType)
                }
            }

            val cachedBlocks = ReaderBlocksCache.get(resolvedMode, scriptType)
            if (cachedBlocks != null) {
                ReaderPerfLogger.log("cache_hit", openStartedAtMs, "mode=$resolvedMode blocks=${cachedBlocks.size}")
                val cachedAyahs = cachedBlocks.mapNotNull { (it as? ReaderStructuralItem.Ayah)?.ayah }
                val firstAyah = cachedAyahs.firstOrNull()
                val selectedKey = selectedAyahKey ?: firstAyah?.ayahKey
                val currentState = _uiState.value
                val pageTitle = if (resolvedMode is ReaderOpenMode.Page) resolvedMode.displayTitle() else firstAyah?.surahNameSimple
                val pageSurahName = firstAyah?.surahNameSimple?.ifBlank { "Surah ${firstAyah.surahNumber}" } ?: currentState.pageSurahName
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedScript = scriptType,
                        openMode = resolvedMode,
                        anchor = anchor,
                        modeTitle = resolvedMode.displayTitle(),
                        readerOpenStartedAtMs = openStartedAtMs,
                        surahNumber = firstAyah?.surahNumber ?: it.surahNumber,
                        surahName = pageTitle?.ifBlank { it.surahName } ?: it.surahName,
                        pageSurahName = pageSurahName,
                        surahNameArabic = firstAyah?.surahNameArabic.orEmpty(),
                        juzNumber = firstAyah?.juzNumber ?: it.juzNumber,
                        ayahs = updateAyahs(cachedAyahs, selectedKey),
                        readerBlocks = updateReaderBlocks(cachedBlocks, selectedKey),
                        selectedAyahKey = selectedKey,
                        anchorScrollIndex = cachedBlocks.indexOfAyah(selectedKey),
                        anchorScrollRequestId = it.anchorScrollRequestId + 1,
                        errorMessage = null,
                    )
                }
                ReaderPerfLogger.log("ui_state_emitted", openStartedAtMs, "mode=$resolvedMode cache=true")
                persistLastRead(cachedAyahs.firstOrNull { it.ayahKey == selectedKey }, scriptType)
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedScript = scriptType,
                    openMode = resolvedMode,
                    anchor = anchor,
                    modeTitle = resolvedMode.displayTitle(),
                    errorMessage = null,
                    readerOpenStartedAtMs = openStartedAtMs,
                )
            }
            ReaderPerfLogger.log("ui_state_emitted", openStartedAtMs, "mode=$resolvedMode loading=true")

            runCatching {
                ReaderPerfLogger.log("db_query_start", openStartedAtMs, "mode=$resolvedMode script=${scriptType.name}")
                val rawAyahs = repository.getReaderAyahs(resolvedMode, scriptType.name)
                ReaderPerfLogger.log("db_query_returned", openStartedAtMs, "rows=${rawAyahs.size}")
                ReaderPerfLogger.log("repository_mapping_start", openStartedAtMs, "mode=$resolvedMode")
                ReaderPerfLogger.log("block_build_start", openStartedAtMs, "mode=$resolvedMode")
                val ayahs = rawAyahs.map { ayah ->
                    ReaderAyahUiModel(
                        ayahKey = ayah.ayahKey,
                        surahNumber = ayah.surahNumber,
                        ayahNumber = ayah.ayahNumber,
                        juzNumber = ayah.juzNumber,
                        pageNumber = ayah.pageNumber,
                        displayText = ayah.displayText,
                        scriptType = scriptType,
                        surahNameArabic = ayah.surahNameArabic,
                        surahNameSimple = ayah.surahNameSimple,
                        surahAyahCount = ayah.surahAyahCount,
                    )
                }
                val leadingJuzHeader = ayahs.firstOrNull()?.let { firstAyah ->
                    val firstAyahForJuz = repository.getFirstAyahForJuz(firstAyah.juzNumber)
                    firstAyahForJuz == firstAyah.ayahKey
                } ?: false
                val readerBlocks = buildReaderStructuralItems(
                    ayahs = ayahs,
                    openMode = resolvedMode,
                    showLeadingJuzHeader = leadingJuzHeader,
                    // Only ever fires on an actual page-number transition inside the loaded ayah
                    // list; a single ReaderOpenMode.Page load never contains more than one page,
                    // so this is inert there and only draws dividers in continuous Surah/Juz reading.
                    showPageDividers = true,
                )
                val firstJuzNumber = rawAyahs.firstOrNull()?.juzNumber ?: 1
                val firstAyah = ayahs.firstOrNull()
                val surahNumber = firstAyah?.surahNumber ?: when (resolvedMode) {
                    is ReaderOpenMode.Surah -> resolvedMode.surahNumber
                    is ReaderOpenMode.Page -> 1
                    is ReaderOpenMode.Juz -> 1
                    is ReaderOpenMode.AyahTarget -> resolvedMode.surahNumber
                }
                val surahName = if (resolvedMode is ReaderOpenMode.Page) {
                    resolvedMode.displayTitle()
                } else {
                    firstAyah?.surahNameSimple?.ifBlank { "Surah $surahNumber" } ?: "Surah $surahNumber"
                }
                ReaderLoadResult(
                    surahNumber = surahNumber,
                    surahName = surahName,
                    surahNameArabic = firstAyah?.surahNameArabic.orEmpty(),
                    ayahs = ayahs,
                    readerBlocks = readerBlocks,
                    firstJuzNumber = firstJuzNumber,
                )
            }.onSuccess { (surahNumber, surahName, surahNameArabic, ayahs, readerBlocks, firstJuzNumber) ->
                ReaderPerfLogger.log("repository_mapping_end", openStartedAtMs, "blocks=${readerBlocks.size}")
                ReaderPerfLogger.log("block_build_end", openStartedAtMs, "blocks=${readerBlocks.size}")
                ReaderBlocksCache.put(resolvedMode, scriptType, readerBlocks)
                val selectedKey = selectedAyahKey ?: ayahs.firstOrNull()?.ayahKey
                val pageSurahName = ayahs.firstOrNull()?.surahNameSimple?.ifBlank { "Surah $surahNumber" } ?: surahName
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        surahNumber = surahNumber,
                        surahName = surahName,
                        pageSurahName = pageSurahName,
                        surahNameArabic = surahNameArabic,
                        juzNumber = firstJuzNumber,
                        ayahs = updateAyahs(ayahs, selectedKey),
                        readerBlocks = updateReaderBlocks(readerBlocks, selectedKey),
                        selectedAyahKey = selectedKey,
                        anchorScrollIndex = readerBlocks.indexOfAyah(selectedKey),
                        anchorScrollRequestId = it.anchorScrollRequestId + 1,
                        readerOpenStartedAtMs = openStartedAtMs,
                        errorMessage = null,
                    )
                }
                ReaderPerfLogger.log("ui_state_emitted", openStartedAtMs, "mode=$resolvedMode loading=false")
                persistLastRead(ayahs.firstOrNull { it.ayahKey == selectedKey }, scriptType)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ayahs = emptyList(),
                        readerBlocks = emptyList(),
                        readerOpenStartedAtMs = openStartedAtMs,
                        errorMessage = throwable.message ?: "Unable to load reader content.",
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.settings.collectLatest { settings ->
                val current = _uiState.value
                val scriptChanged = current.selectedScript != settings.selectedScript
                val bookModeChanged = current.bookModeEnabled != settings.bookModeEnabled
                _uiState.update {
                    it.copy(
                        selectedScript = settings.selectedScript,
                        arabicFontSizeSp = settings.arabicFontSizeSp,
                        elderModeEnabled = settings.elderModeEnabled,
                        bookModeEnabled = settings.bookModeEnabled,
                        translationEnabled = settings.translationEnabled,
                        translationFontSizeSp = settings.translationFontSizeSp,
                        arabicLineSpacingMultiplier = settings.arabicLineSpacingMultiplier,
                        readerHorizontalPaddingDp = settings.readerHorizontalPaddingDp,
                        zoomLevel = settings.effectiveZoomLevel(),
                        autoScrollPace = settings.autoScrollPace,
                        firstZoomHintShown = settings.firstZoomHintShown,
                        pinchToResizeEnabled = settings.pinchToResizeEnabled,
                        contentMode = settings.readerContentMode,
                        translationZoomLevel = settings.translationZoomLevel,
                        linkedZoomEnabled = settings.linkedZoomEnabled,
                        readerHeaderFormat = settings.readerHeaderFormat,
                        keepScreenAwakeEnabled = settings.keepScreenAwakeEnabled,
                    )
                }
                if (settings.translationEnabled && !current.translationEnabled) loadTranslations()
                if (!settings.translationEnabled && current.translationEnabled) {
                    _uiState.update { it.copy(translations = emptyMap()) }
                }
                if (scriptChanged || bookModeChanged) {
                    val nextAnchor = when (val currentAnchor = current.anchor) {
                        is ReaderAnchor.PageStart -> ReaderAnchor.PageStart(
                            pageNumber = currentAnchor.pageNumber,
                            pageReferenceType = settings.selectedScript.toPageReferenceType(),
                        )
                        is ReaderAnchor.ExactAyah -> currentAnchor
                        is ReaderAnchor.SurahStart,
                        is ReaderAnchor.JuzStart -> currentAnchor
                    }
                    val resolvedAnchor = resolveAnchor(nextAnchor, settings.selectedScript)
                    loadOpenMode(
                        openMode = resolvedAnchor.openMode,
                        scriptType = settings.selectedScript,
                        selectedAyahKey = current.selectedAyahKey ?: resolvedAnchor.selectedAyahKey,
                        anchor = nextAnchor,
                    )
                }
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch(dispatcher) {
            bookmarkRepository.getAllBookmarks().collectLatest { bookmarks ->
                val bookmarkedAyahKeys = bookmarks.mapNotNull { it.ayahKey }.toSet()
                val currentPage = _uiState.value.openMode as? ReaderOpenMode.Page
                _uiState.update { state ->
                    state.copy(
                        ayahs = state.ayahs.map { ayah ->
                            ayah.copy(
                                isBookmarked = ayah.ayahKey in bookmarkedAyahKeys,
                                isSelected = state.selectedAyahKey == ayah.ayahKey,
                            )
                        },
                        readerBlocks = state.readerBlocks.map { block ->
                            if (block is ReaderStructuralItem.Ayah) {
                                block.copy(
                                    ayah = block.ayah.copy(
                                        isBookmarked = block.ayah.ayahKey in bookmarkedAyahKeys,
                                        isSelected = state.selectedAyahKey == block.ayah.ayahKey,
                                    )
                                )
                            } else {
                                block
                            }
                        },
                        isPageBookmarked = currentPage?.let {
                            bookmarks.any { record ->
                                record.bookmarkType == BookmarkType.PAGE &&
                                    record.pageNumber == it.pageNumber &&
                                    record.pageReferenceType == it.pageReferenceType
                            }
                        } ?: false,
                    )
                }
            }
        }
    }

    private fun loadTranslations() {
        viewModelScope.launch(dispatcher) {
            val translations = translationRepository?.observeAll()?.first().orEmpty()
                .associate { it.ayahKey to it.displayText }
            _uiState.update { it.copy(translations = translations) }
        }
    }

    private fun persistLastRead(
        selectedAyah: ReaderAyahUiModel?,
        scriptType: ScriptType,
    ) {
        if (selectedAyah == null) return
        viewModelScope.launch(dispatcher) {
            lastReadRepository.saveLastRead(
                LastReadState(
                    ayahKey = selectedAyah.ayahKey,
                    surahNumber = selectedAyah.surahNumber,
                    ayahNumber = selectedAyah.ayahNumber,
                    pageNumber = selectedAyah.pageNumber,
                    juzNumber = selectedAyah.juzNumber,
                    scriptType = scriptType,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    private fun updateAyahs(
        ayahs: List<ReaderAyahUiModel>,
        selectedAyahKey: String?,
    ): List<ReaderAyahUiModel> {
        return ayahs.map { ayah ->
            ayah.copy(
                isSelected = selectedAyahKey == ayah.ayahKey,
                isBookmarked = _uiState.value.ayahs.firstOrNull { it.ayahKey == ayah.ayahKey }?.isBookmarked == true,
            )
        }
    }

    private fun updateAyahSelection(
        ayahs: List<ReaderAyahUiModel>,
        selectedAyahKey: String?,
    ): List<ReaderAyahUiModel> {
        return ayahs.map { ayah ->
            ayah.copy(isSelected = selectedAyahKey == ayah.ayahKey)
        }
    }

    private fun updateReaderBlocks(
        blocks: List<ReaderStructuralItem>,
        selectedAyahKey: String?,
    ): List<ReaderStructuralItem> {
        return blocks.map { block ->
            when (block) {
                is ReaderStructuralItem.Ayah -> block.copy(
                    ayah = block.ayah.copy(
                        isSelected = selectedAyahKey == block.ayah.ayahKey,
                        isBookmarked = _uiState.value.ayahs.firstOrNull { it.ayahKey == block.ayah.ayahKey }?.isBookmarked == true,
                    )
                )
                else -> block
            }
        }
    }

    private fun ReaderOpenMode.displayTitle(): String {
        return when (this) {
            is ReaderOpenMode.Surah -> "Surah $surahNumber"
            is ReaderOpenMode.Page -> "Page $pageNumber"
            is ReaderOpenMode.Juz -> "Juz $juzNumber"
            is ReaderOpenMode.AyahTarget -> "Surah $surahNumber"
        }
    }

    private data class ResolvedAnchor(
        val openMode: ReaderOpenMode,
        val selectedAyahKey: String?,
    )

    private suspend fun resolveAnchor(
        anchor: ReaderAnchor,
        scriptType: ScriptType,
    ): ResolvedAnchor {
        return when (anchor) {
            is ReaderAnchor.SurahStart -> ResolvedAnchor(
                openMode = ReaderOpenMode.Surah(anchor.surahNumber),
                selectedAyahKey = null,
            )
            is ReaderAnchor.JuzStart -> ResolvedAnchor(
                openMode = ReaderOpenMode.Juz(anchor.juzNumber),
                selectedAyahKey = null,
            )
            is ReaderAnchor.PageStart -> ResolvedAnchor(
                openMode = ReaderOpenMode.Page(anchor.pageNumber, anchor.pageReferenceType),
                selectedAyahKey = null,
            )
            is ReaderAnchor.ExactAyah -> {
                val reference = repository.resolveAyahReference(anchor.ayahKey)
                    ?: error("Unknown canonical ayah ${anchor.ayahKey}")
                ReaderPerfLogger.log(
                    "anchor_resolved",
                    detail = "ayah=${reference.ayahKey} surah=${reference.surahNumber} page=${reference.pageNumber} juz=${reference.juzNumber} script=${scriptType.name}",
                )
                ResolvedAnchor(
                    // Keep the full surrounding reading context available. The
                    // selected ayah is used as the scroll/selection anchor; an
                    // AyahTarget would load only that one verse and make
                    // Continue Reading appear truncated.
                    openMode = ReaderOpenMode.Surah(reference.surahNumber),
                    selectedAyahKey = reference.ayahKey,
                )
            }
        }
    }

    companion object {
        fun factory(
            context: Context,
            surahNumber: Int,
            initialAyahKey: String? = null,
        ): ViewModelProvider.Factory = factory(
            context = context,
            openMode = ReaderOpenMode.Surah(surahNumber),
            initialAyahKey = initialAyahKey,
        )

        fun factory(
            context: Context,
            openMode: ReaderOpenMode,
            initialAyahKey: String? = null,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(
                    repository = quranContentRepository(context),
                    settingsRepository = readerSettingsRepository(context),
                    lastReadRepository = lastReadRepository(context),
                    bookmarkRepository = bookmarkRepository(context),
                    translationRepository = TranslationRepository(context),
                    initialOpenMode = openMode,
                    initialAyahKey = initialAyahKey,
                ) as T
            }
        }

        fun factory(
            context: Context,
            anchor: ReaderAnchor,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(
                    repository = quranContentRepository(context),
                    settingsRepository = readerSettingsRepository(context),
                    lastReadRepository = lastReadRepository(context),
                    bookmarkRepository = bookmarkRepository(context),
                    translationRepository = TranslationRepository(context),
                    initialOpenMode = anchor.defaultOpenMode(),
                    initialAnchor = anchor,
                ) as T
            }
        }
    }
}

private fun List<ReaderStructuralItem>.indexOfAyah(ayahKey: String?): Int? {
    if (ayahKey == null) return null
    return indexOfFirst { item ->
        item is ReaderStructuralItem.Ayah && item.ayah.ayahKey == ayahKey
    }.takeIf { it >= 0 }
}

private fun ReaderOpenMode.toReaderAnchor(initialAyahKey: String?): ReaderAnchor {
    if (!initialAyahKey.isNullOrBlank()) return ReaderAnchor.ExactAyah(initialAyahKey)
    return when (this) {
        is ReaderOpenMode.Surah -> ReaderAnchor.SurahStart(surahNumber)
        is ReaderOpenMode.Page -> ReaderAnchor.PageStart(pageNumber, pageReferenceType)
        is ReaderOpenMode.Juz -> ReaderAnchor.JuzStart(juzNumber)
        is ReaderOpenMode.AyahTarget -> ReaderAnchor.ExactAyah(ayahKey)
    }
}

private fun ReaderAnchor.defaultOpenMode(): ReaderOpenMode = when (this) {
    is ReaderAnchor.SurahStart -> ReaderOpenMode.Surah(surahNumber)
    is ReaderAnchor.PageStart -> ReaderOpenMode.Page(pageNumber, pageReferenceType)
    is ReaderAnchor.JuzStart -> ReaderOpenMode.Juz(juzNumber)
    is ReaderAnchor.ExactAyah -> ReaderOpenMode.AyahTarget(
        surahNumber = ayahKey.substringBefore(':').toIntOrNull() ?: 1,
        ayahKey = ayahKey,
    )
}

private fun ScriptType.toPageReferenceType(): PageReferenceType = when (this) {
    ScriptType.INDOPAK -> PageReferenceType.INDOPAK
    ScriptType.UTHMANI -> PageReferenceType.UTHMANI
}
