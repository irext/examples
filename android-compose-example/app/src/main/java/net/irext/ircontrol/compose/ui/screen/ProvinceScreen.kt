package net.irext.ircontrol.compose.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.IRApplication
import net.irext.ircontrol.compose.ui.composable.ItemSingleText
import net.irext.webapi.model.City


/**
 * Filename:       ProvinceScreen.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Displays provinces for set-top box setup.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvinceScreen(
    onProvinceClick: (City) -> Unit,
    onBack: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as IRApplication
    var provinces by remember { mutableStateOf<List<City>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun refresh() {
        scope.launch {
            isRefreshing = true
            provinces = WebApiHelper.listProvinces(app.mWeAPIs)
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.province_title)) },
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
        when {
            isRefreshing && provinces.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = { refresh() }) {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(provinces) { province ->
                            ItemSingleText(
                                text = province.name,
                                modifier = Modifier.clickable { onProvinceClick(province) }
                            )
                        }
                    }
                }
            }
        }
    }
}
