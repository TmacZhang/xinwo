package com.xjh.xinwo.module.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xjh.xinwo.R;
import com.xinwo.base.BaseApplication;
import com.xjh.xinwo.module.chat.activity.AudioVideoChatActivity;


public class AudioVideoDialog extends Dialog {

    private final Context mContext;
    private TextView tvCancel;
    private TextView tvVideo;
    private TextView tvAudio;

    public AudioVideoDialog(@NonNull Context context) {
        this(context, 0);
    }

    public AudioVideoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_audio_video, null);
        setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = BaseApplication.dip2px(mContext.getResources().getDimension(R.dimen.dp_88));
        contentView.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.BOTTOM);
        initView();
    }

    private void initView() {
        tvCancel = findViewById(R.id.tvCancel);
        tvVideo = findViewById(R.id.tvVideo);
        tvAudio = findViewById(R.id.tvAudio);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AudioVideoChatActivity.class);
                mContext.startActivity(intent);
                cancel();
            }
        });


        tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AudioVideoChatActivity.class);
                mContext.startActivity(intent);
                cancel();
            }
        });
    }


}
