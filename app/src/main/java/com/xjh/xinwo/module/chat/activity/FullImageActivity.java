package com.xjh.xinwo.module.chat.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.xjh.xinwo.R;
import com.xjh.xinwo.enity.FullImageInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class FullImageActivity extends Activity {

    ImageView fullImage;
    LinearLayout fullLay;
    private int mLeft;
    private int mTop;
    private float mScaleX;
    private float mScaleY;
    private Drawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_full_image);
        fullImage = (ImageView) findViewById(R.id.full_image);
        fullLay = (LinearLayout) findViewById(R.id.full_lay);

        fullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick();
            }
        });

        EventBus.getDefault().register(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) //在ui线程执行
    public void onDataSynEvent(final FullImageInfo fullImageInfo) {
        final int left = fullImageInfo.getLocationX();
        final int top = fullImageInfo.getLocationY();
        final int width = fullImageInfo.getWidth();
        final int height = fullImageInfo.getHeight();
        mBackground = new ColorDrawable(Color.BLACK);
        fullLay.setBackground(mBackground);
        fullImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fullImage.getViewTreeObserver().removeOnPreDrawListener(this);
                int location[] = new int[2];
                fullImage.getLocationOnScreen(location);
                mLeft = left - location[0];
                mTop = top - location[1];
                mScaleX = width * 1.0f / fullImage.getWidth();
                mScaleY = height * 1.0f / fullImage.getHeight();
                activityEnterAnim();
                return true;
            }
        });
//        Glide.with(this).load(fullImageInfo.getImageUrl()).into(fullImage);
        Glide.with(this).load(R.drawable.header_girl).into(fullImage);
    }

    private void activityEnterAnim() {
        fullImage.setPivotX(0);
        fullImage.setPivotY(0);
        fullImage.setScaleX(mScaleX);
        fullImage.setScaleY(mScaleY);
        fullImage.setTranslationX(mLeft);
        fullImage.setTranslationY(mTop);
        fullImage.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void activityExitAnim(Runnable runnable) {
        fullImage.setPivotX(0);
        fullImage.setPivotY(0);
        fullImage.animate().scaleX(mScaleX).scaleY(mScaleY).translationX(mLeft).translationY(mTop).
                withEndAction(runnable).
                setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground, "alpha", 255, 0);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    @Override
    public void onBackPressed() {
        activityExitAnim(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    public void onImageClick() {
        activityExitAnim(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
