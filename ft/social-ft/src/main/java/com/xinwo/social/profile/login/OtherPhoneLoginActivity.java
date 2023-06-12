package com.xinwo.social.profile.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.xinwo.log.LibSPUtils;
import com.xinwo.base.BaseLoginActivity;
import com.xinwo.social.R;
import com.xinwo.xinutil.JsonUtils;
import com.xjh.xinwo.mvp.model.BaseBean;
import com.xinwo.network.ApiManager;
import com.xjh.xinwo.mvp.presenter.TagPresenter;

import java.util.Map;

public class OtherPhoneLoginActivity extends BaseLoginActivity {
    private final String TAG = "OtherPhoneLoginActivity";
    public static  final String JWT_CODE = "jwt_code";
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    public static final String APP_ID = "wxa78ebfd006b71da9";
    public final static int TAG_GET_CODE = 1;
    public final static int TAG_SUBMIT = 2;

    private TextView tvGetCode;
    private EditText etPhone;
    private EditText etCode;
    private ImageView ivSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_phone_login);
    }

    @Override
    protected void success(Object bean, int tag) {
        if (tag == TAG_SUBMIT) {
            Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
        }
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
        mPresenter = new TagPresenter(getBaseContext(), this);
    }

    @Override
    public void initView() {
        super.initView();

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        findViewById(R.id.tvLoginPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PwdLoginActivity.class);
                startActivity(intent);
            }
        });

        tvGetCode = findViewById(R.id.tvGetCode);
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etPwd);
        ivSubmit = findViewById(R.id.ivSubmit);

        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.postBody(getHeaders(null),
                        ApiManager.GET_CODE,
                        TAG_GET_CODE,
                        BaseBean.class,
                        JsonUtils.parse2Json("phone", etPhone.getText().toString()));
            }
        });

        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.postBody(getHeaders(LibSPUtils.getString(JWT_CODE, "")),
                        ApiManager.BIND_PHONE,
                        TAG_SUBMIT,
                        new TypeReference<BaseBean>() {
                        }.getType(),
                        JsonUtils.parse2Json("appid", APP_ID, "code", etCode.getText().toString(),
                                "phone", etPhone.getText().toString()));
            }
        });
    }

    @Override
    public void loadData() {

    }


}
