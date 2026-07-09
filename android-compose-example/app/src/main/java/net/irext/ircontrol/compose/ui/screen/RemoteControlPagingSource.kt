package net.irext.ircontrol.compose.ui.screen

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.irext.ircontrol.compose.bean.RemoteControl
import net.irext.ircontrol.compose.data.RemoteControlRepository


/**
 * Filename:       RemoteControlPagingSource.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Loads saved remote controls from the local repository with paging.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
class RemoteControlPagingSource(
    private val repository: RemoteControlRepository = RemoteControlRepository(),
) : PagingSource<Int, RemoteControl>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RemoteControl> = try {
        val from = params.key ?: 0
        val count = params.loadSize
        val data = withContext(Dispatchers.IO) {
            repository.list(from, count)
        }

        LoadResult.Page(
            data = data,
            prevKey = if (from == 0) null else maxOf(0, from - count),
            nextKey = if (data.size < count) null else from + data.size,
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, RemoteControl>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { page ->
                page.prevKey?.plus(state.config.pageSize)
                    ?: page.nextKey?.minus(state.config.pageSize)
            }
        }
}
