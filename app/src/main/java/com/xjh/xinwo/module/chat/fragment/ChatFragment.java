package com.xjh.xinwo.module.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xjh.xinwo.R;
import com.xinwo.feed.base.BaseFragment;
import com.xjh.xinwo.mvp.model.BaseBean;
import com.xjh.xinwo.module.chat.activity.IMActivity;
import com.xjh.xinwo.module.chat.adapter.ChatAdapter;
import com.xjh.xinwo.module.chat.adapter.StoryAdapter;
import com.xjh.xinwo.module.profile.ui.LocalPhoneLoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChatFragment extends BaseFragment {

    private StoryAdapter mStoryAdapter;
    private RecyclerView rvStory;
    private ChatAdapter mChatAdapter;
    private RecyclerView rvChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
            return root;
    }


    @Override
    protected void success(Object bean, int tag) {

    }

    @Override
    protected void error(Throwable e, int tag) {

    }

    @Override
    public Map<String, String> getParams(int tag) {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        //------------------
        //  故事的RV
        //------------------
        rvStory = getView().findViewById(R.id.rvStory);
        rvStory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        if(mStoryAdapter == null){
            List<BaseBean> baseBeanList = new ArrayList<>();
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            mStoryAdapter = new StoryAdapter(R.layout.item_story,  baseBeanList);

            View header = LayoutInflater.from(getContext()).inflate(R.layout.header_story, null);

            mStoryAdapter.setHeaderView(header, 0, LinearLayout.HORIZONTAL);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(), FUDualInputToTextureExampleFragment.class);
//                    startActivity(intent);
                }
            });

//            mStoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    Intent intent = new Intent(getContext(), VideoDetailActivity.class);
//                    startActivity(intent);
//                }
//            });

            rvStory.setAdapter(mStoryAdapter);
        }else{
            mStoryAdapter.notifyDataSetChanged();
        }


        //------------------
        //  聊天的RV
        //------------------

        rvChat = getView().findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        if(mChatAdapter == null){
            List<BaseBean> baseBeanList = new ArrayList<>();
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            baseBeanList.add(new BaseBean());
            mChatAdapter = new ChatAdapter(R.layout.item_chat,  baseBeanList);
            mChatAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(getContext(), IMActivity.class);
                    startActivity(intent);
                }
            });
            rvChat.setAdapter(mChatAdapter);
        }else {
            mChatAdapter.notifyDataSetChanged();
        }


        ImageView ivPhoto = getView().findViewById(R.id.ivPhoto);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocalPhoneLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void loadData() {

    }
}
