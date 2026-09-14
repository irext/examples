package net.irext.ircontrol.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import net.irext.ircontrol.R;
import net.irext.ircontrol.bean.RemoteControl;
import net.irext.ircontrol.ui.fragment.MainFragment;
import net.irext.ircontrol.utils.MessageUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Filename:       MainActivity.java
 * Revised:        Date: 2017-04-04
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Main Activity class for irext decode example
 * <p>
 * Revision log:
 * 2017-04-04: created by strawmanbobi
 */
@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mReadPermissionGranted = false;

    private boolean mWritePermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_STORAGE = 1001;

    public static final int CMD_GOTO_CONTROL = 0;

    private RemoteControl mCurrentRemoteControl;

    public MsgHandler mMsgHandler;

    public RemoteControl getCurrentRemoteControl() {
        return mCurrentRemoteControl;
    }

    public MainActivity() {
    }

    public void setCurrentRemoteControl(RemoteControl mCurrentRemoteControl) {
        this.mCurrentRemoteControl = mCurrentRemoteControl;
    }

    public MainActivity(RemoteControl mCurrentRemoteControl) {
        this.mCurrentRemoteControl = mCurrentRemoteControl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        checkAndRequestStoragePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-check permissions when returning from settings
        checkAndRequestStoragePermissions();

        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        MainFragment mRemoteListFragment = (MainFragment) mFragmentManager.findFragmentById(R.id.fragment_remote);

        if (null == mRemoteListFragment) {
            Log.e(TAG, "MainFragment is null");
        }
        assert mRemoteListFragment != null;
        mRemoteListFragment.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "BUTTON PRESSED");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        mMsgHandler = new MsgHandler(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCreateNew();
            }
        });
    }

    private void checkAndRequestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+: use MANAGE_EXTERNAL_STORAGE
            checkManageStoragePermission();
        } else {
            // Android 7.1 ~ 13: use legacy READ/WRITE_EXTERNAL_STORAGE
            checkLegacyStoragePermissions();
        }
    }

    private void checkManageStoragePermission() {
        if (Environment.isExternalStorageManager()) {
            Log.i(TAG, "MANAGE_EXTERNAL_STORAGE already granted.");
            mReadPermissionGranted = true;
            mWritePermissionGranted = true;
        } else {
            Log.w(TAG, "MANAGE_EXTERNAL_STORAGE not granted, redirecting to settings.");
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void checkLegacyStoragePermissions() {
        boolean readGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean writeGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionsToRequest = new ArrayList<>();

        if (!readGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.w(TAG, "read external storage permission not granted");
        }

        if (!writeGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.w(TAG, "write external storage permission not granted");
        }

        if (!permissionsToRequest.isEmpty()) {
            Log.d(TAG, "requesting permission: " + String.join(", ", permissionsToRequest));
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    PERMISSIONS_REQUEST_STORAGE);
        } else {
            Log.i(TAG, "storage permissions already granted.");
            mReadPermissionGranted = true;
            mWritePermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Log.d(TAG, "storage permission granted");
                mReadPermissionGranted = true;
                mWritePermissionGranted = true;
            } else {
                Log.w(TAG, "storage permission denied");
                mReadPermissionGranted = false;
                mWritePermissionGranted = false;
            }
        }
    }

    private void gotoCreateNew() {
        if (!mReadPermissionGranted || !mWritePermissionGranted) {
            Log.e(TAG, "storage read and write permission not granted");
            Toast.makeText(this, this.getString(R.string.storage_permission_warning), Toast.LENGTH_SHORT).show();
            checkAndRequestStoragePermissions();
        }
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    private void gotoControl() {
        if (!mReadPermissionGranted || !mWritePermissionGranted) {
            Log.e(TAG, "storage read and write permission not granted");
            Toast.makeText(this, this.getString(R.string.storage_permission_warning), Toast.LENGTH_SHORT).show();
            checkAndRequestStoragePermissions();
        }
        Intent intent = new Intent(this, ControlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ControlActivity.KEY_REMOTE_ID, mCurrentRemoteControl.getID());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private  static class MsgHandler extends Handler {

        WeakReference<MainActivity> mMainActivity;

        MsgHandler(MainActivity activity) {
            mMainActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.getData().getInt(MessageUtils.KEY_CMD);
            Log.d(TAG, "handle message " + cmd);

            MainActivity mainActivity = mMainActivity.get();
            if (cmd == CMD_GOTO_CONTROL) {
                mainActivity.gotoControl();
            }
        }
    }
}
