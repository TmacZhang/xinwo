package com.xjh.xinwo.module.groupchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.xjh.xinwo.R;
import com.xjh.xinwo.base.BaseActivity;
import com.xjh.xinwo.enity.TotalGroupChatEntity;
import com.xjh.xinwo.module.groupchat.adapter.PagerLayoutManager;
import com.xjh.xinwo.module.groupchat.adapter.TotalGroupChatAdapter;

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
