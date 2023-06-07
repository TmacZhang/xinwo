package com.xjh.gestureheart.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xinwo.xinview.ColorSeekBar;
import com.xjh.gestureheart.activity.adapter.TimeAdapter;
import com.xjh.gestureheart.activity.bean.FilterTimeBean;
import com.xjh.xinwo.R;

import java.util.ArrayList;

/**
 * Created by 25623 on 2018/9/6.
 */

public class MagicFragment extends Fragment {
    private final String TAG = "MagicFragment";
    private PreviewActivity mPreviewActivity;

    private boolean mFirstVisible = true;
    private ColorSeekBar mSeekBar;
    private int mCurrentProgress;
    private ArrayList<FilterTimeBean> mMagicBeanList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPreviewActivity = (PreviewActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_magic, null);

        initView(rootView);

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        if(mFirstVisible){
            onVisible(true);
            mFirstVisible = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG,"onHiddenChanged  = " + hidden);
        if(hidden){
            onVisible(false);
        }else{
            onVisible(true);
        }
    }

    private void onVisible(boolean visible){
        if(visible){
            //可见的时候设置SeekBar
            mPreviewActivity.glSurfaceView.setSeekBar(mSeekBar);
            mPreviewActivity.glSurfaceView.setProgress(mCurrentProgress);
        }else{
            //不可见的时候，记录进度
            mCurrentProgress = mPreviewActivity.glSurfaceView.getProgress();
        }
    }

    private void initData() {
        mMagicBeanList = new ArrayList<>();
        mMagicBeanList.add(new FilterTimeBean("无", R.mipmap.ic_launcher));
        mMagicBeanList.add(new FilterTimeBean("粒子1", R.mipmap.ic_launcher));
    }

    private void initView(View rootView) {
        mSeekBar = (ColorSeekBar) rootView.findViewById(R.id.seekBarMagic);
//        mSeekBar.setOnSeekBarChangeListener(new ColorSeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(ColorSeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(ColorSeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(ColorSeekBar seekBar) {
//                if(mShakeChoosed)
//                    ShakeStack.getInstance().setShakeTimestamp(seekBar.getProgress());
//            }
//        });

        rootView.findViewById(R.id.tvRetrieveMagic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreviewActivity.glSurfaceView.retrieveParticles();
            }
        });

        RecyclerView rvFilter = (RecyclerView) rootView.findViewById(R.id.rvMagic);
        rvFilter.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TimeAdapter mMagicAdapter = new TimeAdapter(R.layout.item_filter, mMagicBeanList);
        mMagicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0://无粒子效果
                        mPreviewActivity.glSurfaceView.enableNewFilter(false);
                        break;
                    case 1://粒子效果1
                        mPreviewActivity.glSurfaceView.enableNewFilter(true);
                        break;
                }
            }
        });
        rvFilter.setAdapter(mMagicAdapter);
    }


}
