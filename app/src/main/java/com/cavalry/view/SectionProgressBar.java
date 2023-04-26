package com.cavalry.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseLongArray;
import android.view.View;

import androidx.annotation.Nullable;

import com.xjh.xinwo.R;


/**
 * Created by 25623 on 2018/6/19.
 */

public class SectionProgressBar extends View {


    private SparseLongArray mSplitArray = new SparseLongArray();
    private int mWidth, mHeight;
    private Paint mPaint;
    private Paint mSplitPaint;
    private int section_firstProgressColor;
    private int section_secondProgressColor;
    private int section_splitLineColor;

    private long section_maxProgress;
    private long mCurrentProgress;
    private int mRealWidth;
    private int startX;
    private int stopX;
    private int startY;
    private int stopY;
    private int section_splitLineWidth;

    public SectionProgressBar(Context context) {
        this(context, null);
    }

    public SectionProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SectionProgressBar, defStyleAttr, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.SectionProgressBar_section_firstProgressColor:
                    section_firstProgressColor = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.SectionProgressBar_section_secondProgressColor:

                    section_secondProgressColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.SectionProgressBar_section_splitLineColor:
                    section_splitLineColor = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.SectionProgressBar_section_splitLineWidth:
                    section_splitLineWidth = a.getDimensionPixelSize(attr,8);
                    break;
            }

        }
        a.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


        mSplitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSplitPaint.setColor(section_splitLineColor);
        mSplitPaint.setStrokeWidth(section_splitLineWidth);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);


        if(widthSpecMode == MeasureSpec.EXACTLY){
            mWidth = widthSpecSize;
        }else{
            mWidth = 0;
        }

        if(heightSpecMode == MeasureSpec.EXACTLY){
            mHeight = heightSpecSize;
        }else{
            mHeight = 0;
        }

        setMeasuredDimension(mWidth, mHeight + getPaddingTop() + getPaddingBottom());

        mRealWidth = mWidth - getPaddingLeft() - getPaddingRight();
        startX = getPaddingLeft();
        stopX = mRealWidth + startX;
        startY = (int) (mHeight * 1.0f / 2 + 0.5f) + getPaddingTop();
        stopY = startY;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画section_firstProgressColor
        mPaint.setColor(section_firstProgressColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mHeight);
        canvas.drawLine(startX, startY , stopX, stopY, mPaint);


        if(section_maxProgress > 0 & mCurrentProgress > 0){
            //画section_secondProgressColor
            mPaint.setColor(section_secondProgressColor);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(mHeight);

            canvas.drawLine(startX, startY , startX + mHeight, stopY, mPaint);

            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            canvas.drawLine(startX + mHeight , startY , getProgressLength(mCurrentProgress), stopY, mPaint);

            //画section_splitLineColor
            for(int i = 0; i<mSplitArray.size(); i++){
                canvas.drawLine(startX + getProgressLength(mSplitArray.get(i)), getPaddingTop(),startX + getProgressLength(mSplitArray.get(i)), getPaddingTop() + mHeight, mSplitPaint);
            }
        }

    }


    private int getProgressLength(long position){
        return (int) (position * 1.0f / section_maxProgress * mWidth);
    }

    public void setMaxProgress(long progress){
        section_maxProgress = progress;
    }

    public void setCurrentProgress(long progress){
        mCurrentProgress = progress;
        invalidate();
    }

    public void addSection(long position){
        mSplitArray.put(mSplitArray.size(), position);
        invalidate();
    }

    public void increaseSection(long increasePosition){
        if(mSplitArray.size() == 0){
            mSplitArray.put(mSplitArray.size(), increasePosition);
        }else{
            mSplitArray.put(mSplitArray.size(), mSplitArray.get(mSplitArray.size()-1) + increasePosition);
        }
        invalidate();
    }

    public void removeLastSection(){
        if(mSplitArray.size() > 0){
            mSplitArray.removeAt(mSplitArray.size()-1);
            if(mSplitArray.size() == 0){
                mCurrentProgress =  0;
            }else{
                mCurrentProgress = mSplitArray.get(mSplitArray.size() - 1);
            }
            invalidate();
        }

    }

    public void clear(){
        if(mSplitArray != null){
            mSplitArray.clear();
            mCurrentProgress = 0;
            invalidate();
        }
    }
}
