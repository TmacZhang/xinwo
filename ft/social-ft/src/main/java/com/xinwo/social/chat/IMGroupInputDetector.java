package com.xinwo.social.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.xinwo.social.chat.dialog.AudioVideoDialog;
import com.xinwo.social.chat.entity.ImGroupMessageInfo;
import com.xinwo.xinutil.AudioRecorderUtils;
import com.xinwo.xinutil.Constants;
import com.xinwo.xinutil.NimUtils;
import com.xinwo.xinutil.PopupWindowFactory;
import com.xinwo.xinutil.Utils;


import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 输入框管理类
 */
public class IMGroupInputDetector {
    private static final String TAG = "EmotionInputDetector";
    private static final String SHARE_PREFERENCE_NAME = "com.dss886.emotioninputdetector";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";

    private static final int CODE_TAKE_PHOTO = 0x111;
    private static final int CODE_CROP_PHOTO = 0xa2;
    public static final int REQUEST_CODE_PICK_IMAGE = 0xa3;
    private static final int REQUEST_CODE_PICK_FILE = 0xa4;
    private static final int CODE_REQUEST_CAMERA = 0xa5;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0xa6;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE_CODE = 0xa7;
    private static final int MY_PERMISSIONS_REQUEST_CAMERACODE = 0xa8;

    private Activity mActivity;
    private InputMethodManager mInputManager;
    private SharedPreferences sp;
    private View mEmotionLayout;
    private EditText mEditText;
    private View mContentView;
    private ViewPager mViewPager;
    private View mSendButton;
    private View mAddButton;
    private Boolean isShowEmotion = false;
    private Boolean isShowAdd = false;
    private boolean isShowVoice = false;
    private AudioRecorderUtils mAudioRecorderUtils;
    private PopupWindowFactory mVoicePop;
    private TextView mPopVoiceText;
    private ImageView mIvChatCamera;
    private ImageView mIvChatPic;
    private ImageView mIvChatGame;
    private ImageView mIvChatAudioVideo;
    private ImageView mVoiceButton;
    private RecyclerView mRvGame;

    private IMGroupInputDetector() {
    }

    public static IMGroupInputDetector with(Activity activity) {
        IMGroupInputDetector emotionInputDetector = new IMGroupInputDetector();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return emotionInputDetector;
    }

    public IMGroupInputDetector bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    public IMGroupInputDetector bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    lockContentHeight();
                    hideEmotionLayout(true);

                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    ImGroupMessageInfo messageInfo = new ImGroupMessageInfo();
                    messageInfo.setContent(mEditText.getText().toString());
                    messageInfo.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
                    EventBus.getDefault().post(messageInfo);
                    mEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 0) {
//                    mAddButton.setVisibility(View.GONE);
//                    mSendButton.setVisibility(View.VISIBLE);
//                } else {
//                    mAddButton.setVisibility(View.VISIBLE);
//                    mSendButton.setVisibility(View.GONE);
//                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return this;
    }

    public IMGroupInputDetector bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (isShowAdd) {
                        mViewPager.setCurrentItem(0);
                        isShowEmotion = true;
                        isShowAdd = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowEmotion = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(0);
                    isShowEmotion = true;
                }
            }
        });
        return this;
    }

    public IMGroupInputDetector bindToAddButton(View addButton) {
        mAddButton = addButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (isShowEmotion) {
                        mViewPager.setCurrentItem(1);
                        isShowAdd = true;
                        isShowEmotion = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowAdd = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(1);
                    isShowAdd = true;
                }
            }
        });
        return this;
    }

    public IMGroupInputDetector bindToSendButton(View sendButton) {
        mSendButton = sendButton;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
                String content = mEditText.getText().toString();

                IMMessage imMessage = NimUtils.createTextMessage("sessionId", SessionTypeEnum.P2P, content);
                NimUtils.sendMessage(imMessage);

                ImGroupMessageInfo messageInfo = new ImGroupMessageInfo();
                messageInfo.setContent(content);
                messageInfo.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
                EventBus.getDefault().post(messageInfo);
                mEditText.setText("");
            }
        });
        return this;
    }

    public IMGroupInputDetector bindToVoiceButton(final ImageView voiceButton) {

        mVoiceButton = voiceButton;
        voiceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获得x轴坐标
                int x = (int) event.getX();
                // 获得y轴坐标
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mVoicePop.showAtLocation(v, Gravity.CENTER, 0, 0);
//                        mVoiceText.setText("松开结束");
                        mPopVoiceText.setText("手指上滑，取消发送");
                        mVoiceButton.setTag("1");
                        mAudioRecorderUtils.startRecord(mActivity);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (wantToCancel(x, y)) {
//                            mVoiceText.setText("松开结束");
                            mPopVoiceText.setText("松开手指，取消发送");
                            mVoiceButton.setTag("2");
                        } else {
//                            mVoiceText.setText("松开结束");
                            mPopVoiceText.setText("手指上滑，取消发送");
                            mVoiceButton.setTag("1");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mVoicePop.dismiss();

                        mAudioRecorderUtils.setOnAudioStatusUpdateListener(new AudioRecorderUtils.OnAudioStatusUpdateListener() {
                            @Override
                            public void onUpdate(double db, long time) {

                            }

                            @Override
                            public void onStop(long duration, String filePath) {
                                IMMessage imMessage = NimUtils.createAudioMessage("sessionId", SessionTypeEnum.P2P, new File(filePath), duration);
                                NimUtils.sendMessage(imMessage);
                            }

                            @Override
                            public void onError() {

                            }
                        });

                        if (mVoiceButton.getTag().equals("2")) {
                            //取消录音（删除录音文件）
                            mAudioRecorderUtils.cancelRecord();
                        } else {
                            //结束录音（保存录音文件）
                            mAudioRecorderUtils.stopRecord();
                        }
//                        mVoiceText.setText("按住说话");
                        mVoiceButton.setTag("3");
//                        mVoiceText.setVisibility(View.GONE);
                        mEditText.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        return this;

//        voiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isShowVoice) {
//                    voiceButton.setImageResource(R.mipmap.icon_voice);
//                } else {
//                    voiceButton.setImageResource(R.mipmap.icon_keyboard);
//
//                }
//
//                isShowVoice = !isShowVoice;
//
//                hideEmotionLayout(false);
//                hideSoftInput();
//            }
//        });
//        return this;
    }



    public IMGroupInputDetector bindToCameraButton(ImageView ivChatCamera) {
        mIvChatCamera = ivChatCamera;
        return this;
    }

    public IMGroupInputDetector bindToPicButton(ImageView ivChatPic) {
        mIvChatPic = ivChatPic;

        mIvChatPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE_CODE);

                } else {
                    choosePhoto();
                }
            }
        });

        return this;
    }

    public IMGroupInputDetector bindToGameButton(ImageView ivChatGame) {
        mIvChatGame = ivChatGame;
        mIvChatGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRvGame.getVisibility() == View.GONE){
                    mEmotionLayout.setVisibility(View.GONE);
                    mRvGame.setVisibility(View.VISIBLE);
                }else{
                    mEmotionLayout.setVisibility(View.GONE);
                    mRvGame.setVisibility(View.GONE);
                }
            }
        });
        return this;
    }

    public IMGroupInputDetector bindToAudioVideoButton(ImageView ivChatAudioVideo){
        mIvChatAudioVideo = ivChatAudioVideo;

        mIvChatAudioVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioVideoDialog dialog = new AudioVideoDialog(mActivity, com.xinwo.xinview.R.style.AudioVideoDialog);
                dialog.show();
            }
        });

        return this;
    }

    private boolean wantToCancel(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > mVoiceButton.getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -50 || y > mVoiceButton.getHeight() + 50) {
            return true;
        }
        return false;
    }



    private void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        mActivity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }


    public IMGroupInputDetector setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    public IMGroupInputDetector setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        return this;
    }

    public IMGroupInputDetector build() {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        mAudioRecorderUtils = new AudioRecorderUtils();

        View view = View.inflate(mActivity, com.xinwo.xinview.R.layout.layout_microphone, null);
        mVoicePop = new PopupWindowFactory(mActivity, view);

        //PopupWindow布局文件里面的控件
        final ImageView mImageView = (ImageView) view.findViewById(com.xinwo.xinview.R.id.iv_recording_icon);
        final TextView mTextView = (TextView) view.findViewById(com.xinwo.xinview.R.id.tv_recording_time);
        mPopVoiceText = (TextView) view.findViewById(com.xinwo.xinview.R.id.tv_recording_text);
        //录音回调
        mAudioRecorderUtils.setOnAudioStatusUpdateListener(new AudioRecorderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(Utils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(long duration, String filePath) {
                mTextView.setText(Utils.long2String(0));
                ImGroupMessageInfo messageInfo = new ImGroupMessageInfo();
                messageInfo.setFileType(Constants.CHAT_FILE_TYPE_VOICE);
                messageInfo.setFilepath(filePath);
                messageInfo.setVoiceTime(duration);
                EventBus.getDefault().post(messageInfo);
            }

            @Override
            public void onError() {
                mEditText.setVisibility(View.VISIBLE);
            }
        });
        return this;
    }

    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, 768);
        }
        hideSoftInput();
        Log.e(TAG, "showEmotionLayout: ->" + softInputHeight );
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
        mRvGame.setVisibility(View.GONE);
    }

    public void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void lockContentHeight() {
        Log.e(TAG, "lockContentHeight: ->" + mContentView.getHeight());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
            Log.e(TAG, "getSupportSoftInputHeight: ->" + softInputHeight );
            sp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    public IMGroupInputDetector bindToGameLayout(RecyclerView rvGame) {
        this.mRvGame = rvGame;
        Log.e(TAG,"bindToGameLayout --> rvGame = " + rvGame);

        return this;
    }
}
