package com.xjh.xinwo.base;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;

import com.cavalry.androidlib.toolbox.exception.LibException;
import com.cavalry.androidlib.toolbox.utils.LibLog;
import com.cavalry.androidlib.toolbox.utils.LibSPUtils;
import com.cavalry.androidlib.toolbox.utils.LibToastUtils;
import com.cavalry.androidlib.ui.activity.LibRLBaseActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.xjh.xinwo.BuildConfig;
import com.xjh.xinwo.Constants;
import com.xjh.xinwo.enity.base.BaseBean;
import com.xjh.xinwo.mvp.presenter.TagPresenter;

import java.util.Map;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * <p>1.具有refresh/loadmore功能, 默认为关闭状态, 子类通过{@link #setRLMode(PtrFrameLayout.Mode)}设置</p>
 * <p>2.带有页码mPageNo. 在{@link #onRefreshStart(PtrFrameLayout)}时自动mPageNo=0;
 *      在{@link #onError(Throwable, int)}中, 若isLoadingMore(), 自动 --mPageNo</p>
 * <p>3.在{@link #success(Object, int)}之后自动{@link #refreshOrLoadMoreComplete()}</p>
 * <p>4.在{@link #error(Throwable, int)}之前自动{@link #refreshOrLoadMoreComplete()}</p>
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */
public abstract class BaseLoginActivity extends LibRLBaseActivity {
    protected TagPresenter mPresenter;
    protected final int PAGE_SIZE = 20;
    protected int mPageNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRLMode(PtrFrameLayout.Mode.NONE);//设置位没有refresh/loadmore功能, 这样就不会初始化RL控件. 子类可重新设置RL功能
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null)
            mPresenter.detachView();
    }


    @Override
    public void onSuccess(Object bean, int tag) {
        BaseBean baseBean = (BaseBean) bean;
        if(baseBean.message.code == 200){
            success(bean,tag);

            afterSuccess(bean, tag);
        }else{
            onError(new LibException(baseBean.message.code, baseBean.message.msg), tag);
        }

    }

    private void afterSuccess(Object bean, int tag) {
        if(isRefreshingOrLoadingMore()){
            refreshOrLoadMoreComplete();
        }
    }


    @Override
    public void onError(Throwable e, int tag) {
        beforeError(e,tag);
        error(e,tag);
    }

    private void beforeError(Throwable e, int tag) {
        if(BuildConfig.DEBUG){
            e.printStackTrace();
        }
        if (isLoadingMore() && mPageNo>0){
            --mPageNo;
        }
        if(isRefreshingOrLoadingMore()){
            refreshOrLoadMoreComplete();
        }
    }

    protected abstract void success(Object bean, int tag);

    protected abstract void error(Throwable e, int tag);

    //RL的几个方法覆写为非抽象
    /**
     * {@inheritDoc}
     * @return
     */
    protected View getRLView(){return null;}

    /**
     * 已将mPageNo置为0
     * @return
     */
    protected void onRefreshStart(PtrFrameLayout frame){
        mPageNo = 0;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    protected void onLoadMoreStart(PtrFrameLayout frame){}

    protected Map<String, String> getHeaders(String jwt_code){
        ArrayMap<String, String> headers = new ArrayMap<>();
        headers.put("Content-Type", "application/json");
        if(jwt_code != null){
            headers.put("Authorization", jwt_code);
        }

        return headers;
    }

    @Override
    public void initView() {
        int[] colors = new int[]{Color.parseColor("#FF805E"), Color.parseColor("#FF5C6D"),
                Color.parseColor("#FF3C7B")};
        GradientDrawable linearDrawable = new GradientDrawable();
        linearDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        linearDrawable.setColors(colors);
        linearDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mContentView.setBackground(linearDrawable);
    }

    /**
     * 网易云信登录
     * @param account
     * @param token
     */
    public void doNimLogin(String account, String token) {


        LoginInfo info = new LoginInfo(account, token); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        LibSPUtils.put(Constants.NIM_LOGIN_INFO_ACCOUNT, param.getAccount());
                        LibSPUtils.put(Constants.NIM_LOGIN_INFO_TOKEN, param.getToken());
                        LibSPUtils.put(Constants.NIM_LOGIN_INFO_APPKEY, param.getAppKey());
                    }

                    @Override
                    public void onFailed(int code) {
                        LibToastUtils.toast("登录失败：code - " + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        exception.printStackTrace();
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);

    }

    /**
     * 网易云信登出
     */
    public void doNimLogout(){
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * 检查网易云信是否已登录
     * @return
     */
    public boolean isNimLogin(){
        StatusCode status = NIMClient.getStatus();
        if(status.getValue() == StatusCode.LOGINED.getValue()){
            return true;
        }

        return false;
    }
}