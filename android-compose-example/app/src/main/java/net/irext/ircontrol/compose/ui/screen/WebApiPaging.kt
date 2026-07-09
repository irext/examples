package net.irext.ircontrol.compose.ui.screen

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.irext.webapi.WebAPIs
import net.irext.webapi.model.Brand
import net.irext.webapi.model.Category
import net.irext.webapi.model.RemoteIndex
import net.irext.webapi.model.StbOperator


/**
 * Filename:       WebApiPaging.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Implements paging sources for web API category, brand, operator, and index data.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
private fun nextOffset(from: Int, count: Int, loaded: Int): Int? =
    if (loaded < count) null else from + loaded

class CategoryPagingSource(
    private val api: WebAPIs,
) : PagingSource<Int, Category>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Category> = try {
        val from = params.key ?: 0
        val count = params.loadSize
        val data = WebApiHelper.listCategories(api, from, count)
        LoadResult.Page(
            data = data,
            prevKey = if (from == 0) null else maxOf(0, from - count),
            nextKey = nextOffset(from, count, data.size),
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Category>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { page ->
                page.prevKey?.plus(state.config.pageSize)
                    ?: page.nextKey?.minus(state.config.pageSize)
            }
        }
}

class BrandPagingSource(
    private val api: WebAPIs,
    private val categoryId: Int,
) : PagingSource<Int, Brand>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Brand> = try {
        val from = params.key ?: 0
        val count = params.loadSize
        val data = WebApiHelper.listBrands(api, categoryId, from, count)
        LoadResult.Page(
            data = data,
            prevKey = if (from == 0) null else maxOf(0, from - count),
            nextKey = nextOffset(from, count, data.size),
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Brand>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { page ->
                page.prevKey?.plus(state.config.pageSize)
                    ?: page.nextKey?.minus(state.config.pageSize)
            }
        }
}

class OperatorPagingSource(
    private val api: WebAPIs,
    private val cityCode: String,
) : PagingSource<Int, StbOperator>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StbOperator> = try {
        val from = params.key ?: 0
        val count = params.loadSize
        val data = WebApiHelper.listOperators(api, cityCode, from, count)
        LoadResult.Page(
            data = data,
            prevKey = if (from == 0) null else maxOf(0, from - count),
            nextKey = nextOffset(from, count, data.size),
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, StbOperator>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { page ->
                page.prevKey?.plus(state.config.pageSize)
                    ?: page.nextKey?.minus(state.config.pageSize)
            }
        }
}

class RemoteIndexPagingSource(
    private val api: WebAPIs,
    private val categoryId: Int,
    private val brandId: Int,
    private val cityCode: String,
    private val operatorId: String,
    private val withParaData: Int = 0,
) : PagingSource<Int, RemoteIndex>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RemoteIndex> = try {
        val from = params.key ?: 0
        val count = params.loadSize
        val data = WebApiHelper.listRemoteIndexes(
            api = api,
            categoryId = categoryId,
            brandId = brandId,
            cityCode = cityCode,
            operatorId = operatorId,
            withParaData = withParaData,
            from = from,
            count = count,
        )
        LoadResult.Page(
            data = data,
            prevKey = if (from == 0) null else maxOf(0, from - count),
            nextKey = nextOffset(from, count, data.size),
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, RemoteIndex>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { page ->
                page.prevKey?.plus(state.config.pageSize)
                    ?: page.nextKey?.minus(state.config.pageSize)
            }
        }
}
