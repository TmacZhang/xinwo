package com.cavalry.androidlib.mvp.subscriber.handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.cavalry.androidlib.mvp.subscriber.listener.OnProgressCancelListener;


/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private final Context mContext;
    private final OnProgressCancelListener mOnProgressCancelListener;
    private boolean mCancelable;
    private String mMsg;
    private ProgressDialog mProgressDialog;


    public ProgressDialogHandler(Context context, OnProgressCancelListener onProgressCancelListener, boolean cancelable, String msg){
        mContext = context;
        mOnProgressCancelListener = onProgressCancelListener;
        mCancelable = cancelable;
        mMsg = msg;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case SHOW_PROGRESS_DIALOG:
                showProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

    private void showProgressDialog(){
        if(mProgressDialog==null){
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setCancelable(mCancelable);
            if(TextUtils.isEmpty(mMsg)){
                mProgressDialog.setMessage("努力加载中...");
            }else{
                mProgressDialog.setMessage(mMsg);
            }
            if(mCancelable){
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mOnProgressCancelListener.onProgressCancel();
                    }
                });
            }
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void setMessage(String msg){
        if(TextUtils.isEmpty(msg)){
            return;
        }
        if(mProgressDialog != null){
            mProgressDialog.setMessage(mMsg);
        }
    }

    public void setCancelable(boolean isCancelable){
        this.mCancelable = isCancelable;
    }

    public boolean isShown() {
        if (mProgressDialog != null) {
            return mProgressDialog.isShowing();
        }
        return false;
    }

}
