package com.xinwo.social.groupchat.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.xinwo.social.R;
import com.xinwo.social.chat.entity.TotalGroupChatEntity;
import com.xinwo.social.groupchat.adapter.PagerLayoutManager;
import com.xinwo.social.groupchat.adapter.TotalGroupChatAdapter;
import com.xinwo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TotalGroupChatActivity extends BaseActivity {

    private RecyclerView rvTotalGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_group_chat);
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
        String[] colors = {
                "#FF0000",
                "#00FF00",
                "#0000FF",
                "#FFFF00",
                "#00FFFF"
        };

        List<TotalGroupChatEntity> totalGroupChatEntityList = new ArrayList<>();
        for(int i = 0; i<colors.length; ++i){
            totalGroupChatEntityList.add(new TotalGroupChatEntity(colors[i]));
        }

        rvTotalGroupChat = findViewById(R.id.rvTotalGroupChat);

        rvTotalGroupChat.setLayoutManager(new PagerLayoutManager(getBaseContext()));

        rvTotalGroupChat.setAdapter(new TotalGroupChatAdapter(totalGroupChatEntityList));
    }

    @Override
    public void loadData() {

    }
}
