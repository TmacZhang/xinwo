package com.xjh.xinwo.module.chat.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xjh.xinwo.R;


public class AudioVideoChatActivity extends AppCompatActivity {

    private View ivChatCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_video_chat);

        initView();
    }

    private void initView() {
        ivChatCancel = findViewById(R.id.ivChatCancel);

        ivChatCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
