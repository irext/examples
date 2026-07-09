package net.irext.ircontrol.compose.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import net.irext.ircontrol.compose.ui.navigation.AppNavDisplay
import net.irext.ircontrol.compose.ui.theme.IRControlTheme


/**
 * Filename:       MainActivity.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Hosts the Compose application and handles storage permission requests.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var storagePermissionGranted = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val readOk = results[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        val writeOk = results[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
        if (readOk && writeOk) {
            storagePermissionGranted = true
            Log.i(TAG, "storage permissions granted")
        } else {
            Log.w(TAG, "storage permissions denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkStoragePermissions()

        setContent {
            IRControlTheme {
                AppNavDisplay()
            }
        }
    }

    private fun checkStoragePermissions() {
        val readGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val writeGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (readGranted && writeGranted) {
            storagePermissionGranted = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }
}
