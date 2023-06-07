package com.xinwo.xinview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;


public class StatusTextView extends View {
    private final String TAG = "StatusTextView";

    private CharSequence mText;
    private int mTextSize;
    private int mSelectedColor;
    private int mUnSelectedColor;
    private boolean mSelected;
    private Paint mTextPaint;
    private int mTextHeight;
    private Paint.FontMetrics mFontMetrics;

    private final float HEIGHT_RATIO = 1.2f;
    private final float POINT_RATIO = 1.1f;
    private int mWidth;
    private int mHeight;
    private int mGravity;
    private int mTextWidth;
    private float mTextStartX;
    private float mTextStartY;

    public StatusTextView(Context context) {
        this(context, null);
    }

    public StatusTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatusTextView, defStyleAttr, 0);
        int n = typedArray.getIndexCount();

        TypedValue typedValue = new TypedValue();

        for (int i = 0; i < n; ++i) {
            if (i == R.styleable.StatusTextView_text) {
                mText = typedArray.getText(i);
            } else if (i == R.styleable.StatusTextView_textSize) {
                mTextSize = typedArray.getDimensionPixelSize(i, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f,
                        context.getResources().getDisplayMetrics()));
            } else if (i == R.styleable.StatusTextView_selected_color) {
                context.getTheme().resolveAttribute(R.color.colorPrimaryDark, typedValue, true);
                mSelectedColor = typedArray.getColor(i, typedValue.data);
            } else if (i == R.styleable.StatusTextView_unselected_color) {
                context.getTheme().resolveAttribute(R.color.colorPrimary, typedValue, true);
                mUnSelectedColor = typedArray.getColor(i, typedValue.data);
            } else if (i == R.styleable.StatusTextView_selected) {
                mSelected = typedArray.getBoolean(i, false);
            } else if (i == R.styleable.StatusTextView_android_gravity) {
                mGravity = typedArray.getInt(i, Gravity.RIGHT);
            }
        }

        typedArray.recycle();

        Log.e(TAG, "mText = " + mText.toString() + "     mTextSize = " + mTextSize);
        Log.e(TAG, "mSelectedColor = " + mSelectedColor + "     mUnSelectedColor = " + mUnSelectedColor);
        Log.e(TAG, "mSelected = " + mSelected);

        if (mGravity == Gravity.CENTER) {
            Log.e(TAG, "mGravity = CENTER");
        } else if (mGravity == Gravity.CENTER_HORIZONTAL) {
            Log.e(TAG, "mGravity = CENTER_HORIZONTAL");
        } else if (mGravity == Gravity.RIGHT) {
            Log.e(TAG, "mGravity = RIGHT");
        } else {
            Log.e(TAG, "mGravity = else");
        }

        initPaints();
    }

    private void initPaints() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mFontMetrics = mTextPaint.getFontMetrics();
        mTextWidth = (int) mTextPaint.measureText(mText.toString());
        mTextHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        Log.i(TAG, "onMeasure BEFORE== mWidth = " + mWidth + "    mHeight = " + mHeight);

        if (MeasureSpec.AT_MOST == widthMode) {
            Log.e(TAG, "WidthMode  AT_MOST");
        } else if (MeasureSpec.UNSPECIFIED == widthMode) {
            Log.e(TAG, "WidthMode  UNSPECIFIED");
        } else if (MeasureSpec.EXACTLY == widthMode) {
            Log.e(TAG, "WidthMode  EXACTLY");
        }


        if (widthMode != MeasureSpec.EXACTLY) {
            mWidth = (int) (mTextWidth + getPaddingLeft() + getPaddingRight());
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            mHeight = (int) Math.ceil(mTextHeight * HEIGHT_RATIO) + getPaddingTop() + getPaddingBottom();
        }

        Log.e(TAG, "onMeasure AFTER mWidth = " + mWidth + "    mHeight = " + mHeight);

        setMeasuredDimension(mWidth, mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSelected) {
            mTextPaint.setColor(mSelectedColor);
            mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            mTextPaint.setColor(mUnSelectedColor);
            mTextPaint.setTypeface(Typeface.DEFAULT);
        }

        mTextStartY = -mFontMetrics.top + getPaddingTop();

        if (mGravity == Gravity.CENTER_HORIZONTAL) {
            mTextStartX = (mWidth - mTextWidth) / 2;
        } else {
            mTextStartX = 0;
        }
        canvas.drawText(mText.toString(), mTextStartX, mTextStartY, mTextPaint);

        if (mSelected) {
            canvas.drawCircle(mWidth / 2, mTextHeight * POINT_RATIO + getPaddingTop(), mTextHeight * 0.1f, mTextPaint);
        }

    }

    public void setSelectedColor(int color) {
        mSelectedColor = color;
        invalidate();
    }

    public void setUnSelectedColor(int color) {
        mUnSelectedColor = color;
        invalidate();
    }

    public void setText(CharSequence text) {
        mText = text;
        invalidate();
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

}
