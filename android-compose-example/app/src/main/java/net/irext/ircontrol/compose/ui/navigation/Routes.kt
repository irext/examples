package net.irext.ircontrol.compose.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


/**
 * Filename:       Routes.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Declares navigation route keys and parameters.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
@Serializable
data object RouteHome : NavKey

@Serializable
data object RouteCategory : NavKey

@Serializable
data class RouteBrand(
    val categoryId: Int,
    val categoryName: String,
) : NavKey

/** Province selection - carries category info through STB flow */
@Serializable
data class RouteProvince(
    val categoryId: Int,
    val categoryName: String,
) : NavKey

@Serializable
data class RouteCity(
    val categoryId: Int,
    val categoryName: String,
    val provinceCode: String,
) : NavKey

@Serializable
data class RouteOperator(
    val categoryId: Int,
    val categoryName: String,
    val cityCode: String,
    val cityName: String,
) : NavKey

@Serializable
data class RouteIndex(
    val categoryId: Int,
    val categoryName: String,
    val brandId: Int,
    val brandName: String,
    val cityCode: String,
    val cityName: String,
    val operatorId: String,
    val operatorName: String,
) : NavKey

@Serializable
data class RouteControl(
    val remoteId: Long,
) : NavKey

@Serializable
data class RouteTest(
    val categoryId: Int,
    val categoryName: String,
    val brandId: Int,
    val brandName: String,
    val cityCode: String,
    val cityName: String,
    val operatorId: String,
    val operatorName: String,
) : NavKey
