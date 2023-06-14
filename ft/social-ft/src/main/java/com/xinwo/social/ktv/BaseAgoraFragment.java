package com.xinwo.social.ktv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.xinwo.base.BaseApplication;
import com.xinwo.base.BaseFragment;
import com.xinwo.base.ConstantApp;
import com.xinwo.base.EngineConfig;
import com.xinwo.base.MyEngineEventHandler;
import com.xinwo.base.WorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

import io.agora.rtc.RtcEngine;

public abstract class BaseAgoraFragment extends BaseFragment {
    private final static Logger log = LoggerFactory.getLogger(BaseAgoraFragment.class);
    private boolean mIsFirstResume = true;
    private final String TAG = "BaseAgoraFragment";

    @Override
    protected void success(Object bean, int tag) {

    }

    @Override
    protected void error(Throwable e, int tag) {

    }

    @Override
    public Map<String, String> getParams(int tag) {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        //        final View layout  = getView().findViewById(Window.ID_ANDROID_CONTENT);
        //        ViewTreeObserver vto =layout.getViewTreeObserver();
        //        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        //            @Override
        //            public void onGlobalLayout() {
        //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        //                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        //                } else {
        //                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        //                }
        //                initUIandEvent();
        //            }
        //        });
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionsUtil.requestPermission(getContext(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Log.e(TAG, "onResume --> permissionGranted --> length = " + permission.length);
                int length = permission.length;
                for (int i = 0; i < length; ++i) {
                    if (permission[i] == Manifest.permission.CAMERA) {
                        BaseApplication.getInstance().initWorkerThread();
                    }
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {

            }
        }, new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onResume() {
        super.onResume();
        //        boolean checkPermissionResult = checkSelfPermissions();

        Log.e(TAG, "onResume --> ");


        if (mIsFirstResume) {
            initUIandEvent();
            mIsFirstResume = false;
        }
    }


    protected abstract void initUIandEvent();

    protected abstract void deInitUIandEvent();

    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        log.debug("onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        switch (requestCode) {
            case ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
                } else {
                    Toast.makeText(getContext(), "录音权限申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                    BaseApplication.getInstance().initWorkerThread();
                } else {
                    Toast.makeText(getContext(), "相机权限申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getContext(), "存储权限申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deInitUIandEvent();

    }

    protected RtcEngine rtcEngine() {
        return BaseApplication.getInstance().getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return BaseApplication.getInstance().getWorkerThread();
    }

    protected final EngineConfig config() {
        return BaseApplication.getInstance().getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return BaseApplication.getInstance().getWorkerThread().eventHandler();
    }


    public boolean checkSelfPermission(String permission, int requestCode) {
        log.debug("checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(getContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            PermissionsUtil.requestPermission(getContext(), new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {

                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {

                }
            }, permission);
            //
            // AppCompatActivity.requestPermissions(getContext(),
            //         new String[]{permission},
            //         requestCode);
            return false;
        }

        if (Manifest.permission.CAMERA.equals(permission)) {
            BaseApplication.getInstance().initWorkerThread();
        }
        return true;
    }
}
