package com.xinwo.social.profile.login;

import android.os.Bundle;
import android.view.View;

import com.xinwo.base.BaseLoginActivity;
import com.xinwo.social.R;

import java.util.Map;

public class PwdLoginActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_login);
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
        super.initView();

        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void loadData() {

    }
}
