package net.irext.ircontrol.compose.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.IRApplication
import net.irext.ircontrol.compose.ui.composable.ItemSingleText
import net.irext.webapi.model.Category


/**
 * Filename:       CategoryScreen.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Displays available remote device categories.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onCategoryClick: (Category) -> Unit,
    onBack: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as IRApplication
    val categoryFlow = remember(app.mWeAPIs) {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { CategoryPagingSource(app.mWeAPIs) },
        ).flow
    }
    val categories = categoryFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.category_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(count = categories.itemCount) { index ->
                categories[index]?.let { category ->
                    ItemSingleText(
                        text = category.name,
                        modifier = Modifier.clickable { onCategoryClick(category) }
                    )
                }
            }

            when (val appendState = categories.loadState.append) {
                is LoadState.Loading -> item { LoadingMoreItem() }
                is LoadState.Error -> item {
                    LoadErrorItem(
                        message = appendState.error.message ?: stringResource(R.string.load_more_categories_failed),
                        onRetry = { categories.retry() },
                    )
                }
                else -> Unit
            }
        }

        when (val refreshState = categories.loadState.refresh) {
            is LoadState.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is LoadState.Error -> FullScreenError(
                message = refreshState.error.message ?: stringResource(R.string.category_load_failed),
                onRetry = { categories.retry() },
                modifier = Modifier.padding(padding),
            )
            else -> Unit
        }
    }
}
