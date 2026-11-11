package net.irext.ircontrol.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import net.irext.decode.sdk.IRDecode;
import net.irext.decode.sdk.utils.Constants;
import net.irext.ircontrol.R;
import net.irext.ircontrol.bean.RemoteControl;
import net.irext.ircontrol.controller.ArduinoRemote;
import net.irext.ircontrol.controller.PhoneRemote;
import net.irext.ircontrol.controller.base.Remote;
import net.irext.ircontrol.utils.FileUtils;
import net.irext.ircontrol.utils.MessageUtils;
import net.irext.ircontrol.utils.MiscUtils;
import net.irext.ircontrol.utils.ToastUtils;

import java.lang.ref.WeakReference;

/**
 * Filename:       ControlActivity.java
 * Revised:        Date: 2017-04-22
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Control activity containing control panel (without fragment)
 * <p>
 * Revision log:
 * 2017-04-22: created by strawmanbobi
 */
@SuppressWarnings("unused")
public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ControlActivity.class.getSimpleName();

    private static final int VIB_TIME = 60;

    private static final int CMD_GET_REMOTE_CONTROL = 0;

    private PhoneRemote mPhoneRemote;
    private ArduinoRemote mArduinoRemote;

    private MsgHandler mHandler;

    private Long mRemoteID;
    private RemoteControl mCurrentRemoteControl;

    private TextView mTvControlTitle;
    private EditText mEtEmitterIp;
    private ImageButton mBtnConnect;
    private View mVWConnectStatus;

    public static final String KEY_REMOTE_ID = "KEY_REMOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mHandler = new MsgHandler(this);

        mArduinoRemote = new ArduinoRemote(this, createCallback());

        mPhoneRemote = PhoneRemote.getInstance(this);

        initViews();

        mRemoteID = getIntent().getLongExtra(KEY_REMOTE_ID, -1L);
        if (-1 == mRemoteID) {
            Log.d(TAG, "remote ID IS NULL");
        } else {
            Log.d(TAG, "get remote, ID = " + mRemoteID);
            getRemote();
        }
    }

    private void initViews() {
        mTvControlTitle = findViewById(R.id.tv_control_title);

        ImageButton btnPower = findViewById(R.id.iv_power);
        ImageButton btnBack = findViewById(R.id.iv_back);
        ImageButton btnHome = findViewById(R.id.iv_home);
        ImageButton btnMenu = findViewById(R.id.iv_menu);
        ImageButton btnUp = findViewById(R.id.iv_up);
        ImageButton btnDown = findViewById(R.id.iv_down);
        ImageButton btnLeft = findViewById(R.id.iv_left);
        ImageButton btnRight = findViewById(R.id.iv_right);
        ImageButton btnOK = findViewById(R.id.iv_ok);
        ImageButton btnPlus = findViewById(R.id.iv_plus);
        ImageButton btnMinus = findViewById(R.id.iv_minus);

        btnPower.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);

        mEtEmitterIp = findViewById(R.id.emitter_ip);
        mBtnConnect = findViewById(R.id.btn_connect_emitter);
        mVWConnectStatus = findViewById(R.id.vw_connect_status);

        mBtnConnect.setOnClickListener(v -> {
            vibrate(ControlActivity.this);
            String emitterIp = mEtEmitterIp.getText().toString();
            if (!MiscUtils.isValidIPv4(emitterIp)) {
                Log.e(TAG, "IP address is invalid: " + emitterIp);
                ToastUtils.showToast(ControlActivity.this, getString(R.string.input_emitter_ip_address), null);
                return;
            }
            mArduinoRemote.connectToEmitter(emitterIp, String.valueOf(ArduinoRemote.EMITTER_PORT));
        });
    }

    private ArduinoRemote.IRSocketEmitterCallback createCallback() {
        return new ArduinoRemote.IRSocketEmitterCallback() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> onEmitterConnected());
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> onEmitterDisconnected());
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                onEmitterResponse(response);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        long newRemoteId = getIntent().getLongExtra(KEY_REMOTE_ID, -1L);
        if (newRemoteId != mRemoteID) {
            mRemoteID = newRemoteId;
            if (mRemoteID != -1) {
                Log.d(TAG, "get remote, ID = " + mRemoteID);
                getRemote();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mArduinoRemote.disconnect();
        closeIRBinary();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mArduinoRemote.disconnect();
    }

    private void getRemote() {
        new Thread() {
            @Override
            public void run() {
                MessageUtils.postMessage(mHandler, CMD_GET_REMOTE_CONTROL);
            }
        }.start();
    }

    private void showRemote() {
        Log.d(TAG, "showRemote, remoteID = " + mRemoteID);
        mCurrentRemoteControl = RemoteControl.getRemoteControl(mRemoteID);
        if (null != mCurrentRemoteControl) {
            int categoryID  = mCurrentRemoteControl.getCategoryId();
            int subCategoryID = mCurrentRemoteControl.getSubCategory();
            String categoryName = mCurrentRemoteControl.getCategoryName();
            String brandName = mCurrentRemoteControl.getBrandName();
            String cityName = mCurrentRemoteControl.getCityName();
            String operatorName = mCurrentRemoteControl.getOperatorName();
            String remoteName = mCurrentRemoteControl.getRemote();
            String remoteControlTitle;
            Log.d(TAG, "showRemote, category = " + categoryID + ", subCategory = " + subCategoryID);
            String binFileName = FileUtils.binDir + FileUtils.FILE_NAME_PREFIX +
                    mCurrentRemoteControl.getRemoteMap() + FileUtils.FILE_NAME_EXT;

            if (Constants.CategoryID.STB.getValue() == categoryID) {
                remoteControlTitle = cityName + operatorName + categoryName + " - " + remoteName;
            } else {
                remoteControlTitle = brandName + categoryName + " - " + remoteName;
            }
            mTvControlTitle.setText(remoteControlTitle);
            /* decode SDK - load binary file */
            int ret = mPhoneRemote.irOpen(binFileName, categoryID, subCategoryID);
            Log.d(TAG, "binary opened : " + ret);
        }
    }

    public void closeIRBinary() {
        mPhoneRemote.irClose();
    }

    private void onEmitterConnected() {
        Log.d(TAG, "onEmitterConnected, set the status and button color in UI");
        mBtnConnect.setImageDrawable(AppCompatResources.getDrawable(this, R.mipmap.button_unlink));
        mVWConnectStatus.setBackgroundColor(Color.parseColor("#3FAFFF"));
    }

    private void onEmitterDisconnected() {
        Log.d(TAG, "onEmitterDisconnected, set the status and button color in UI");
        ToastUtils.showToast(this, getString(R.string.connect_disconnected), Toast.LENGTH_SHORT);
        mBtnConnect.setImageDrawable(AppCompatResources.getDrawable(this, R.mipmap.button_link));
        mVWConnectStatus.setBackgroundColor(Color.parseColor("#FF7F7F"));
    }

    private void processEHello(String response) {
        mArduinoRemote.sendHelloToEmitter();
    }

    private void processEBin(String response) {
        long currentRemoteId = getIntent().getLongExtra(KEY_REMOTE_ID, -1L);
        Log.d(TAG, "processEBin: current remote ID = " + currentRemoteId);

        RemoteControl currentRemoteControl = RemoteControl.getRemoteControl(currentRemoteId);
        if (currentRemoteControl != null) {
            Log.d(TAG, "processEBin, will send binary for remote control, id = " + currentRemoteId + 
                  ", remoteControl.id = " + currentRemoteControl.getId() + 
                  ", remoteControl.category = " + currentRemoteControl.getCategoryId());
            
            String binFileName = FileUtils.binDir + FileUtils.FILE_NAME_PREFIX +
                    currentRemoteControl.getRemoteMap() + FileUtils.FILE_NAME_EXT;
            byte []binContent = FileUtils.getByteArrayFromFile(binFileName);
            if (null != binContent) {
                mArduinoRemote.sendBinToEmitter(binContent, 
                    currentRemoteControl.getCategoryId(), 
                    currentRemoteControl.getSubCategory());
            } else {
                Log.e(TAG, "emitter sender could not open the binary file");
                ToastUtils.showToast(this, getString(R.string.file_could_not_open), Toast.LENGTH_SHORT);
            }
        }
    }

    private void processECtrl(String response) {

    }

    private void processControlResult(String response) {
        runOnUiThread(() -> {
            if (response.startsWith(ArduinoRemote.E_INDICATION_SUCCESS)) {
                ToastUtils.showToast(this, getString(R.string.decode_and_send_success), null);
            } else {
                ToastUtils.showToast(this, getString(R.string.decode_and_send_failed), null);
            }
        });
    }

    private void onEmitterResponse(String response) {
        if (response.startsWith(ArduinoRemote.E_RESPONSE_HELLO)) {
            processEHello(response);
        } else if (response.startsWith(ArduinoRemote.E_RESPONSE_BIN)) {
            processEBin(response);
        } else if (response.startsWith(ArduinoRemote.E_RESPONSE_CTRL)) {
            processECtrl(response);
        } else if (response.startsWith(ArduinoRemote.E_INDICATION_SUCCESS) ||
                   response.startsWith(ArduinoRemote.E_INDICATION_FAILED)) {
            processControlResult(response);
        } else {
            Log.e(TAG, "unexpected response : " + response);
        }
    }

    @Override
    public void onClick(View v) {
        vibrate(this);
        Remote remote = null;
        int keyCode = 0;
        int result = 0;
        int id = v.getId();
        if (id == R.id.iv_power) {
            keyCode = Remote.KEY_POWER;
        } else if (id == R.id.iv_up) {
            keyCode = Remote.KEY_UP;
        } else if (id == R.id.iv_down) {
            keyCode = Remote.KEY_DOWN;
        } else if (id == R.id.iv_left) {
            keyCode = Remote.KEY_LEFT;
        } else if (id == R.id.iv_right) {
            keyCode = Remote.KEY_RIGHT;
        } else if (id == R.id.iv_ok) {
            keyCode = Remote.KEY_OK;
        } else if (id == R.id.iv_plus) {
            keyCode = Remote.KEY_PLUS;
        } else if (id == R.id.iv_minus) {
            keyCode = Remote.KEY_MINUS;
        } else if (id == R.id.iv_back) {
            keyCode = Remote.KEY_BACK;
        } else if (id == R.id.iv_home) {
            keyCode = Remote.KEY_HOME;
        } else if (id == R.id.iv_menu) {
            keyCode = Remote.KEY_MENU;
        }

        if (mArduinoRemote.getConnectionStatus() == ArduinoRemote.EMITTER_WORKING && mCurrentRemoteControl != null) {
            mArduinoRemote.irControl(mCurrentRemoteControl.getCategoryId(), mCurrentRemoteControl.getSubCategory(), keyCode);
        } else if (mCurrentRemoteControl != null){
            result = mPhoneRemote.irControl(mCurrentRemoteControl.getCategoryId(), mCurrentRemoteControl.getSubCategory(), keyCode);
            if (0 == result) {
                ToastUtils.showToast(this, getString(R.string.decode_and_send_success), null);
            } else {
                ToastUtils.showToast(this, getString(R.string.decode_and_send_failed), null);
            }
        }
    }

    private static class MsgHandler extends Handler {

        WeakReference<ControlActivity> mActivity;

        MsgHandler(ControlActivity activity) {
            super(Looper.getMainLooper());
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.getData().getInt(MessageUtils.KEY_CMD);

            ControlActivity activity = mActivity.get();
            if (activity != null && cmd == CMD_GET_REMOTE_CONTROL) {
                activity.showRemote();
            }
        }
    }

    private static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIB_TIME);
    }
}