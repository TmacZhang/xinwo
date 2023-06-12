package com.xinwo.produce.gestureheart.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xinwo.produce.R;
import com.xinwo.produce.gestureheart.activity.adapter.FilterAdapter;
import com.xinwo.produce.gestureheart.activity.bean.FilterTimeBean;
import com.xinwo.xinview.ColorSeekBar;
import com.xinwo.xinview.history.EditStepStack;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 25623 on 2018/8/27.
 */

public class FilterFragment extends Fragment {
    private final String TAG = "FilterFragment";

    private PreviewActivity mPreviewActivity;

    private final String[] FILTER_NAMES = {"1", "2", "3", "4", "5",
                                            "6", "7", "8", "9", "10"};
    private List<FilterTimeBean> mFilterBeanList;

    private boolean mFirstVisible = true;
    private ColorSeekBar mSeekBar;
    private int mCurrentProgress;

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
        View rootView = inflater.inflate(R.layout.fragment_filter, null);
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
        mFilterBeanList = new ArrayList<>();
        mFilterBeanList.add(new FilterTimeBean("1", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("2", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("3", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("4", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("5", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("6", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("7", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("8", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("9", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("10", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("11", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("12", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("13", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("14", R.mipmap.ic_launcher));
        mFilterBeanList.add(new FilterTimeBean("15", R.mipmap.ic_launcher));
    }

    private void initView(View rootView) {
        mSeekBar = (ColorSeekBar) rootView.findViewById(R.id.seekBarFilter);
        rootView.findViewById(R.id.tvRetrieveFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreviewActivity.glSurfaceView.retrieveFilter();
                mSeekBar.setProgress((int) EditStepStack.getInstance().getLastTimeStamp());
            }
        });


        RecyclerView rvFilter = (RecyclerView) rootView.findViewById(R.id.rvFilter);
        rvFilter.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        FilterAdapter mFilterAdapter = new FilterAdapter(R.layout.item_filter, mFilterBeanList);
        mFilterAdapter.setOnItemTouchListener(new FilterAdapter.OnItemTouchListener() {
            @Override
            public void onItemTouch(View v, int position, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mPreviewActivity.glSurfaceView.changeFilter(position+1);
                        mPreviewActivity.glSurfaceView.playVideo(true);
                        mSeekBar.startNewFilter(position + 1);
                        break;
                    case MotionEvent.ACTION_UP:
                        mPreviewActivity.glSurfaceView.pauseVideo();
                        mSeekBar.endNewFilter();
                        break;
                }
            }
        });
        rvFilter.setAdapter(mFilterAdapter);
    }

}
