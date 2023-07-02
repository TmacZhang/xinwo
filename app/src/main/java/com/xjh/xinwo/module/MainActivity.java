package com.xjh.xinwo.module;

import android.Manifest;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.xinwo.base.BaseActivity;
import com.xinwo.feed.FeedFragment;
import com.xinwo.produce.record.FUDualInputToTextureExampleFragment;
import com.xinwo.social.chat.fragment.ChatFragment;
import com.xinwo.social.groupchat.fragment.GroupChatFragment;
import com.xinwo.social.profile.ProfileFragment;
import com.xjh.xinwo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final String[] fragmentTags = {
            FeedFragment.class.getName(),
            ChatFragment.class.getName(),
            FUDualInputToTextureExampleFragment.class.getName(),
            GroupChatFragment.class.getName(),
            ProfileFragment.class.getName()};
    private Fragment mCurrentFragment;

    private RelativeLayout mRelativeFeed;
    private RelativeLayout mRelativeChat;
    private RelativeLayout mRelativeCamera;
    private RelativeLayout mRelativeGroupChat;
    private RelativeLayout mRelativeProfile;

    private View mContainerMainBottom;
    private int mCurrentTab = 0;
    private int mLastTab = -1;
    private final List<View> mLists = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarColor();

        System.loadLibrary("native-lib");
        registerSignal();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isDark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                    == Configuration.UI_MODE_NIGHT_YES;
            View decorView = this.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (!isDark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    getWindow().setStatusBarColor(Color.BLACK);
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
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
        mRelativeFeed = findViewById(R.id.relativeFeed);
        mRelativeChat = findViewById(R.id.relativeChat);
        mRelativeCamera = findViewById(R.id.relativeCamera);
        mRelativeGroupChat = findViewById(R.id.relativeGroupChat);
        mRelativeProfile = findViewById(R.id.relativeProfile);
        TextView ivFeed = findViewById(R.id.ivFeed);
        TextView ivChat = findViewById(R.id.ivChat);
        ImageView ivCamera = findViewById(R.id.ivCamera);
        TextView ivGroupChat = findViewById(R.id.ivGroupChat);
        TextView ivProfile = findViewById(R.id.ivProfile);
        mContainerMainBottom = findViewById(R.id.containerMainBottom);

        mRelativeFeed.setOnClickListener(this);
        mRelativeChat.setOnClickListener(this);
        mRelativeCamera.setOnClickListener(this);
        mRelativeGroupChat.setOnClickListener(this);
        mRelativeProfile.setOnClickListener(this);

        mLists.add(ivFeed);
        mLists.add(ivChat);
        mLists.add(ivCamera);
        mLists.add(ivGroupChat);
        mLists.add(ivProfile);
        switchFragment(0);
        changeTab(0);
    }

    @Override
    public void loadData() {

    }

    private void switchFragment(int tabPosition) {
        for (int i = 0; i < mLists.size(); i++) {
            if (i == tabPosition) {
                if (mLists.get(tabPosition) instanceof TextView) {
                    ((TextView) mLists.get(tabPosition)).setTextColor(Color.BLACK);
                    ((TextView) mLists.get(tabPosition)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
            } else {
                mLists.get(i).setSelected(false);
                if (mLists.get(i) instanceof TextView) {
                    ((TextView) mLists.get(i)).setTextColor(Color.GRAY);
                    ((TextView) mLists.get(i)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            }
        }

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
                fragment = new FeedFragment();
                break;
            case 1:
                fragment = new ChatFragment();
                break;
            case 2:
                fragment = new FUDualInputToTextureExampleFragment();
                break;
            case 3:
                fragment = new GroupChatFragment();
                break;
            case 4:
                fragment = new ProfileFragment();
                break;
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.relativeFeed) {
            switchFragment(0);
            changeTab(0);
            mContainerMainBottom.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.relativeChat) {
            switchFragment(1);
            changeTab(1);
            mContainerMainBottom.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.relativeCamera) {
            switchFragment(2);
            changeTab(2);
            mContainerMainBottom.setVisibility(View.GONE);
        } else if (v.getId() == R.id.relativeGroupChat) {
            switchFragment(3);
            changeTab(3);
            mContainerMainBottom.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.relativeProfile) {
            switchFragment(4);
            changeTab(4);
            mContainerMainBottom.setVisibility(View.VISIBLE);
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
                mRelativeFeed.performClick();
                break;
            case 1:
                mRelativeChat.performClick();
                break;
            case 2:
                mRelativeCamera.performClick();
                break;
            case 3:
                mRelativeGroupChat.performClick();
                break;
            case 4:
                mRelativeProfile.performClick();
                break;
        }
    }


    public native void registerSignal();
}
