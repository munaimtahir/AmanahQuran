package org.amanahquran.app.feature.bookmarks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.amanahquran.app.core.ui.AmanahSectionHeader
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import org.amanahquran.app.core.model.ScriptType
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.QuranFonts
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahEmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onNavigateBack: () -> Unit,
    onOpenBookmark: (BookmarkUiItem) -> Unit,
    onOpenReadingPosition: (ayahKey: String) -> Unit = {},
    viewModel: BookmarksViewModel = viewModel(factory = BookmarksViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding
    var showCreateCollection by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.items.isEmpty() && uiState.readingPosition == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    AmanahEmptyState(
                        icon = Icons.Rounded.BookmarkBorder,
                        title = "No bookmarks yet",
                        message = "Tap an ayah in the reader and choose Bookmark to save it here. Your reading position is saved automatically and shown separately.",
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = AmanahSpacing.sm),
                ) {
                    uiState.readingPosition?.let { position ->
                        item(key = "reading-position") {
                            AmanahSectionHeader(title = "Reading position")
                            ReadingPositionRow(
                                item = position,
                                onOpen = { onOpenReadingPosition(position.ayahKey) },
                            )
                            AmanahDivider(modifier = Modifier.padding(bottom = AmanahSpacing.md))
                        }
                    }
                    item(key = "saved-header") {
                        AmanahSectionHeader(title = "Saved bookmarks")
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = AmanahSpacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
                        ) {
                            uiState.collections.forEach { collection ->
                                FilterChip(
                                    selected = collection.id == uiState.selectedCollectionId,
                                    onClick = { viewModel.selectCollection(collection.id) },
                                    label = { Text(collection.name) },
                                )
                            }
                            TextButton(onClick = { showCreateCollection = true }) { Text("New") }
                        }
                    }
                    if (uiState.items.isEmpty()) {
                        item(key = "saved-empty") {
                            Text(
                                text = "No bookmarks saved yet. Tap an ayah in the reader and choose Bookmark.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = AmanahSpacing.md),
                            )
                        }
                    }
                    items(uiState.items, key = { it.record.id }) { item ->
                        BookmarkRow(
                            item = item,
                            scriptType = uiState.selectedScript,
                            collections = uiState.collections.filterNot { it.isDefault },
                            onOpen = { onOpenBookmark(item) },
                            onRemove = { viewModel.removeBookmark(item.record) },
                            onToggleCollection = { collectionId, shouldBeIn ->
                                viewModel.setBookmarkInCollection(item.record.id, collectionId, shouldBeIn)
                            },
                        )
                        AmanahDivider()
                    }
                }
            }
        }
    }

    if (showCreateCollection) {
        AlertDialog(
            onDismissRequest = { showCreateCollection = false },
            title = { Text("New bookmark collection") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it.take(80) },
                    label = { Text("Collection name") },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createCollection(newCollectionName)
                    newCollectionName = ""
                    showCreateCollection = false
                }, enabled = newCollectionName.isNotBlank()) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreateCollection = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun ReadingPositionRow(item: ReadingPositionUiItem, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Continue reading at ${item.title}", onClick = onOpen)
            .padding(vertical = AmanahSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BookmarkRow(
    item: BookmarkUiItem,
    scriptType: ScriptType,
    collections: List<org.amanahquran.app.core.repository.BookmarkCollection>,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onToggleCollection: (collectionId: String, shouldBeIn: Boolean) -> Unit,
) {
    var showCollectionPicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Open ${item.title}", onClick = onOpen)
            .padding(vertical = AmanahSpacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
    ) {
        Icon(
            imageVector = Icons.Rounded.Bookmark,
            contentDescription = null,
            tint = AmanahGoldMuted,
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.title} ${item.subtitle}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = item.createdLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item.previewText?.takeIf { it.isNotBlank() }?.let { preview ->
                // Quran preview laid out RTL so wrapping and the ellipsis follow Arabic reading order.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = preview,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            lineHeight = 36.sp,
                            fontFamily = QuranFonts.getFontFamily(scriptType),
                            letterSpacing = 0.sp,
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (collections.isNotEmpty()) {
            IconButton(
                onClick = { showCollectionPicker = true },
                modifier = Modifier.size(AmanahSpacing.minTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Rounded.CreateNewFolder,
                    contentDescription = "Add to collection",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(AmanahSpacing.minTouchTarget),
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Remove bookmark",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }

    if (showCollectionPicker) {
        AlertDialog(
            onDismissRequest = { showCollectionPicker = false },
            title = { Text("Add to collection") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                    collections.forEach { collection ->
                        val isIn = collection.bookmarkIds.contains(item.record.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleCollection(collection.id, !isIn) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = isIn,
                                onCheckedChange = { checked -> onToggleCollection(collection.id, checked) },
                            )
                            Text(collection.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCollectionPicker = false }) { Text("Done") }
            },
        )
    }
}
