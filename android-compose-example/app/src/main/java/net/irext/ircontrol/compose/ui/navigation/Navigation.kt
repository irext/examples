package net.irext.ircontrol.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import net.irext.ircontrol.compose.ui.screen.BrandScreen
import net.irext.ircontrol.compose.ui.screen.CategoryScreen
import net.irext.ircontrol.compose.ui.screen.CityScreen
import net.irext.ircontrol.compose.ui.screen.ControlScreen
import net.irext.ircontrol.compose.ui.screen.HomeScreen
import net.irext.ircontrol.compose.ui.screen.IndexScreen
import net.irext.ircontrol.compose.ui.screen.OperatorScreen
import net.irext.ircontrol.compose.ui.screen.ProvinceScreen
import net.irext.ircontrol.compose.ui.screen.TestScreen


/**
 * Filename:       Navigation.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Defines the Compose navigation flow for selecting and controlling remotes.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
fun buildEntryProvider(
    backStack: MutableList<Any>,
): (Any) -> NavEntry<Any> = { key ->
    when (key) {
        is RouteHome -> NavEntry(key) {
        HomeScreen(
            onNavigateToCategory = { backStack.add(RouteCategory) },
            onRemoteClick = { remoteId -> backStack.add(RouteControl(remoteId)) },
        )
    }
        is RouteCategory -> NavEntry(key) {
        CategoryScreen(
            onCategoryClick = { category ->
                if (net.irext.decode.sdk.utils.Constants.CategoryID.STB.value != category.id) {
                    backStack.add(RouteBrand(category.id, category.name))
                } else {
                    backStack.add(RouteProvince(category.id, category.name))
                }
            },
            onBack = { backStack.removeLastOrNull() },
        )
    }
        is RouteBrand -> NavEntry(key) {
        BrandScreen(
            categoryId = key.categoryId,
            categoryName = key.categoryName,
            onBrandClick = { brand ->
                backStack.add(
                    RouteIndex(
                        categoryId = key.categoryId,
                        categoryName = key.categoryName,
                        brandId = brand.id,
                        brandName = brand.name,
                        cityCode = "", cityName = "",
                        operatorId = "", operatorName = "",
                    )
                )
            },
            onBack = { backStack.removeLastOrNull() },
        )
    }
        is RouteProvince -> NavEntry(key) {
        ProvinceScreen(
            onProvinceClick = { province ->
                backStack.add(
                    RouteCity(
                        categoryId = key.categoryId,
                        categoryName = key.categoryName,
                        provinceCode = province.code.substring(0, 2),
                    )
                )
            },
            onBack = { backStack.removeLastOrNull() },
        )
    }
        is RouteCity -> NavEntry(key) {
        CityScreen(
            provinceCode = key.provinceCode,
            onCityClick = { city ->
                backStack.add(
                    RouteOperator(
                        categoryId = key.categoryId,
                        categoryName = key.categoryName,
                        cityCode = city.code,
                        cityName = city.name,
                    )
                )
            },
            onBack = { backStack.removeLastOrNull() },
        )
    }
        is RouteOperator -> NavEntry(key) {
        OperatorScreen(
            cityCode = key.cityCode,
            cityName = key.cityName,
            onOperatorClick = { operator ->
                backStack.add(
                    RouteIndex(
                        categoryId = key.categoryId,
                        categoryName = key.categoryName,
                        brandId = 0, brandName = "",
                        cityCode = key.cityCode,
                        cityName = key.cityName,
                        operatorId = operator.operatorId,
                        operatorName = operator.operatorName,
                    )
                )
            },
            onBack = { backStack.removeLastOrNull() },
        )
    }
        is RouteIndex -> NavEntry(key) {
        IndexScreen(
            route = key,
            onSaved = { backStack.removeAll { it != RouteHome } },
            onBack = { backStack.removeLastOrNull() },
            onTestClick = {
                backStack.add(
                    RouteTest(
                        categoryId = key.categoryId,
                        categoryName = key.categoryName,
                        brandId = key.brandId,
                        brandName = key.brandName,
                        cityCode = key.cityCode,
                        cityName = key.cityName,
                        operatorId = key.operatorId,
                        operatorName = key.operatorName,
                    )
                )
            },
        )
    }
        is RouteControl -> NavEntry(key) {
        ControlScreen(
            remoteId = key.remoteId,
            onBack = { backStack.removeLastOrNull() },
        )
    }
        is RouteTest -> NavEntry(key) {
        TestScreen(
            route = key,
            onBack = { backStack.removeLastOrNull() },
        )
    }
        else -> error("Unknown route: $key")
    }
}

@Composable
fun AppNavDisplay() {
    val backStack = remember { mutableStateListOf<Any>(RouteHome) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = buildEntryProvider(backStack),
    )
}
