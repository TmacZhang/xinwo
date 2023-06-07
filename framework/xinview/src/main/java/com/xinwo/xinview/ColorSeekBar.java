package com.xinwo.xinview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xinwo.xinview.history.EditStepChain;
import com.xinwo.xinview.history.EditStepStack;

public class ColorSeekBar extends View {
    private final String TAG = "ColorSeekBar";

    private Paint mLinePaint;
    private Paint mSliderPaint;
    private Rect mSlider;
    private int mCurrentProgress;
    private int mMaxProgress;
    private int mSliderHeight;
    private int mLineHeight;
    private int mLineWidth;
    private int mSliderWidth;
    private Paint mHistoryPaint;

    private final int colors[] = {
            Color.BLACK,
            Color.parseColor("#FF0000"),
            Color.parseColor("#00FF00"),
            Color.parseColor("#0000FF"),
            Color.parseColor("#323232"),
            Color.parseColor("#4e4e4e"),
            Color.parseColor("#f0f0f0")};
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private long mHistoryStartX;
    private long mHistoryEndX;
    private int mHistoryColor;
    private int mNewFilterIndex = -1;
    private int mNewFilterStartX;
    private boolean mStartNewFilter;
    private boolean mFromUser;

    public ColorSeekBar(Context context) {
        this(context, null);
    }

    public ColorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        mWidth = MeasureSpec.getSize(widthMeasureSpec);
//        mHeight = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(mWidth, mHeight);
//        Log.e(TAG,"SIZE onMeasure  mWidth = " + mWidth +"      mHeight = " + mHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG,"SIZE onSizeChanged w = " + w +"      h = " + h + "    oldw = " + oldw + " oldh = " + oldh);

        mSliderWidth = mSliderHeight = h;
        mLineHeight = (int) (mSliderHeight * 0.6f);
        mLineWidth = w;
        initPaints();
    }

    private void initPaints() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(mLineHeight);

        mSliderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSliderPaint.setColor(Color.RED);
        mSlider = new Rect(0, 0, mSliderWidth, mSliderHeight);

        mHistoryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHistoryPaint.setStrokeWidth(mLineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, mSliderHeight/2);
        canvas.drawLine(0, 0 , mLineWidth, 0, mLinePaint);

        //绘制滤镜历史
        EditStepChain editStepChain = EditStepStack.getInstance().getLastEditStepChain();
        if(mMaxProgress > 0 && editStepChain != null){
            int length = editStepChain.details.length;
            for(int i=0; i<length; i++){
                mHistoryStartX = (long) (1.0f * editStepChain.details[i].startTimestamp/mMaxProgress * mLineWidth);
                mHistoryEndX = (long) (1.0f * editStepChain.details[i].endTimestamp/mMaxProgress * mLineWidth);

                mHistoryColor = getColor(editStepChain.details[i].filterIndex);
                mHistoryPaint.setColor(mHistoryColor);
                Log.e(TAG,"History  i = " + i
                        + "   filterIndex = " + editStepChain.details[i].filterIndex
                + " mHistoryColor = " + mHistoryColor
                +"  (" + mHistoryStartX + ","  + mHistoryEndX + ")"
                +"  startTimestamp = " + editStepChain.details[i].startTimestamp
                + " endTimestamp = " + editStepChain.details[i].endTimestamp
                + " maxProgress = " + mMaxProgress);

                canvas.drawLine(mHistoryStartX, 0, mHistoryEndX, 0, mHistoryPaint);

            }
        }

        //绘制当前进度
        if(mStartNewFilter){
            mHistoryPaint.setColor(getColor(mNewFilterIndex));
            canvas.drawLine(mNewFilterStartX, 0, getCurrentProgressLength(), 0, mHistoryPaint);
        }else{//绘制

        }

        //绘制滑块
        canvas.translate(0, -mSliderHeight/2);
        canvas.drawRect(mSlider, mSliderPaint);
    }

    private int getColor(int filterIndex) {

        if(filterIndex > colors.length -1){
            return Color.BLACK;
        }
        return colors[filterIndex];
    }

    public void setProgress(int progress) {
        mFromUser = false;
        mCurrentProgress = progress;

        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onProgressChanged(this, mCurrentProgress, false);

        updateSliderPosition();
        postInvalidate();
    }

    public int getProgress(){
        return mCurrentProgress;
    }

    public void setMax(int progress){
        mMaxProgress = progress;
    }

    public void startNewFilter(int newFilterIndex){
        mStartNewFilter = true;
        mNewFilterIndex = newFilterIndex;
        mNewFilterStartX = getCurrentProgressLength();
    }

    public void endNewFilter(){
        mStartNewFilter = false;
        mNewFilterIndex = -1;
    }

    private int mTouchX;
    private int mTouchY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                Log.e(TAG,"onTouchEvent  ACTION_DOWN  mTouchX = " + mTouchX + " mTouchY = " + mTouchY);
                updateSlider(true, event);
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                updateSlider(true, event);
                Log.e(TAG,"onTouchEvent  ACTION_MOVE  mTouchX = " + mTouchX + " mTouchY = " + mTouchY);
                break;
            case MotionEvent.ACTION_UP:
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                updateSlider(true, event);
                Log.e(TAG,"onTouchEvent  ACTION_UP  mTouchX = " + mTouchX + " mTouchY = " + mTouchY);
                break;
        }
        return true;
    }

    private void updateSlider(boolean fromUser, MotionEvent event) {
        mFromUser = fromUser;

        mCurrentProgress = (int) (1.0f * mTouchX / mLineWidth * mMaxProgress);

        updateSliderPosition();

        if(mOnSeekBarChangeListener != null && fromUser){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mOnSeekBarChangeListener.onStartTrackingTouch(this);
                    mOnSeekBarChangeListener.onProgressChanged(this, mCurrentProgress, fromUser);
                break;
                case MotionEvent.ACTION_MOVE:
                    mOnSeekBarChangeListener.onProgressChanged(this, mCurrentProgress, fromUser);
                    break;
                case MotionEvent.ACTION_UP:
                    mOnSeekBarChangeListener.onProgressChanged(this, mCurrentProgress, fromUser);
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                    break;
            }
        }

        postInvalidate();
    }

    private void updateSliderPosition() {
        if(mSlider != null){
            int currentLength = getCurrentProgressLength();
            mSlider.set(currentLength - mSlider.width()/2, 0, currentLength + mSlider.width()/2, mSliderHeight);
        }
    }

    public int getCurrentProgressLength(){
        return (int) (1.0f * mCurrentProgress / mMaxProgress * mLineWidth);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(ColorSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(ColorSeekBar seekBar);

        void onStopTrackingTouch(ColorSeekBar seekBar);
    }
}
