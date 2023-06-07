package com.xjh.gestureheart.record;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xinwo.xinview.BreatheButton;
import com.xinwo.xinview.SectionProgressBar;
import com.xjh.gestureheart.record.encoder.TextureMovieEncoder;
import com.xjh.gestureheart.record.view.EffectAndFilterSelectAdapter;
import com.xjh.xinwo.R;
import com.xjh.xinwo.module.MainActivity;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


/**
 * Base Acitivity, 负责界面UI的处理
 * Created by lirui on 2017/1/19.
 */

public abstract class FUBaseUIFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "FUBaseUIFragment";
    public final long MAX_RECORD_DURATION_MILLIS = 15000;

    private RecyclerView mEffectRecyclerView;
    private EffectAndFilterSelectAdapter mEffectRecyclerAdapter;
    private RecyclerView mEffectRecyclerViewTmp;
    private EffectAndFilterSelectAdapter mEffectRecyclerAdapterTmp;

    private LinearLayout mEffectSelect;
    private LinearLayout mSkinBeautySelect;
    private LinearLayout mFaceShapeSelect;

    private Button mChooseEffectBtn;
    private Button mChooseFilterBtn;
    private Button mChooseBeautyFilterBtn;
    private Button mChooseSkinBeautyBtn;
    private Button mChooseFaceShapeBtn;

    private DiscreteSeekBar filterLevelSeekbar;

    private TextView[] mBlurLevels;
    private int[] BLUR_LEVEL_TV_ID = {R.id.blur_level0, R.id.blur_level1, R.id.blur_level2,
            R.id.blur_level3, R.id.blur_level4, R.id.blur_level5, R.id.blur_level6};

    private TextView mFaceShape0Nvshen;
    private TextView mFaceShape1Wanghong;
    private TextView mFaceShape2Ziran;
    private TextView mFaceShape3Default;

    protected BreatheButton mRecordingBtn;
    private int mRecordStatus = 0;
    private final int STATUS_RECORDING = 1;
    private final int STATUS_STOP = 2;
    private final int STATUS_PAUSE = 3;

    protected TextView tvHint;
    protected TextView isCalibratingText;

    //顶部的几个View
    private View ivMenu;
    private PopupWindow mPopupMenu;
    private View mPopupMenuContentView;

    private ImageView ivSpeed;
    private PopupWindow mPopupSpeed;
    private View mPopupSpeedContentView;

    private ImageView ivMusic;


    protected SectionProgressBar sectionProgressBar;
//    private BottomSheetBehavior<View> behavior;
    private View mMainBottom;
    private View layoutRecord;
    private View layoutRoot;
    private View containerBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_base, container, false);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getActivity().getWindow().setAttributes(params);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTopViews();
        initRecordViews();

        initBottomViews();

        initToolRecycleView();
        initOtherRecycleView();

        mChooseEffectBtn = (Button) getView().findViewById(R.id.btn_choose_effect);
        mChooseFilterBtn = (Button) getView().findViewById(R.id.btn_choose_filter);
        mChooseBeautyFilterBtn = (Button) getView().findViewById(R.id.btn_choose_beauty_filter);
        mChooseSkinBeautyBtn = (Button) getView().findViewById(R.id.btn_choose_skin_beauty);
        mChooseFaceShapeBtn = (Button) getView().findViewById(R.id.btn_choose_face_shape);

        mFaceShape0Nvshen = (TextView) getView().findViewById(R.id.face_shape_0_nvshen);
        mFaceShape1Wanghong = (TextView) getView().findViewById(R.id.face_shape_1_wanghong);
        mFaceShape2Ziran = (TextView) getView().findViewById(R.id.face_shape_2_ziran);
        mFaceShape3Default = (TextView) getView().findViewById(R.id.face_shape_3_default);

        mEffectSelect = (LinearLayout) getView().findViewById(R.id.effect_select_block);
        mSkinBeautySelect = (LinearLayout) getView().findViewById(R.id.skin_beauty_select_block);
        mFaceShapeSelect = (LinearLayout) getView().findViewById(R.id.lin_face_shape);

        mBlurLevels = new TextView[BLUR_LEVEL_TV_ID.length];
        for (int i = 0; i < BLUR_LEVEL_TV_ID.length; i++) {
            final int level = i;
            mBlurLevels[i] = (TextView) getView().findViewById(BLUR_LEVEL_TV_ID[i]);
            mBlurLevels[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBlurLevelTextBackground(mBlurLevels[level]);
                    onBlurLevelSelected(level);
                }
            });
        }

        filterLevelSeekbar = (DiscreteSeekBar) getView().findViewById(R.id.filter_level_seekbar);
        filterLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                Log.d(TAG, "filter level selected " + value);
                onFilterLevelSelected(value, 100);
                mEffectRecyclerAdapter.setFilterLevels(value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        Switch mAllBlurLevelSwitch = (Switch) getView().findViewById(R.id.all_blur_level);
        mAllBlurLevelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onALLBlurLevelSelected(isChecked ? 1 : 0);
            }
        });

        DiscreteSeekBar colorLevelSeekbar = (DiscreteSeekBar) getView().findViewById(R.id.color_level_seekbar);
        colorLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onColorLevelSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar cheekThinSeekbar = (DiscreteSeekBar) getView().findViewById(R.id.cheekthin_level_seekbar);
        cheekThinSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onCheekThinSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar enlargeEyeSeekbar = (DiscreteSeekBar) getView().findViewById(R.id.enlarge_eye_level_seekbar);
        enlargeEyeSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onEnlargeEyeSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar faceShapeLevelSeekbar = (DiscreteSeekBar) getView().findViewById(R.id.face_shape_seekbar);
        faceShapeLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onFaceShapeLevelSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar redLevelShapeLevelSeekbar = (DiscreteSeekBar) getView().findViewById(R.id.red_level_seekbar);
        redLevelShapeLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onRedLevelSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        mRecordingBtn = (BreatheButton) getView().findViewById(R.id.btn_recording);
//        mRecordingBtn.setOnClickListener(this);
        mRecordingBtn.setOnTouchListener(mBreathOnTouchListener);
        tvHint = (TextView) getView().findViewById(R.id.hint_text);
        isCalibratingText = (TextView) getView().findViewById(R.id.is_calibrating_text);
        sectionProgressBar = (SectionProgressBar) getView().findViewById(R.id.sectionProgressBar);
        sectionProgressBar.setMaxProgress(MAX_RECORD_DURATION_MILLIS);//15秒

        mMainBottom = getView().findViewById(R.id.main_bottom);
        layoutRecord = getView().findViewById(R.id.layoutRecord);
        getView().findViewById(R.id.layoutRoot).setOnClickListener(this);
    }

    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    private void initToolRecycleView() {
        mEffectRecyclerView = (RecyclerView) getView().findViewById(R.id.effect_recycle_view);
        mEffectRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
//        mEffectRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mEffectRecyclerAdapter = new EffectAndFilterSelectAdapter(mEffectRecyclerView, EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT);
        mEffectRecyclerAdapter.setOnItemSelectedListener(new EffectAndFilterSelectAdapter.OnItemSelectedListener() {
            @Override
            public void onEffectItemSelected(int itemPosition) {
                Log.d(TAG, "effect item selected " + itemPosition);
                onEffectSelected(EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[itemPosition]);
                showHintText(mEffectRecyclerAdapter.getHintStringByPosition(itemPosition));
            }

            @Override
            public void onFilterItemSelected(int itemPosition, int filterLevel) {
                Log.d(TAG, "filter item selected " + itemPosition);
                onFilterSelected(EffectAndFilterSelectAdapter.FILTERS_NAME[itemPosition]);
                filterLevelSeekbar.setProgress(filterLevel);
            }

            @Override
            public void onBeautyFilterItemSelected(int itemPosition, int filterLevel) {
                Log.d(TAG, "beauty filter item selected " + itemPosition);
                onFilterSelected(EffectAndFilterSelectAdapter.BEAUTY_FILTERS_NAME[itemPosition]);
                filterLevelSeekbar.setProgress(filterLevel);
            }
        });
        mEffectRecyclerView.setAdapter(mEffectRecyclerAdapter);
    }

    private void initOtherRecycleView() {
        mEffectRecyclerViewTmp = (RecyclerView) getView().findViewById(R.id.effect_recycle_view_tmp);
        mEffectRecyclerViewTmp.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mEffectRecyclerAdapterTmp = new EffectAndFilterSelectAdapter(mEffectRecyclerViewTmp, EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_FILTER);
        mEffectRecyclerAdapterTmp.setOnItemSelectedListener(new EffectAndFilterSelectAdapter.OnItemSelectedListener() {
            @Override
            public void onEffectItemSelected(int itemPosition) {
                Log.d(TAG, "effect item selected " + itemPosition);
                onEffectSelected(EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[itemPosition]);
                showHintText(mEffectRecyclerAdapterTmp.getHintStringByPosition(itemPosition));
            }

            @Override
            public void onFilterItemSelected(int itemPosition, int filterLevel) {
                Log.d(TAG, "filter item selected " + itemPosition);
                onFilterSelected(EffectAndFilterSelectAdapter.FILTERS_NAME[itemPosition]);
                filterLevelSeekbar.setProgress(filterLevel);
            }

            @Override
            public void onBeautyFilterItemSelected(int itemPosition, int filterLevel) {
                Log.d(TAG, "beauty filter item selected " + itemPosition);
                onFilterSelected(EffectAndFilterSelectAdapter.BEAUTY_FILTERS_NAME[itemPosition]);
                filterLevelSeekbar.setProgress(filterLevel);
            }
        });
        mEffectRecyclerViewTmp.setAdapter(mEffectRecyclerAdapterTmp);
    }


    /**
     * 初始化录制按钮周围的几个按钮
     */
    private void initRecordViews() {
        getView().findViewById(R.id.ivDelete).setOnClickListener(this);
        getView().findViewById(R.id.ivSticker).setOnClickListener(this);
        getView().findViewById(R.id.ivMagic).setOnClickListener(this);
    }

    /**
     * 初始化顶部的几个View
     */
    private void initTopViews() {
        getView().findViewById(R.id.ivBack).setOnClickListener(this);
        getView().findViewById(R.id.ivChangeCamera).setOnClickListener(this);
        ivMenu = getView().findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(this);

        ivSpeed = (ImageView) getView().findViewById(R.id.ivSpeed);
        ivSpeed.setOnClickListener(this);

        ivMusic = (ImageView) getView().findViewById(R.id.ivMusic);
        ivMusic.setOnClickListener(this);


        getView().findViewById(R.id.tvNext).setOnClickListener(this);
    }

    private void initBottomViews(){
        containerBottom = getView().findViewById(R.id.containerBottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_effect:
                setEffectFilterBeautyChooseBtnTextColor(mChooseEffectBtn);
                setEffectFilterBeautyChooseBlock(mEffectSelect);
                mEffectRecyclerAdapter.setOwnerRecyclerViewType(EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT);
                filterLevelSeekbar.setVisibility(View.GONE);
                break;
            case R.id.btn_choose_filter:
                setEffectFilterBeautyChooseBtnTextColor(mChooseFilterBtn);
                setEffectFilterBeautyChooseBlock(mEffectSelect);
                mEffectRecyclerAdapterTmp.setOwnerRecyclerViewType(EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_FILTER);
                filterLevelSeekbar.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_choose_beauty_filter:
                setEffectFilterBeautyChooseBtnTextColor(mChooseBeautyFilterBtn);
                setEffectFilterBeautyChooseBlock(mEffectSelect);
                mEffectRecyclerAdapterTmp.setOwnerRecyclerViewType(EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_BEAUTY_FILTER);
                filterLevelSeekbar.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_choose_skin_beauty:
                setEffectFilterBeautyChooseBtnTextColor(mChooseSkinBeautyBtn);
                setEffectFilterBeautyChooseBlock(mSkinBeautySelect);
                break;
            case R.id.btn_choose_face_shape:
                setEffectFilterBeautyChooseBtnTextColor(mChooseFaceShapeBtn);
                setEffectFilterBeautyChooseBlock(mFaceShapeSelect);
                break;
//            case R.id.btn_recording:
////                if (mRecordStatus != STATUS_RECORDING) {
////                    mRecordingBtn.startBreathe();
////                    onStartRecording();
////                    mRecordStatus = STATUS_RECORDING;
////                } else {
////                    mRecordingBtn.stopBreatheDelay();
////                    onStopRecording();
////                    mRecordStatus = STATUS_STOP;
////                }
////                break;
            case R.id.face_shape_0_nvshen:
                setFaceShapeBackground(mFaceShape0Nvshen);
                onFaceShapeSelected(0);
                break;
            case R.id.face_shape_1_wanghong:
                setFaceShapeBackground(mFaceShape1Wanghong);
                onFaceShapeSelected(1);
                break;
            case R.id.face_shape_2_ziran:
                setFaceShapeBackground(mFaceShape2Ziran);
                onFaceShapeSelected(2);
                break;
            case R.id.face_shape_3_default:
                setFaceShapeBackground(mFaceShape3Default);
                onFaceShapeSelected(3);
                break;
            /** 顶部的几个View **/
            case R.id.ivBack:
                Log.e(TAG,"点击了ivBack");
                ((MainActivity)getActivity()).switchToLastTab();
                break;
            case R.id.ivChangeCamera:
                onCameraChange();
                break;
            case R.id.ivMenu:
                togglePopupMenu();
                break;
            case R.id.ivSpeed:
                togglePopupSpeed();
                break;
            case R.id.ivMusic:
                onMusicClick();
                break;
            case R.id.tvNext:
                onNextClick();
                break;
            /**   录制按钮周围的几个按钮  **/
            case R.id.ivDelete:
                onDeleteClick();
                break;
            case R.id.ivSticker:
                layoutRecord.setVisibility(View.INVISIBLE);
                mEffectRecyclerView.setVisibility(View.VISIBLE);
                mMainBottom.setVisibility(View.INVISIBLE);
                animateBottom(containerBottom, TYPE_ANIMATE_SHOW);
                break;
            case R.id.ivMagic:
                layoutRecord.setVisibility(View.INVISIBLE);
                mEffectRecyclerView.setVisibility(View.INVISIBLE);
                mMainBottom.setVisibility(View.VISIBLE);
                animateBottom(containerBottom, TYPE_ANIMATE_SHOW);
                break;
            case R.id.layoutRoot:
                if(layoutRecord.getVisibility() == View.INVISIBLE){
                    animateBottom(containerBottom, TYPE_ANIMATE_HIDE);

                }
                break;
        }
    }


    private final int TYPE_ANIMATE_SHOW = 1;
    private final int TYPE_ANIMATE_HIDE = 2;
    private ObjectAnimator bottomOA = null;

    private void animateBottom(final View target, final int type){
//        if(bottomOA == null){
            bottomOA = new ObjectAnimator();
            bottomOA.setTarget(target);
            bottomOA.setPropertyName("translationY");
            bottomOA.setDuration(500);
            bottomOA.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.e(TAG,"animateBottom --> onAnimationEnd --> type = " + type);
                    if(TYPE_ANIMATE_HIDE == type){
                        layoutRecord.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
//        }

        if(TYPE_ANIMATE_SHOW == type){
            containerBottom.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"animateBottom --> target.getMeasuredHeight() = " + target.getMeasuredHeight());
                    bottomOA.setFloatValues(0, - target.getMeasuredHeight());
                    bottomOA.start();
                }
            });
        }else{
            containerBottom.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"animateBottom --> target.getMeasuredHeight() = " + target.getMeasuredHeight());
                    bottomOA.setFloatValues(- target.getMeasuredHeight(), 0);
                    bottomOA.start();
                }
            });
        }
    }

    private View.OnTouchListener mBreathOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if (mRecordStatus != STATUS_RECORDING) {
                        mRecordingBtn.startBreathe();
                        onStartRecording();
                        mRecordStatus = STATUS_RECORDING;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(mRecordStatus == STATUS_RECORDING) {
                        mRecordingBtn.stopBreatheDelay();
                        onStopRecording();
                        mRecordStatus = STATUS_STOP;
                    }
                    break;
            }
            return true;
        }
    };



    private  void togglePopupMenu(){
        if(mPopupMenu == null){
            mPopupMenuContentView = LayoutInflater.from(getContext()).inflate(R.layout.popup_menu, null);
            mPopupMenu = new PopupWindow(getContext());
            mPopupMenu.setContentView(mPopupMenuContentView);
            mPopupMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupMenu.setOutsideTouchable(true);

            mPopupMenuContentView.findViewById(R.id.ivMenuFlash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onToggleFlash();
                    mPopupMenu.dismiss();
                }
            });

            mPopupMenuContentView.findViewById(R.id.ivMenuBeauty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onToggleBeauty();
                    mPopupMenu.dismiss();
                }
            });
        }

        if(mPopupMenu.isShowing()){
            mPopupMenu.dismiss();
        }else{
            mPopupMenuContentView.measure(-1,-1);
            Log.e(TAG,"mPopupMenuContentView.getMeasuredWidth() = " + mPopupMenuContentView.getMeasuredWidth() +"   ivMenu.getWidth() = " + ivMenu.getWidth());
            mPopupMenu.showAsDropDown(ivMenu, (ivMenu.getWidth()-mPopupMenuContentView.getMeasuredWidth())/2, 0);
        }

    }


    private  void togglePopupSpeed(){
        if(mPopupSpeed == null){
            mPopupSpeedContentView = LayoutInflater.from(getContext()).inflate(R.layout.popup_speed, null);
            mPopupSpeed = new PopupWindow(getContext());
            mPopupSpeed.setContentView(mPopupSpeedContentView);
            mPopupSpeed.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupSpeed.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupSpeed.setOutsideTouchable(true);

            mPopupSpeedContentView.findViewById(R.id.ivSpeedLower).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeSpeed(TextureMovieEncoder.SPEED_LOWER);
                    mPopupSpeed.dismiss();
                }
            });

            mPopupSpeedContentView.findViewById(R.id.ivSpeedLow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeSpeed(TextureMovieEncoder.SPEED_LOW);
                    mPopupSpeed.dismiss();
                }
            });

            mPopupSpeedContentView.findViewById(R.id.ivSpeedNormal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeSpeed(TextureMovieEncoder.SPEED_NORMAL);
                    mPopupSpeed.dismiss();
                }
            });

            mPopupSpeedContentView.findViewById(R.id.ivSpeedFast).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeSpeed(TextureMovieEncoder.SPEED_FAST);
                    mPopupSpeed.dismiss();
                }
            });

            mPopupSpeedContentView.findViewById(R.id.ivSpeedFaster).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeSpeed(TextureMovieEncoder.SPEED_FASTER);
                    mPopupSpeed.dismiss();
                }
            });
        }

        if(mPopupSpeed.isShowing()){
            mPopupSpeed.dismiss();
        }else{
            mPopupSpeedContentView.measure(-1,-1);
            Log.e(TAG,"mPopupSpeedContentView.getMeasuredWidth() = " + mPopupSpeedContentView.getMeasuredWidth());
            mPopupSpeed.showAsDropDown(ivSpeed, (ivSpeed.getWidth()-mPopupSpeedContentView.getMeasuredWidth())/2, 0);
        }

    }

    Runnable resetHintRunnable = new Runnable() {
        @Override
        public void run() {
            tvHint.setText("");
            tvHint.setVisibility(View.GONE);
        }
    };

    public void showHintText(String hint) {
        if (tvHint != null) {
            tvHint.removeCallbacks(resetHintRunnable);
            tvHint.setText(hint);
            if (hint.isEmpty()) {
                tvHint.setVisibility(View.GONE);
            } else {
                tvHint.setVisibility(View.VISIBLE);
            }
        }
        tvHint.postDelayed(resetHintRunnable, 5000);
    }

    private void setBlurLevelTextBackground(TextView tv) {
        mBlurLevels[0].setBackground(getResources().getDrawable(R.drawable.zero_blur_level_item_unselected));
        for (int i = 1; i < BLUR_LEVEL_TV_ID.length; i++) {
            mBlurLevels[i].setBackground(getResources().getDrawable(R.drawable.blur_level_item_unselected));
        }
        if (tv == mBlurLevels[0]) {
            tv.setBackground(getResources().getDrawable(R.drawable.zero_blur_level_item_selected));
        } else {
            tv.setBackground(getResources().getDrawable(R.drawable.blur_level_item_selected));
        }
    }

    private void setFaceShapeBackground(TextView tv) {
        mFaceShape0Nvshen.setBackground(getResources().getDrawable(R.color.unselect_gray));
        mFaceShape1Wanghong.setBackground(getResources().getDrawable(R.color.unselect_gray));
        mFaceShape2Ziran.setBackground(getResources().getDrawable(R.color.unselect_gray));
        mFaceShape3Default.setBackground(getResources().getDrawable(R.color.unselect_gray));
        tv.setBackground(getResources().getDrawable(R.color.faceunityYellow));
    }

    private void setEffectFilterBeautyChooseBlock(View v) {
        mEffectSelect.setVisibility(View.GONE);
        mSkinBeautySelect.setVisibility(View.GONE);
        mFaceShapeSelect.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    private void setEffectFilterBeautyChooseBtnTextColor(Button selectedBtn) {
        mChooseEffectBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseFilterBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseBeautyFilterBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseSkinBeautyBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseFaceShapeBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        selectedBtn.setTextColor(getResources().getColor(R.color.faceunityYellow));
    }

    /**
     * 道具贴纸选择
     *
     * @param effectItemName 道具贴纸文件名
     */
    abstract protected void onEffectSelected(String effectItemName);

    /**
     * 滤镜强度
     *
     * @param progress 滤镜强度滑动条进度
     * @param max      滤镜强度滑动条最大值
     */
    abstract protected void onFilterLevelSelected(int progress, int max);

    /**
     * 滤镜选择
     *
     * @param filterName 滤镜名称
     */
    abstract protected void onFilterSelected(String filterName);

    /**
     * 磨皮选择
     *
     * @param level 磨皮level
     */
    abstract protected void onBlurLevelSelected(int level);

    /**
     * 精准磨皮
     *
     * @param isAll 是否开启精准磨皮（0关闭 1开启）
     */
    abstract protected void onALLBlurLevelSelected(int isAll);

    /**
     * 美白选择
     *
     * @param progress 美白滑动条进度
     * @param max      美白滑动条最大值
     */
    abstract protected void onColorLevelSelected(int progress, int max);

    /**
     * 瘦脸选择
     *
     * @param progress 瘦脸滑动进度
     * @param max      瘦脸滑动条最大值
     */
    abstract protected void onCheekThinSelected(int progress, int max);

    /**
     * 大眼选择
     *
     * @param progress 大眼滑动进度
     * @param max      大眼滑动条最大值
     */
    abstract protected void onEnlargeEyeSelected(int progress, int max);

    /**
     * 相机切换
     */
    abstract protected void onCameraChange();

    /**
     * 开始录制
     */
    abstract protected void onStartRecording();

    /**
     * 暂停录制
     */
    protected abstract void onPauseRecording();

    /**
     * 继续录制
     */
    protected abstract void onResumeRecording();


    /**
     * 停止录制
     */
    abstract protected void onStopRecording();

    /**
     * 脸型选择
     */
    abstract protected void onFaceShapeSelected(int faceShape);

    /**
     * 美型程度选择
     */
    abstract protected void onFaceShapeLevelSelected(int progress, int max);

    /**
     * 美白程度选择
     */
    abstract protected void onRedLevelSelected(int progress, int max);

    /**
     * 开关闪光灯
     */
    protected abstract void onToggleFlash();

    protected abstract void onToggleBeauty();

    protected abstract void onChangeSpeed(@TextureMovieEncoder.Speed int speed);

    protected abstract void onMusicClick();

    /**
     * 下一步
     */
    protected abstract void onNextClick();


    /**
     * 回删
     */
    protected abstract void onDeleteClick();
}
