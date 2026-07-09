package net.irext.ircontrol.compose

import android.util.Log
import com.activeandroid.ActiveAndroid
import net.irext.webapi.WebAPIs
import net.irext.webapi.model.UserApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.irext.webapi.WebAPICallbacks.SignInCallback

/**
 * Filename:       IRApplication.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Initializes application-wide services and signs in to the IR web API.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class IRApplication : com.activeandroid.app.Application() {

    val mWeAPIs: WebAPIs = WebAPIs.getInstance(ADDRESS, APP_NAME)

    var userApp: UserApp? = null
        private set

    private val mSignInCallback = object : SignInCallback {
        override fun onSignInSuccess(userApp: UserApp) {
            this@IRApplication.userApp = userApp
        }

        override fun onSignInFailed() {
            Log.w(TAG, "sign in failed")
        }

        override fun onSignInError() {
            Log.e(TAG, "sign in error")
        }
    }


    override fun onCreate() {
        super.onCreate()

        // initialize ActiveAndroid
        ActiveAndroid.initialize(this)

        // login with guest-admin account
        CoroutineScope(Dispatchers.IO).launch {
            mWeAPIs.signIn(this@IRApplication, mSignInCallback)
            val currentApp = userApp
            if (currentApp != null) {
                Log.d(TAG, "signIn response : ${currentApp.id}, ${currentApp.token}")
            } else {
                Log.w(TAG, "signIn failed")
            }
        }
    }

    companion object {
        private val TAG = IRApplication::class.java.simpleName
        private const val ADDRESS = "https://srv.irext.net"
        private const val APP_NAME = "/irext-server"

    }
}
