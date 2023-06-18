package com.xjh.xinwo.module;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import com.xinwo.produce.record.FUDualInputToTextureExampleFragment;
import com.xjh.xinwo.R;
import com.xinwo.base.BaseActivity;
import com.xinwo.social.chat.fragment.ChatFragment;
import com.xinwo.social.groupchat.fragment.GroupChatFragment;

import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final String[] fragmentTags = {ChatFragment.class.getName(),
            FUDualInputToTextureExampleFragment.class.getName(), GroupChatFragment.class.getName()};
    private Fragment mCurrentFragment;
    private ImageView mIvChat;
    private ImageView mIvCamera;
    private ImageView mIvGroupChat;
    private View mContainerMainBottom;
    private int mCurrentTab = 0;
    private int mLastTab = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("jin", String.valueOf(TestKt.a));
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionsUtil.requestPermission(this, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_LONG).show();
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);
    }

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
        switchFragment(0);
        changeTab(0);

        mIvChat = findViewById(R.id.ivChat);
        mIvCamera = findViewById(R.id.ivCamera);
        mIvGroupChat = findViewById(R.id.ivGroupChat);
        mContainerMainBottom = findViewById(R.id.containerMainBottom);

        mIvChat.setOnClickListener(this);
        mIvCamera.setOnClickListener(this);
        mIvGroupChat.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    private void switchFragment(int tabPosition) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(fragmentTags[tabPosition]);
        if (fragment == null) {
            fragment = createFragment(tabPosition);

            if (mCurrentFragment == null) {
                fm.beginTransaction()
                        // 在添加的时候给其制定 tag，不然到时候上面的语句就没用了
                        .add(R.id.container, fragment, fragmentTags[tabPosition])
                        .commit();
            } else {
                fm.beginTransaction()
                        // 在添加的时候给其制定 tag，不然到时候上面的语句就没用了
                        .add(R.id.container, fragment, fragmentTags[tabPosition])
                        .hide(mCurrentFragment)
                        .commit();
            }

            mCurrentFragment = fragment;
        } else {
            // BUG FIX: NullPointerException: Attempt to invoke virtual method
            // 'void androidx.fragment.app.Fragment.setNextAnim(int)' on a null object reference
            if (mCurrentFragment != null) {
                fm.beginTransaction()
                        .hide(mCurrentFragment)
                        .show(fragment)
                        .commit();
            } else {
                fm.beginTransaction()
                        .show(fragment)
                        .commit();
            }

            mCurrentFragment = fragment;
        }
    }

    private Fragment createFragment(int tabPosition) {
        Fragment fragment = null;
        switch (tabPosition) {
            case 0:
                fragment = new ChatFragment();
                break;
            case 1:
                fragment = new FUDualInputToTextureExampleFragment();
                break;
            case 2:
                fragment = new GroupChatFragment();
                break;
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivChat) {
            switchFragment(0);
            changeTab(0);
            mContainerMainBottom.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.ivCamera) {
            switchFragment(1);
            changeTab(1);
            mContainerMainBottom.setVisibility(View.GONE);
        } else if (v.getId() == R.id.ivGroupChat) {
            switchFragment(2);
            changeTab(2);
            mContainerMainBottom.setVisibility(View.GONE);
        }
    }

    private void changeTab(int position) {
        if (mCurrentTab == position)
            return;

        mLastTab = mCurrentTab;
        mCurrentTab = position;
    }

    public void switchToLastTab() {
        switch (mLastTab) {
            case 0:
                mIvChat.performClick();
                break;
            case 1:
                mIvCamera.performClick();
                break;
            case 2:
                mIvGroupChat.performClick();
                break;
        }
    }
}
