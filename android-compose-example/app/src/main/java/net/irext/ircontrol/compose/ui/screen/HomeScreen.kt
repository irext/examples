package net.irext.ircontrol.compose.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.irext.decode.sdk.utils.Constants
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.data.RemoteControlRepository
import net.irext.ircontrol.compose.ui.composable.ItemSingleText

/**
 * Filename:       HomeScreen.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Displays saved remote controls and management actions.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategory: () -> Unit,
    onRemoteClick: (Long) -> Unit,
    onDeleteRemote: ((Long) -> Unit)? = null,
    onMoveRemoteUp: ((Long) -> Unit)? = null,
    onMoveRemoteDown: ((Long) -> Unit)? = null
) {
    var isEditMode by remember { mutableStateOf(false) }

    val remoteFlow = remember {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { RemoteControlPagingSource() },
        ).flow
    }
    val remotes = remoteFlow.collectAsLazyPagingItems()

    val scope = rememberCoroutineScope()
    val repository = remember { RemoteControlRepository() }

    val actualDeleteRemote: (Long) -> Unit = onDeleteRemote ?: { id ->
        scope.launch {
            withContext(Dispatchers.IO) {
                repository.delete(id)
            }
            remotes.refresh()
        }
    }

    val actualMoveRemoteUp: (Long) -> Unit = onMoveRemoteUp ?: { id ->
        scope.launch {
            withContext(Dispatchers.IO) {
                repository.moveUp(id)
            }
            remotes.refresh()
        }
    }

    val actualMoveRemoteDown: (Long) -> Unit = onMoveRemoteDown ?: { id ->
        scope.launch {
            withContext(Dispatchers.IO) {
                repository.moveDown(id)
            }
            remotes.refresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                colors = TopAppBarDefaults.topAppBarColors(),
                actions = {
                    TextButton(onClick = { isEditMode = !isEditMode }) {
                        Text(
                            if (isEditMode) {
                                stringResource(R.string.button_done)
                            } else {
                                stringResource(R.string.button_manage)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isEditMode) {
                FloatingActionButton(onClick = onNavigateToCategory) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.content_description_add)
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(
                count = remotes.itemCount,
                key = { index -> remotes[index]?.id ?: index }
            ) { index ->
                remotes[index]?.let { remote ->
                    val displayName = if (remote.categoryId != Constants.CategoryID.STB.value) {
                        "${remote.categoryName}-${remote.brandName}"
                    } else {
                        "${remote.cityName}-${remote.operatorName}"
                    }

                    if (isEditMode) {
                        RemoteItemRow(
                            displayName = displayName,
                            isEditMode = true,
                            onRemoteClick = { onRemoteClick(remote.id) },
                            onMoveUp = { actualMoveRemoteUp(remote.id) },
                            onMoveDown = { actualMoveRemoteDown(remote.id) },
                            onDelete = { actualDeleteRemote(remote.id) }
                        )
                    } else {
                        SwipeToDeleteWrapper(
                            key = remote.id,
                            onDelete = { actualDeleteRemote(remote.id) }
                        ) {
                            RemoteItemRow(
                                displayName = displayName,
                                isEditMode = false,
                                onRemoteClick = { onRemoteClick(remote.id) },
                                onMoveUp = {},
                                onMoveDown = {},
                                onDelete = {}
                            )
                        }
                    }
                }
            }

            when (val appendState = remotes.loadState.append) {
                is LoadState.Loading -> item { LoadingMoreItem() }
                is LoadState.Error -> item {
                    LoadErrorItem(
                        message = appendState.error.message ?: stringResource(R.string.load_more_remotes_failed),
                        onRetry = { remotes.retry() },
                    )
                }
                else -> Unit
            }
        }

        when (val refreshState = remotes.loadState.refresh) {
            is LoadState.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is LoadState.Error -> FullScreenError(
                message = refreshState.error.message ?: stringResource(R.string.remote_load_failed),
                onRetry = { remotes.retry() },
                modifier = Modifier.padding(padding),
            )
            is LoadState.NotLoading -> {
                if (remotes.itemCount == 0) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.create_new),
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoteItemRow(
    displayName: String,
    isEditMode: Boolean,
    onRemoteClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = !isEditMode) { onRemoteClick() }
        ) {
            ItemSingleText(
                text = displayName,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isEditMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                IconButton(onClick = onMoveUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = stringResource(R.string.content_description_move_up),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onMoveDown) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = stringResource(R.string.content_description_move_down),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.content_description_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteWrapper(
    key: Any,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    key(key) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
                    true
                } else {
                    false
                }
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                val isDismissing = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
                val color = if (isDismissing) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (isDismissing) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.content_description_delete),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            },
            content = {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    content()
                }
            }
        )
    }
}
