package com.xjh.xinwo.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xjh.xinwo.Constants;
import com.xinwo.base.BaseActivity;
import com.xjh.xinwo.module.profile.ui.LocalPhoneLoginActivity;
import com.xjh.xinwo.mvp.presenter.TagPresenter;

import java.util.Map;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private final static String TAG = "WXEntryActivity";



    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private String wxCode;

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

    }

    @Override
    public void loadData() {

    }


    @Override
    protected void success(Object bean, int tag) {

    }

    @Override
    protected void error(Throwable e, int tag) {
    }


//    private static class MyHandler extends Handler {
//        private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;
//
//        public MyHandler(WXEntryActivity wxEntryActivity) {
//            wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
//
//            Log.e(TAG,"MyHandler constructor 执行了！！！");
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            int tag = msg.what;
//            switch (tag) {
//                case NetworkUtil.GET_TOKEN: {
//                    Bundle data = msg.getData();
//                    JSONObject json = null;
//                    try {
//                        json = new JSONObject(data.getString("result"));
//                        String openId, accessToken, refreshToken, scope;
//                        openId = json.getString("openid");
//                        accessToken = json.getString("access_token");
//                        refreshToken = json.getString("refresh_token");
//                        scope = json.getString("scope");
//                        Intent intent = new Intent(wxEntryActivityWeakReference.get(), ResultActivity.class);
//                        intent.putExtra("openId", openId);
//                        intent.putExtra("accessToken", accessToken);
//                        intent.putExtra("refreshToken", refreshToken);
//                        intent.putExtra("scope", scope);
//                        wxEntryActivityWeakReference.get().startActivity(intent);
//                    } catch (JSONException e) {
//                        Log.e("WXEntryActivity", e.getMessage());
//                    }
//                }
//            }
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);

        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * sendReq是第三方app主动发送消息给微信，发送完成之后会切回到第三方app界面
     * @param baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {
        Log.e(TAG, "onReq");
    }

    /**
     * sendResp是微信向第三方app请求数据
     **/
    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onResPPPPPPPPPPPPPPPPPp");

        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            Log.e(TAG, "onResPPPPPPPPPPPPPPPPPp    COMMAND_SENDAUTH");

            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            wxCode = authResp.code;

            Log.e(TAG,"onResp  authResp.code = " + authResp.code);



            Intent intent = new Intent(this, LocalPhoneLoginActivity.class);
            intent.putExtra("code", wxCode);
            startActivity(intent);



//            mPresenter.postData(ApiManager.LOGIN_WX, TAG_WX_LOGIN, WXLoginBean.class);

//            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
//                            "appid=%s&secret=%s&code=%s&grant_type=authorization_code", "wxa78ebfd006b71da9",
//                    "600b71ff9039d18c0492a10387580f80", code), NetworkUtil.GET_TOKEN);
        }


        finish();
    }




}
