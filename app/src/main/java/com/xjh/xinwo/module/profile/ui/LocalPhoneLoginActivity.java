package com.xjh.xinwo.module.profile.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xinwo.log.LibSPUtils;
import com.xjh.xinwo.Constants;
import com.xjh.xinwo.R;
import com.xinwo.base.BaseLoginActivity;
import com.xjh.xinwo.enity.WXLoginBean;
import com.xinwo.network.ApiManager;
import com.xjh.xinwo.mvp.presenter.TagPresenter;
import com.xjh.xinwo.util.Decrypt;
import com.xjh.xinwo.util.JsonUtils;
import com.xjh.xinwo.view.ButtonStyle;


import java.util.Map;

public class LocalPhoneLoginActivity extends BaseLoginActivity implements View.OnClickListener {
    private final String TAG = "LocalPhoneLoginActivity";
    public final static int TAG_WX_LOGIN = 1;
    private IWXAPI api;
    private String wxCode;

    private TextView tvPhone;
    private TextView tvTelecom;
    private ButtonStyle btnLocalPhoneLogin;
    private ButtonStyle btnOtherPhoneLogin;
    private TextView tvBiXin;
    private TextView tvYinSi;
    private TextView tvTiaoKuan;
    private ImageView ivLoginWeChat;
    private TextView tvLoginTrouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_phone_login);
    }

    @Override
    protected void success(Object bean, int tag) {
        if (TAG_WX_LOGIN == tag) {
            WXLoginBean wxLoginBean = (WXLoginBean) bean;
            Decrypt.JWTParse(wxLoginBean.jwt_token);
            Log.e("JWT_CODE", wxLoginBean.jwt_token);
            LibSPUtils.put(Constants.JWT_CODE, wxLoginBean.jwt_token);
            Intent intent = new Intent(this, OtherPhoneLoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void error(Throwable e, int tag) {

    }

    @Override
    public Map<String, String> getParams(int tag) {
        ArrayMap<String, String> map = new ArrayMap<>();
        if (tag == TAG_WX_LOGIN) {
            map.put("appid", Constants.APP_ID);
            map.put("code", wxCode);

            return map;
        }
        return null;
    }

    @Override
    public void initData() {
        PermissionsUtil.requestPermission(getApplicationContext(),
                new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permission) {
                        Toast.makeText(getApplicationContext(), "获取存储权限", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permission) {
                        Toast.makeText(getApplicationContext(), "获取存储权限失败", Toast.LENGTH_LONG).show();
                    }
                },
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
    }

    @Override
    public void initPresenter() {
        mPresenter = new TagPresenter(getBaseContext(), this);
    }

    @Override
    public void initView() {
        super.initView();

        findViewById(R.id.ivClose).setOnClickListener(this);
        tvPhone = findViewById(R.id.tvPhone);
        tvTelecom = findViewById(R.id.tvTelecom);
        btnLocalPhoneLogin = findViewById(R.id.btnLocalPhoneLogin);
        btnLocalPhoneLogin.setOnClickListener(this);
        btnOtherPhoneLogin = findViewById(R.id.btnOtherPhoneLogin);
        btnOtherPhoneLogin.setOnClickListener(this);
        tvBiXin = findViewById(R.id.tvBiXin);
        tvBiXin.setOnClickListener(this);
        tvYinSi = findViewById(R.id.tvYinSi);
        tvYinSi.setOnClickListener(this);
        tvTiaoKuan = findViewById(R.id.tvTiaoKuan);
        tvTiaoKuan.setOnClickListener(this);
        ivLoginWeChat = findViewById(R.id.ivLoginWeChat);
        ivLoginWeChat.setOnClickListener(this);
        tvLoginTrouble = findViewById(R.id.tvLoginTrouble);
        tvLoginTrouble.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                finish();
                break;
            case R.id.btnLocalPhoneLogin:
                break;
            case R.id.btnOtherPhoneLogin:
                Intent intent = new Intent(getBaseContext(), OtherPhoneLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tvBiXin:
                break;
            case R.id.tvYinSi:
                break;
            case R.id.tvTiaoKuan:
                break;
            case R.id.ivLoginWeChat:
                // send oauth request
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact";
                req.state = "none";
                api.sendReq(req);
                break;
            case R.id.tvLoginTrouble:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");


        if (intent != null) {
            wxCode = intent.getStringExtra("code");
            Log.e("WXTOKEN", "TOKEN = " + wxCode);

            mPresenter.postBody(getHeaders(null), ApiManager.LOGIN_WX, TAG_WX_LOGIN, WXLoginBean.class, JsonUtils.parse2Json("appid", Constants.APP_ID, "code", wxCode));

        } else {
            Log.e("WXTOKEN", "TOKEN为空");
        }

    }
}
