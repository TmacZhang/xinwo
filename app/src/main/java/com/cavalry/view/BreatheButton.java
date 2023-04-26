package com.cavalry.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.xjh.xinwo.R;


/**
 * Created by 25623 on 2018/8/23.
 */

public class BreatheButton extends View {
    private final String TAG = "BreatheButton";

    private int inner_circle_radius;
    private float breathe_ratio;
    private int inner_circle_color;
    private int outer_circle_radius;
    private int outer_circle_color;

    private Paint mOuterCiclePaint;
    private Paint mInnerCirclePaint;

    private int mWidth;
    private int mHeight;
    private float mCurrentBreatheRatio;
    private ObjectAnimator mAnimator;
    private boolean isBreathe;

    public BreatheButton(Context context) {
        this(context, null);
    }

    public BreatheButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreatheButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BreatheButton, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int index = a.getIndex(i);
            switch (index)
            {
                case R.styleable.BreatheButton_outer_circle_color:
                    outer_circle_color = a.getColor(index, Color.WHITE);
                    Log.e(TAG,"outer_circle_color = " + outer_circle_color);
                    break;
                case R.styleable.BreatheButton_inner_circle_color:
                    inner_circle_color = a.getColor(index, Color.BLACK);
                    Log.e(TAG,"inner_circle_color = " + inner_circle_color);
                    break;
                case R.styleable.BreatheButton_outer_circle_radius:
                    outer_circle_radius = a.getDimensionPixelSize(index, 80);
                    Log.e(TAG,"outer_circle_radius = " + outer_circle_radius);
                    break;
                case R.styleable.BreatheButton_inner_circle_radius:
                    inner_circle_radius = a.getDimensionPixelSize(index,60);
                    Log.e(TAG,"inner_circle_radius = " + inner_circle_radius);
                    break;
                case R.styleable.BreatheButton_breathe_ratio:
                    breathe_ratio = a.getFloat(index, 1.2f);
                    Log.e(TAG,"breathe_ratio = " + breathe_ratio);
                    break;
            }

        }
        a.recycle();

        init();
    }

    private void init() {
        mOuterCiclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCiclePaint.setColor(outer_circle_color);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(inner_circle_color);

        mCurrentBreatheRatio = 1.0f;

        mAnimator = ObjectAnimator.ofFloat(this, "BreatheRatio", 1.0f, breathe_ratio);
//        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, breathe_ratio, 1.0f);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(!isBreathe && animation.getAnimatedFraction() < 0.05f){
                    animation.cancel();
                }
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = (int) (outer_circle_radius * breathe_ratio * 2 + 0.5f + 12);
        mHeight = mWidth;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mWidth/2, mWidth/2);
        canvas.drawCircle(0, 0, outer_circle_radius * mCurrentBreatheRatio, mOuterCiclePaint);
        canvas.drawCircle(0, 0, inner_circle_radius, mInnerCirclePaint);
    }

    public void setBreatheRatio(float ratio){
        mCurrentBreatheRatio = ratio;
        invalidate();
    }

    public void startBreathe(){
        isBreathe = true;
        mAnimator.start();
    }

    public void stopBreathe(){
        mAnimator.cancel();
        isBreathe = false;
    }

    /**
     * 延迟暂停，待圈回到初始状态停止动画
     */
    public void stopBreatheDelay(){
        isBreathe = false;
    }


    public float getBreatheRatio(){
        return mCurrentBreatheRatio;
    }

}
