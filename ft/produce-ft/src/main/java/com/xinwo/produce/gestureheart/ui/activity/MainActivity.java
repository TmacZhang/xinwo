package com.xinwo.produce.gestureheart.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.xinwo.produce.R;
import com.xinwo.produce.gestureheart.ui.activity.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionsUtil.requestPermission(getBaseContext(), new PermissionListener() {

                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                        Toast.makeText(getBaseContext(), "读写SD卡权限申请成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        Toast.makeText(getBaseContext(), "读写SD卡权限申请失败！！！", Toast.LENGTH_SHORT).show();
                    }
                },
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO});

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //TODO 导入注释 2019.12.5
//        findViewById(R.id.btn01).setOnClickListener(this);
//        findViewById(R.id.btn02).setOnClickListener(this);
//        findViewById(R.id.btn03).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //TODO 导入注释 2019.12.5
//        Intent intent = null;
//        switch (v.getId()){
//            case R.id.btn01:
//                intent = new Intent(this, PreviewActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn02:
//                intent = new Intent(this, FUDualInputToTextureExampleFragment.class);
//                startActivity(intent);
//                break;
//            case R.id.btn03:
//                intent = new Intent(this, MusicPlayActivity.class);
//                startActivity(intent);
//                break;
//        }
    }
}
