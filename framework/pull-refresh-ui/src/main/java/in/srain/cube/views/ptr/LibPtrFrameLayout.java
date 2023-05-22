package in.srain.cube.views.ptr;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Create by cavalry on 17-1-6
 */

public class LibPtrFrameLayout extends PtrFrameLayout {
    private String mHeaderText = "CAVALRY";
    private String mFooterText = "CAVALRY";
    private int mHeaderFooterTextColor = Color.WHITE;
    private int mHeaderFooterBgColor = Color.parseColor("#212121");

    public LibPtrFrameLayout(Context context) {
        this(context, null);
    }

    public LibPtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LibPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {

        StoreHouseHeader header = new StoreHouseHeader(getContext());

        //为了header,footer拉出的部分颜色一致, 给header设置了很大的paddingTop,给footer设置了很大的paddingBottom
        header.setPadding(0, PtrLocalDisplay.dp2px(600), 0, PtrLocalDisplay.dp2px(20));
        header.initWithString(mHeaderText);
        header.setTextColor(mHeaderFooterTextColor);
        header.setLineWidth(2);
        header.setBackgroundColor(mHeaderFooterBgColor);


        StoreHouseHeader footer = new StoreHouseHeader(getContext());

        //为了header,footer拉出的部分颜色一致, 给header设置了很大的paddingTop,给footer设置了很大的paddingBottom
        footer.setPadding(0, PtrLocalDisplay.dp2px(20), 0, PtrLocalDisplay.dp2px(600));
        footer.initWithString(mFooterText);
        footer.setTextColor(mHeaderFooterTextColor);
        footer.setLineWidth(2);
        footer.setBackgroundColor(mHeaderFooterBgColor);

        setDurationToCloseHeader(1500);
        setHeaderView(header);
        addPtrUIHandler(header);
        setFooterView(footer);
        addPtrUIHandler(footer);
    }
}
