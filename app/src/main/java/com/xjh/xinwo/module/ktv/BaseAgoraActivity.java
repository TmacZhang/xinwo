package com.xjh.xinwo.module.ktv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xinwo.feed.base.BaseActivity;
import com.xinwo.feed.base.ConstantApp;
import com.xinwo.feed.base.EngineConfig;
import com.xinwo.feed.base.MyEngineEventHandler;
import com.xinwo.feed.base.BaseApplication;
import com.xinwo.feed.base.WorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


import io.agora.rtc.RtcEngine;

public abstract class BaseAgoraActivity extends BaseActivity {
    private final static Logger log = LoggerFactory.getLogger(BaseAgoraActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View layout  =findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver vto =layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                initUIandEvent();
            }
        });


    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                boolean checkPermissionResult = checkSelfPermissions();

                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
                    // so far we do not use OnRequestPermissionsResultCallback
                }
            }
        }, 500);
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
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                    ((BaseApplication) getApplication()).initWorkerThread();
                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
                break;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        deInitUIandEvent();

    }
    protected RtcEngine rtcEngine() {
        return ((BaseApplication) getApplication()).getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return ((BaseApplication) getApplication()).getWorkerThread();
    }

    protected final EngineConfig config() {
        return ((BaseApplication) getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((BaseApplication) getApplication()).getWorkerThread().eventHandler();
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        log.debug("checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }

        if (Manifest.permission.CAMERA.equals(permission)) {
            ((BaseApplication) getApplication()).initWorkerThread();
        }
        return true;
    }
}
