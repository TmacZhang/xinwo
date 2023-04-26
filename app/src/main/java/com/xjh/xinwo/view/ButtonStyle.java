package com.xjh.xinwo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.xjh.xinwo.R;


/**
 * @author Lai
 */

public class ButtonStyle extends AppCompatButton {

    //按下颜色
    private int pressedColor;
    //当前颜色
    private int normalColor;
    //当前圆角
    private float currCorner;
    // 四边宽度
    private float strokeWidth;
    // 颜色
    private int strokeColor;

    GradientDrawable gradientDrawable;

    //按钮类型
    private  int type;

    boolean isTouchPass = true;

    public ButtonStyle(Context context) {
        this(context, null);
    }

    public ButtonStyle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }


    private void init() {
        setGravity(Gravity.CENTER);
        gradientDrawable = new GradientDrawable();
        //说明配置了快速按钮选项
        if(type!=0){
            setTextColor(Color.WHITE);
            switch (type){
                case 1:
                    normalColor = Color.parseColor("#5CB85C");
                    pressedColor= Color.parseColor("#449D44");
                    break;
                case 2:
                    normalColor = Color.parseColor("#5BC0DE");
                    pressedColor= Color.parseColor("#31B0D5");
                    break;
                case 3:
                    normalColor = Color.parseColor("#F0AD4E");
                    pressedColor= Color.parseColor("#EC971F");
                    break;
                case 4:
                    normalColor = Color.parseColor("#D9534F");
                    pressedColor= Color.parseColor("#C9302C");
                    break;
            }
        }

        gradientDrawable.setColor(normalColor);
        gradientDrawable.setStroke((int) strokeWidth, strokeColor);
        gradientDrawable.setCornerRadius(currCorner);
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                setBackgroundDrawable(gradientDrawable);
                return setColor(event.getAction());
            }
        });
        setBackgroundDrawable(gradientDrawable);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonStyle);
            int color = Color.parseColor("#AEDEF4");
            normalColor = typedArray.getColor(R.styleable.ButtonStyle_normal_color, color);
            strokeWidth = typedArray.getDimension(R.styleable.ButtonStyle_stroke_width, 0);
            strokeColor = typedArray.getColor(R.styleable.ButtonStyle_stroke_color, Color.TRANSPARENT);
            pressedColor = typedArray.getColor(R.styleable.ButtonStyle_press_color, Color.WHITE);
            currCorner = typedArray.getDimension(R.styleable.ButtonStyle_corner, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getContext().getResources().getDisplayMetrics()));
            type = typedArray.getInt(R.styleable.ButtonStyle_type, 0);
            typedArray.recycle();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        isTouchPass = false;
    }

    public boolean setColor(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                gradientDrawable.setColor(pressedColor);
                break;
            case MotionEvent.ACTION_UP:
                gradientDrawable.setColor(normalColor);
                break;
            case MotionEvent.ACTION_CANCEL:
                gradientDrawable.setColor(normalColor);
                break;
        }

        return isTouchPass;
    }

    /**
     * @return 获取按下颜色
     */
    public int getPressedColor() {
        return pressedColor;
    }

    /**
     * @param pressedColor 按下颜色设置
     */
    public void setPressedColor(int pressedColor) {
        this.pressedColor = getResources().getColor(pressedColor);
    }

    /**
     * @param pressedColor 设置按下颜色 例如：#ffffff
     */
    public void setPressedColor(String pressedColor) {
        this.pressedColor = Color.parseColor(pressedColor);
    }

    /**
     * @return 获取默认颜色
     */
    public int getNormalColor() {
        return normalColor;
    }

    /**
     * @param normalColor 设置默认颜色
     */
    public void setNormalColor(int normalColor) {
        this.normalColor = getResources().getColor(normalColor);
        if(gradientDrawable!=null)
            gradientDrawable.setColor(this.normalColor);
    }

    /**
     * @param normalColor 设置默认颜色 例如：#ffffff
     */
    public void setNormalColor(String normalColor) {
        this.normalColor = Color.parseColor(normalColor);
        if(gradientDrawable!=null)
            gradientDrawable.setColor(this.normalColor);
    }

    /**
     * @return 返回当前圆角大小
     */
    public float getCurrCorner() {
        return currCorner;
    }

    /**
     * @param currCorner 设置当前圆角
     */
    public void setCurrCorner(float currCorner) {
        this.currCorner = currCorner;
        if(gradientDrawable!=null)
           gradientDrawable.setCornerRadius(currCorner);
    }

    /**
     * @return 返回边框大小
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * @param strokeWidth 设置边框大小
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        if(gradientDrawable!=null)
            gradientDrawable.setStroke((int) strokeWidth, this.strokeColor);
    }

    /**
     * @return 返回边框颜色
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * @param strokeColor 设置边框颜色
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = getResources().getColor(strokeColor);
        if(gradientDrawable!=null)
            gradientDrawable.setStroke((int) strokeWidth, this.strokeColor);
    }

    /**
     * @param strokeColor 设置边框颜色 例如：#ffffff
     */
    public void setStrokeColor(String strokeColor) {
        this.strokeColor = Color.parseColor(strokeColor);
        if(gradientDrawable!=null)
            gradientDrawable.setStroke((int) strokeWidth, this.strokeColor);
    }
}
