package com.xjh.gestureheart.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xinwo.xinview.StatusTextView;
import com.xjh.gestureheart.gl.VideoGLSurfaceView;
import com.xjh.gestureheart.mediacodec.VideoClipper;
import com.xjh.gestureheart.record.FUExampleFragment;
import com.xjh.xinwo.R;

import java.io.IOException;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = "PreviewActivity";

    public VideoGLSurfaceView glSurfaceView;
    private String mInputVideoPath;
    private String mInputVideoPath2;
    private String mOutputVideoPath;
    private String mOutputAudioPath2;
//    private AppCompatSeekBar seekBar;
    private View containerPlay;
    private ImageView ivPlayStatus;

    private Fragment currentFragment;
    private Fragment filterFragment;
    private MagicFragment magicFragment;
    private Fragment timeFragment;
    private StatusTextView tvFilter;
    private StatusTextView tvMagic;
    private StatusTextView tvTime;
    private String mergeVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_new);

        initData();
        initView();
        initGL();
    }

    private void initData() {
        mergeVideo = getIntent().getExtras().getString(FUExampleFragment.MERGE_PATH);
    }


    private void initView() {
        switchFragment(getFragment(R.id.tvFilter)).commit();

        glSurfaceView = (VideoGLSurfaceView) findViewById(R.id.glSurfaceView);
//        seekBar = (AppCompatSeekBar) findViewById(R.id.seekBar);

        containerPlay = findViewById(R.id.containerWan);
        ivPlayStatus = (ImageView) findViewById(R.id.ivPlayStatus);
        ivPlayStatus.setVisibility(View.VISIBLE);

        glSurfaceView.setOnPlayStateChangeListener(new VideoGLSurfaceView.PlayStateChangeListener() {
            @Override
            public void onPlayStart() {
                ivPlayStatus.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPlayPause() {
                ivPlayStatus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPlayComplete() {
                ivPlayStatus.setVisibility(View.VISIBLE);
            }
        });



        containerPlay.setOnClickListener(this) ;
        ivPlayStatus.setOnClickListener(this);

        tvFilter = (StatusTextView) findViewById(R.id.tvFilter);
        tvFilter.setSelected(true);
        tvFilter.setOnClickListener(this);
        tvMagic = (StatusTextView) findViewById(R.id.tvMagic);
        tvMagic.setOnClickListener(this);
        tvTime = (StatusTextView) findViewById(R.id.tvTime);
        tvTime.setOnClickListener(this);

        findViewById(R.id.tvSave).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);

//        findViewById(R.id.btnGenerate).setOnClickListener(this);
//        findViewById(R.id.btnRetrieveFilter).setOnClickListener(this);
//        findViewById(R.id.btnRetrieveParticle).setOnClickListener(this);
//        findViewById(R.id.btnReverse).setOnClickListener(this);
//        findViewById(R.id.btnCancelReverse).setOnClickListener(this);
//        findViewById(R.id.btnTest).setOnClickListener(this);
//
//        findViewById(R.id.btnFilter01).setOnTouchListener(this);
//        findViewById(R.id.btnFilter02).setOnTouchListener(this);
//        findViewById(R.id.btnFilter03).setOnTouchListener(this);
//        findViewById(R.id.btnFilter04).setOnTouchListener(this);
//        findViewById(R.id.btnFilter05).setOnTouchListener(this);
//        findViewById(R.id.btnFilter06).setOnTouchListener(this);
//        findViewById(R.id.btnFilter07).setOnTouchListener(this);
//        findViewById(R.id.btnFilter08).setOnTouchListener(this);
//        findViewById(R.id.btnFilter09).setOnTouchListener(this);
//        findViewById(R.id.btnFilter10).setOnTouchListener(this);
//        findViewById(R.id.btnFilter11).setOnTouchListener(this);
//        findViewById(R.id.btnFilter12).setOnTouchListener(this);
//        findViewById(R.id.btnFilter13).setOnTouchListener(this);
//        findViewById(R.id.btnFilter14).setOnTouchListener(this);
//        findViewById(R.id.btnFilter15).setOnTouchListener(this);
    }

    private void initGL() {
//        mInputVideoPath = Environment.getExternalStorageDirectory() + "/generateEnable.mp4";
//        mInputVideoPath = Environment.getExternalStorageDirectory() + "/zc-bjyd.mp4";
//        mInputVideoPath = Environment.getExternalStorageDirectory() + "/zc01.mp4";
        mInputVideoPath2 = Environment.getExternalStorageDirectory() + "/zz14.mp4";
        mOutputVideoPath = Environment.getExternalStorageDirectory() + "/Codec/video/generateEnable.mp4";
        mOutputAudioPath2 = Environment.getExternalStorageDirectory() + "/Codec/video/mixedAudio.aac";
        Log.e(TAG,"videoPath = " + mInputVideoPath);

//        glSurfaceView.setVideoInputPath(mInputVideoPath);
        glSurfaceView.setVideoInputPath(mergeVideo);
        glSurfaceView.setVideoOutputPath(mOutputVideoPath);
//        glSurfaceView.setSeekBar(seekBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvFilter:
                tvFilter .setSelected(true);
                tvMagic.setSelected(false);
                tvTime .setSelected(false);
                switchFragment(getFragment(v.getId())).commit();
                break;
            case R.id.tvMagic:
                tvFilter .setSelected(false);
                tvMagic.setSelected(true);
                tvTime .setSelected(false);
                switchFragment(getFragment(v.getId())).commit();
                break;
            case R.id.tvTime:
                tvFilter .setSelected(false);
                tvMagic.setSelected(false);
                tvTime .setSelected(true);
                switchFragment(getFragment(v.getId())).commit();
                break;
            //ivPlayStatus 与 containerPlay 点击效果相同
            case R.id.ivPlayStatus:
            case R.id.containerWan:
                if(glSurfaceView.isPlaying()){
                    glSurfaceView.pauseVideo();
                }else{
                    glSurfaceView.playVideo(false);
                }
                break;
            case R.id.tvSave:
                VideoClipper videoClipper = new VideoClipper(1, 3_000_000);
//                videoClipper.setInputVideoPath(mInputVideoPath, mOutputAudioPath2);
                videoClipper.setInputVideoPath(mInputVideoPath, null);
                videoClipper.setOutputVideoPath(mOutputVideoPath);
                videoClipper.setFilter(0);
                videoClipper.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
                    @Override
                    public void onFinish() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "生成完毕", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                try {
                    videoClipper.clipVideo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvCancel:
                finish();
                break;
//            case R.id.btnRetrieveFilter:
//                glSurfaceView.retrieveFilter();
//                break;
//            case R.id.btnRetrieveParticle:
//                glSurfaceView.retrieveParticles();
//                break;
//            case R.id.btnReverse:
//                glSurfaceView.reverse();
//                break;
//            case R.id.btnCancelReverse:
//                glSurfaceView.cancelReverse();
//                break;
//            case R.id.btnTest:
//                AudioMixer audioMixer = new AudioMixer();
//                audioMixer.setInputPath(mInputVideoPath, mInputVideoPath2);
//                audioMixer.setOutputPath(mOutputAudioPath2);
//                audioMixer.setVolume(0.2f, 1.0f);
//                audioMixer.start();
//                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                glSurfaceView.changeFilter( getFilterIndex(v.getId()));
//                glSurfaceView.playVideo(true);
//                break;
//            case MotionEvent.ACTION_UP:
//                glSurfaceView.pauseVideo();
//                break;
//        }
        return true;
    }

    private int getFilterIndex(int viewId) {
        int filterIndex = 0;
//        switch (viewId){
//            case R.id.btnFilter01:
//                filterIndex = 1;
//                break;
//            case R.id.btnFilter02:
//                filterIndex = 2;
//                break;
//            case R.id.btnFilter03:
//                filterIndex = 3;
//                break;
//            case R.id.btnFilter04:
//                filterIndex = 4;
//                break;
//            case R.id.btnFilter05:
//                filterIndex = 5;
//                break;
//            case R.id.btnFilter06:
//                filterIndex = 6;
//                break;
//            case R.id.btnFilter07:
//                filterIndex = 7;
//                break;
//            case R.id.btnFilter08:
//                filterIndex = 8;
//                break;
//            case R.id.btnFilter09:
//                filterIndex = 9;
//                break;
//            case R.id.btnFilter10:
//                filterIndex = 10;
//                break;
//            case R.id.btnFilter11:
//                filterIndex = 11;
//                break;
//            case R.id.btnFilter12:
//                filterIndex = 12;
//                break;
//            case R.id.btnFilter13:
//                filterIndex = 13;
//                break;
//            case R.id.btnFilter14:
//                filterIndex = 14;
//                break;
//            case R.id.btnFilter15:
//                filterIndex = 15;
//                break;
//
//        }
        return filterIndex;
    }

    private Fragment getFragment(int idClicked){
        Fragment fragment = null;
        switch (idClicked){
            case R.id.tvFilter:
                if(filterFragment == null){
                    filterFragment = new FilterFragment();
                }
                fragment = filterFragment;
                break;
            case R.id.tvMagic:
                if(magicFragment == null){
                    magicFragment = new MagicFragment();
                }
                fragment = magicFragment;
                break;
            case R.id.tvTime:
                if(timeFragment == null){
                    timeFragment = new TimeFragment();
                }
                fragment = timeFragment;
                break;
        }
        return fragment;
    }

    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.containerFrag, targetFragment,targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

}
