package net.irext.ircontrol;

import android.os.Build;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import net.irext.webapi.WebAPIs;
import net.irext.webapi.model.UserApp;
import net.irext.webapi.WebAPICallbacks.SignInCallback;

/**
 * Filename:       IRApplication.java
 * Revised:        Date: 2017-03-28
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Main Application class for irext decode example
 * <p>
 * Revision log:
 * 2017-03-28: created by strawmanbobi
 */
@SuppressWarnings("unused")
public class IRApplication extends com.activeandroid.app.Application {

    private static final String TAG = IRApplication.class.getSimpleName();

    private static final String ADDRESS = "http://<your private server IP>:8081";
    private static final String APP_NAME = "/irext-server";

    public WebAPIs mWeAPIs = WebAPIs.getInstance(ADDRESS, APP_NAME);

    private UserApp mUserApp;

    private SignInCallback mSignInCallback = new SignInCallback() {
        @Override
        public void onSignInSuccess(UserApp userApp) {
            mUserApp = userApp;
        }

        @Override
        public void onSignInFailed() {
            Log.w(TAG, "sign in failed");
        }

        @Override
        public void onSignInError() {
            Log.e(TAG, "sign in error");
        }
    };

    public UserApp getUserApp() {
        return mUserApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize ActiveAndroid
        ActiveAndroid.initialize(this);

        // login with guest-admin account
        new Thread() {
            @Override
            public void run() {
                mWeAPIs.signIn(IRApplication.this, mSignInCallback);
                if (null != mUserApp) {
                    Log.d(TAG, "signIn response : " + mUserApp.getId() + ", " + mUserApp.getToken());
                } else {
                    Log.w(TAG, "signIn failed");
                }
            }
        }.start();
    }

    public static String getApplicationName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return IRApplication.class.getPackageName();
        } else {
            return IRApplication.class.getPackage().getName();
        }
    }
}
