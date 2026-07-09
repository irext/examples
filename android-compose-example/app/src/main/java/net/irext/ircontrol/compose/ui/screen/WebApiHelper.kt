package net.irext.ircontrol.compose.ui.screen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.irext.webapi.WebAPIs
import net.irext.webapi.WebAPICallbacks
import net.irext.webapi.model.Brand
import net.irext.webapi.model.Category
import net.irext.webapi.model.City
import net.irext.webapi.model.RemoteIndex
import net.irext.webapi.model.StbOperator
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * Filename:       WebApiHelper.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Wraps web API callback calls as coroutine-friendly suspend functions.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
/**
 * Coroutine-friendly wrappers for WebAPIs callback-based methods.
 */
object WebApiHelper {

    suspend fun listCategories(api: WebAPIs, from: Int = 0, count: Int = 20): List<Category> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.listCategories(from, count, object : WebAPICallbacks.ListCategoriesCallback {
                    override fun onListCategoriesSuccess(categories: List<Category>?) {
                        cont.resume(categories ?: emptyList())
                    }
                    override fun onListCategoriesFailed() {
                        cont.resume(emptyList())
                    }
                    override fun onListCategoriesError() {
                        cont.resume(emptyList())
                    }
                })
            }
        }
    }

    suspend fun listBrands(
        api: WebAPIs,
        categoryId: Int,
        from: Int = 0,
        count: Int = 20,
    ): List<Brand> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.listBrands(categoryId, from, count, object : WebAPICallbacks.ListBrandsCallback {
                    override fun onListBrandsSuccess(brands: List<Brand>?) {
                        cont.resume(brands ?: emptyList())
                    }
                    override fun onListBrandsFailed() {
                        cont.resume(emptyList())
                    }
                    override fun onListBrandsError() {
                        cont.resume(emptyList())
                    }
                })
            }
        }
    }

    suspend fun listProvinces(api: WebAPIs): List<City> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.listProvinces(object : WebAPICallbacks.ListProvincesCallback {
                    override fun onListProvincesSuccess(provinces: List<City>?) {
                        cont.resume(provinces ?: emptyList())
                    }
                    override fun onListProvincesFailed() {
                        cont.resume(emptyList())
                    }
                    override fun onListProvincesError() {
                        cont.resume(emptyList())
                    }
                })
            }
        }
    }

    suspend fun listCities(api: WebAPIs, prefix: String): List<City> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.listCities(prefix, object : WebAPICallbacks.ListCitiesCallback {
                    override fun onListCitiesSuccess(cities: List<City>?) {
                        cont.resume(cities ?: emptyList())
                    }
                    override fun onListCitiesFailed() {
                        cont.resume(emptyList())
                    }
                    override fun onListCitiesError() {
                        cont.resume(emptyList())
                    }
                })
            }
        }
    }

    suspend fun listOperators(
        api: WebAPIs,
        cityCode: String,
        from: Int = 0,
        count: Int = 20,
    ): List<StbOperator> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.listOperators(cityCode, from, count, object : WebAPICallbacks.ListOperatersCallback {
                    override fun onListOperatorsSuccess(operators: List<StbOperator>?) {
                        cont.resume(operators ?: emptyList())
                    }
                    override fun onListOperatorsFailed() {
                        cont.resume(emptyList())
                    }
                    override fun onListOperatorsError() {
                        cont.resume(emptyList())
                    }
                })
            }
        }
    }

    suspend fun listRemoteIndexes(
        api: WebAPIs,
        categoryId: Int,
        brandId: Int,
        cityCode: String,
        operatorId: String,
        withParaData: Int = 0,
        from: Int = 0,
        count: Int = 20,
    ): List<RemoteIndex> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.listRemoteIndexes(
                    categoryId, brandId, cityCode, operatorId, withParaData, from, count,
                    object : WebAPICallbacks.ListIndexesCallback {
                        override fun onListIndexesSuccess(indexes: List<RemoteIndex>?) {
                            cont.resume(indexes ?: emptyList())
                        }
                        override fun onListIndexesFailed() {
                            cont.resume(emptyList())
                        }
                        override fun onListIndexesError() {
                            cont.resume(emptyList())
                        }
                    }
                )
            }
        }
    }

    suspend fun downloadBin(api: WebAPIs, remoteMap: String, indexId: Int): InputStream {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                api.downloadBin(remoteMap, indexId,
                    object : WebAPICallbacks.DownloadBinCallback {
                        override fun onDownloadBinSuccess(inputStream: InputStream?) {
                            if (inputStream != null) cont.resume(inputStream)
                            else cont.resumeWithException(Exception("downloadBin returned null"))
                        }
                        override fun onDownloadBinFailed() {
                            cont.resumeWithException(Exception("downloadBin failed"))
                        }
                        override fun onDownloadBinError() {
                            cont.resumeWithException(Exception("downloadBin error"))
                        }
                    }
                )
            }
        }
    }
}
