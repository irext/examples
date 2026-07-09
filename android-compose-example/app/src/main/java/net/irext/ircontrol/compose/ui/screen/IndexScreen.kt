package net.irext.ircontrol.compose.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.IRApplication
import net.irext.ircontrol.compose.bean.RemoteControl
import net.irext.ircontrol.compose.data.RemoteControlRepository
import net.irext.ircontrol.compose.ui.composable.ItemIndexText
import net.irext.ircontrol.compose.ui.navigation.RouteIndex
import net.irext.ircontrol.compose.utils.remoteBinFile
import net.irext.ircontrol.compose.utils.writeFrom
import net.irext.webapi.model.RemoteIndex


/**
 * Filename:       IndexScreen.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Displays remote indexes and saves the selected remote definition.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
private sealed class IndexActionState {
    data object Idle : IndexActionState()
    data class Downloading(val index: RemoteIndex) : IndexActionState()
    data object Saving : IndexActionState()
    data class Error(val message: String) : IndexActionState()
}

private const val TAG = "IndexScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexScreen(
    route: RouteIndex,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onTestClick: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as IRApplication
    val appContext = LocalContext.current.applicationContext
    val repository = remember { RemoteControlRepository() }
    var actionState by remember { mutableStateOf<IndexActionState>(IndexActionState.Idle) }
    val scope = rememberCoroutineScope()
    val indexFlow = remember(app.mWeAPIs, route) {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                RemoteIndexPagingSource(
                    api = app.mWeAPIs,
                    categoryId = route.categoryId,
                    brandId = route.brandId,
                    cityCode = route.cityCode,
                    operatorId = route.operatorId,
                )
            },
        ).flow
    }
    val indexes = indexFlow.collectAsLazyPagingItems()

    fun onIndexClick(index: RemoteIndex) {
        scope.launch {
            actionState = IndexActionState.Downloading(index)
            try {
                val stream = WebApiHelper.downloadBin(app.mWeAPIs, index.remoteMap, index.id)
                actionState = IndexActionState.Saving
                withContext(Dispatchers.IO) {
                    appContext.remoteBinFile(index.remoteMap).writeFrom(stream)
                }
                val rc = RemoteControl()
                rc.categoryId = index.categoryId
                rc.categoryName = route.categoryName
                rc.brandId = index.brandId
                rc.brandName = route.brandName
                rc.cityCode = index.cityCode
                rc.cityName = route.cityName
                rc.operatorId = index.operatorId
                rc.operatorName = route.operatorName
                rc.protocol = index.protocol
                rc.remote = index.remote
                rc.remoteMap = index.remoteMap
                rc.subCategory = index.subCate
                repository.save(rc)
                actionState = IndexActionState.Idle
                onSaved()
            } catch (e: Exception) {
                Log.e(TAG, "download error: ${e.message}", e)
                actionState = IndexActionState.Error(e.message ?: "Download failed")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(route.brandName.ifEmpty { route.cityName.ifEmpty { stringResource(R.string.index_title) } }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                    }
                },
                actions = {
                    IconButton(onClick = onTestClick) {
                        Icon(Icons.Filled.Science, contentDescription = stringResource(R.string.content_description_ir_test))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(count = indexes.itemCount) { indexPos ->
                indexes[indexPos]?.let { index ->
                    val nameText = if (route.brandName.isNotEmpty()) {
                        "${route.brandName} ${index.remoteMap}"
                    } else {
                        "${route.operatorName} ${index.remoteMap}"
                    }
                    ItemIndexText(
                        nameText = nameText,
                        mapText = index.remoteMap,
                        modifier = Modifier.clickable { onIndexClick(index) }
                    )
                }
            }

            when (val appendState = indexes.loadState.append) {
                is LoadState.Loading -> item { LoadingMoreItem() }
                is LoadState.Error -> item {
                    LoadErrorItem(
                        message = appendState.error.message ?: stringResource(R.string.load_more_remotes_failed),
                        onRetry = { indexes.retry() },
                    )
                }
                else -> Unit
            }
        }

        when (val refreshState = indexes.loadState.refresh) {
            is LoadState.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is LoadState.Error -> FullScreenError(
                message = refreshState.error.message ?: stringResource(R.string.remote_index_load_failed),
                onRetry = { indexes.retry() },
                modifier = Modifier.padding(padding),
            )
            else -> if (indexes.itemCount == 0) {
                FullScreenError(
                    message = stringResource(
                        R.string.no_remote_indexes_found_with_filters,
                        route.categoryId,
                        route.brandId,
                    ),
                    onRetry = { indexes.refresh() },
                    modifier = Modifier.padding(padding),
                )
            }
        }

        when (val s = actionState) {
            is IndexActionState.Downloading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.downloading_remote_index, s.index.remoteMap))
                    }
                }
            }
            is IndexActionState.Saving -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.saving))
                    }
                }
            }
            is IndexActionState.Error -> FullScreenError(
                message = s.message,
                onRetry = { actionState = IndexActionState.Idle },
                modifier = Modifier.padding(padding),
            )
            IndexActionState.Idle -> Unit
        }
    }
}
