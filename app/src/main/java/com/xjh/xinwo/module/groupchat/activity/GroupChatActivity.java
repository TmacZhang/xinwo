package com.xjh.xinwo.module.groupchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.xinwo.social.chat.ChatContextMenu;
import com.xinwo.social.chat.EmotionInputDetector;
import com.xinwo.social.chat.MessageCenter;
import com.xinwo.social.chat.entity.ImMessageInfo;
import com.xinwo.social.chat.entity.Link;
import com.xinwo.xinutil.Constants;
import com.xinwo.xinutil.GlobalOnItemClickManagerUtils;
import com.xinwo.xinutil.MediaManager;
import com.xinwo.xinview.NoScrollViewPager;
import com.xinwo.xinview.StateButton;
import com.xjh.xinwo.R;
import com.xinwo.social.chat.adapter.ChatAdapterV2;
import com.xinwo.social.chat.adapter.CommonFragmentPagerAdapter;
import com.xinwo.social.chat.entity.ChatGameEntity;
import com.xjh.xinwo.enity.FullImageInfo;
import com.xjh.xinwo.enity.FunctionEnvEntity;
import com.xjh.xinwo.enity.KTVPeopleEntity;
import com.xinwo.social.chat.activity.FullImageActivity;
import com.xjh.xinwo.module.groupchat.adapter.ChatGameAdapter;
import com.xjh.xinwo.module.groupchat.adapter.FunctionEvnAdapter;
import com.xjh.xinwo.module.groupchat.adapter.KTVPeopleAdapter;
import com.xinwo.social.chat.fragment.ChatEmotionFragment;
import com.xinwo.social.chat.fragment.ChatFunctionFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IMActivity";
    RecyclerView chatList;
    ImageView emotionVoice;
    EditText editText;
    ImageView emotionButton;
    ImageView emotionAdd;
    StateButton emotionSend;
    NoScrollViewPager viewpager;
    RelativeLayout emotionLayout;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapterV2 chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<ImMessageInfo> messageInfos;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;
    private ImageView ivChatCamera;
    private ImageView ivChatPic;
    private ImageView ivChatGame;
    private ImageView ivMoreFunction;
    private ViewGroup containerFunctionMore;
    private ImageView ivFunctionChat;
    private TextView tvFunctionChat;
    private ImageView ivFunctionKTV;
    private TextView tvFunctionKTV;
    private ImageView ivFunctionEnvironment;
    private TextView tvFunctionEnvironment;
    private ImageView ivFunctionBg;
    private TextView tvFunctionBg;
    private ImageView ivFunctionSlience;
    private TextView tvFunctionSlience;
    private ImageView ivFunctionNoDisturb;
    private TextView tvFunctionNoDisturb;
    private ImageView ivFunctionCollopse;
    private TextView tvFunctionCollapse;
    private ImageView ivFunctionClose;
    private TextView tvFunctionClose;
    private RecyclerView rvFunctionEnvironment;
    private ViewGroup containerFunctionBg;
    private ImageView ivChatBack;
    private TextView tvGroupName;
    private ViewGroup containerGroupOwner;
    private ViewGroup containerTableLeft;
    private ViewGroup containerTableRight;
    private ViewGroup containerKTV;
    private RecyclerView rvKTVPeople;
    private View containerKTVMicroGetSing;
    private View containerKTVChooseMusic;
    private View ivRedPackage;
    private View ivGift;
    private FunctionEvnAdapter functionEvnAdapter;
    private RecyclerView rvGame;
    private ChatGameAdapter chatGameAdapter;
    //    private ImageView ivChatAudioVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        initMoreFunction();
        findViewByIds();
        EventBus.getDefault().register(this);
        initWidget();
        handleIncomeAction();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

    }



    @Override
    protected void onResume() {
        super.onResume();
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Toast.makeText(GroupChatActivity.this, "权限申请失败", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO});
    }

    private void initMoreFunction() {
        containerFunctionMore = findViewById(R.id.containerFunctionMore);
        ivFunctionChat = findViewById(R.id.ivFunctionChat);
        tvFunctionChat = findViewById(R.id.tvFunctionChat);
        ivFunctionKTV = findViewById(R.id.ivFunctionKTV);
        tvFunctionKTV = findViewById(R.id.tvFunctionKTV);
        ivFunctionEnvironment = findViewById(R.id.ivFunctionEnvironment);
        tvFunctionEnvironment = findViewById(R.id.tvFunctionEnvironment);
        ivFunctionBg = findViewById(R.id.ivFunctionBg);
        tvFunctionBg = findViewById(R.id.tvFunctionBg);
        ivFunctionSlience = findViewById(R.id.ivFunctionSlience);
        tvFunctionSlience = findViewById(R.id.tvFunctionSlience);
        ivFunctionNoDisturb = findViewById(R.id.ivFunctionNoDisturb);
        tvFunctionNoDisturb = findViewById(R.id.tvFunctionNoDisturb);
        ivFunctionCollopse = findViewById(R.id.ivFunctionCollopse);
        tvFunctionCollapse = findViewById(R.id.tvFunctionCollapse);
        ivFunctionClose = findViewById(R.id.ivFunctionClose);
        tvFunctionClose = findViewById(R.id.tvFunctionClose);

        rvFunctionEnvironment = findViewById(R.id.rvFunctionEnvironment);
        containerFunctionBg = findViewById(R.id.containerFunctionBg);

        ivFunctionChat.setOnClickListener(this);
        tvFunctionChat.setOnClickListener(this);
        ivFunctionKTV.setOnClickListener(this);
        tvFunctionKTV.setOnClickListener(this);
        ivFunctionEnvironment.setOnClickListener(this);
        tvFunctionEnvironment.setOnClickListener(this);
        ivFunctionBg.setOnClickListener(this);
        tvFunctionBg.setOnClickListener(this);
        ivFunctionSlience.setOnClickListener(this);
        tvFunctionSlience.setOnClickListener(this);
        ivFunctionNoDisturb.setOnClickListener(this);
        tvFunctionNoDisturb.setOnClickListener(this);
        ivFunctionCollopse.setOnClickListener(this);
        tvFunctionCollapse.setOnClickListener(this);
        ivFunctionClose.setOnClickListener(this);
        tvFunctionClose.setOnClickListener(this);

        List<FunctionEnvEntity> data = new ArrayList<>();
        int[] resID = new int[]{
                R.mipmap.more_function_env_pic_01,
                R.mipmap.more_function_env_pic_02,
                R.mipmap.more_function_env_pic_03,
                R.mipmap.more_function_env_pic_04,
                R.mipmap.more_function_env_pic_05,
                R.mipmap.more_function_env_pic_06,
                R.mipmap.more_function_env_pic_07,
                R.mipmap.more_function_env_pic_08,
                R.mipmap.more_function_env_pic_09
        };

        String[] names = new String[]{
                "雨一直下",
                "檐下风铃",
                "围炉夜话",
                "潮汐涨落",
                "夏夜闲谈",
                "林间小屋",
                "人在囧途",
                "深海潜行",
                "街角咖啡",
        };

        for(int i = 0; i < 9; ++i){
            data.add(new FunctionEnvEntity(resID[i], names[i]));
        }

        rvFunctionEnvironment.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
        functionEvnAdapter = new FunctionEvnAdapter(R.layout.item_function_env, data);
        rvFunctionEnvironment.setAdapter(functionEvnAdapter);
    }


    private void findViewByIds() {
        //顶部
        ivChatBack = findViewById(R.id.ivChatBack);
        ivMoreFunction = findViewById(R.id.ivMoreFunction);
        tvGroupName = findViewById(R.id.tvGroupName);

        ivChatBack.setOnClickListener(this);
        ivMoreFunction.setOnClickListener(this);

        //中间
        containerGroupOwner = findViewById(R.id.containerGroupOwner);
        containerTableLeft = findViewById(R.id.containerTableLeft);
        containerTableRight = findViewById(R.id.containerTableRight);

        containerKTV = findViewById(R.id.containerKTV);
        rvKTVPeople = findViewById(R.id.rvKTVPeople);

        int[] ktvPeopleResID = new int[]{
                R.mipmap.function_ktv_header_01,
                R.mipmap.function_ktv_header_02,
                R.mipmap.function_ktv_header_03,
                R.mipmap.function_ktv_header_04,
                R.mipmap.function_ktv_header_05,
                R.mipmap.function_ktv_header_06,
                R.mipmap.function_ktv_header_07,
                R.mipmap.function_ktv_header_08,
                R.mipmap.function_ktv_header_09,
                R.mipmap.function_ktv_header_10,
        };

        List<KTVPeopleEntity> ktvPeopleEntityList = new ArrayList<>();
        for(int i = 0; i < 10; ++i){
            ktvPeopleEntityList.add(new KTVPeopleEntity(ktvPeopleResID[i], R.mipmap.function_ktv_header_micro_phone));
        }

        KTVPeopleAdapter ktvPeopleAdapter = new KTVPeopleAdapter(ktvPeopleEntityList);
        rvKTVPeople.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
        rvKTVPeople.setAdapter(ktvPeopleAdapter);

        //底部上面
        containerKTVMicroGetSing = findViewById(R.id.containerKTVMicroGetSing);
        containerKTVChooseMusic = findViewById(R.id.containerKTVChooseMusic);
        ivRedPackage = findViewById(R.id.ivRedPackage);
        ivGift = findViewById(R.id.ivGift);

        //底部
        chatList = (RecyclerView) findViewById(R.id.chat_list);
        emotionVoice = (ImageView) findViewById(R.id.emotion_voice);
        editText = (EditText) findViewById(R.id.edit_text);
        emotionButton = (ImageView) findViewById(R.id.emotion_button);
        emotionAdd = (ImageView) findViewById(R.id.emotion_add);
        emotionSend = (StateButton) findViewById(R.id.emotion_send);
        emotionLayout = (RelativeLayout) findViewById(R.id.emotion_layout);
        viewpager = (NoScrollViewPager) findViewById(R.id.viewpager);

        ivChatCamera = findViewById(R.id.ivChatCamera);
        ivChatPic = findViewById(R.id.ivChatPic);
        ivChatGame = findViewById(R.id.ivChatGame);
//        ivChatAudioVideo = findViewById(R.id.ivChatAudioVideo);


        int[] gamePicResIDs = new int[]{
                R.mipmap.game_ma_jiang,
                R.mipmap.game_dou_di_zhu,
                R.mipmap.game_piao_yi,
        };
        List<ChatGameEntity> chatGameEntities = new ArrayList<>();
        for(int i = 0; i <= 2; ++i){
            chatGameEntities.add(new ChatGameEntity(gamePicResIDs[i], "http://demo.ligx.top/ht" + (i+2)));
        }

        rvGame = findViewById(R.id.rvGame);
        Log.e(TAG,"findViewByIds --> rvGame = " + rvGame);
        chatGameAdapter = new ChatGameAdapter(chatGameEntities);

        rvGame.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
        rvGame.setAdapter(chatGameAdapter);
    }


    private void handleIncomeAction() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        MessageCenter.handleIncoming(bundle, getIntent().getType(), this);
    }

    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToCameraButton(ivChatCamera)
                .bindToPicButton(ivChatPic)
                .bindToGameButton(ivChatGame)
                .bindToGameLayout(rvGame)
//                .bindToAudioVideoButton(ivChatAudioVideo)
                .build();

        chatAdapter = new ChatAdapterV2(messageInfos);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        LoadData();
    }

    /**
     * item点击事件
     */
    private ChatAdapterV2.onItemClickListener itemClickListener = new ChatAdapterV2.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(GroupChatActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(view.getWidth());
            fullImageInfo.setHeight(view.getHeight());
            fullImageInfo.setImageUrl(messageInfos.get(position).getFilepath());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(GroupChatActivity.this, FullImageActivity.class));
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (messageInfos.get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });
        }

        @Override
        public void onFileClick(View view, int position) {

            ImMessageInfo messageInfo = messageInfos.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(messageInfo.getFilepath());
            Uri fileUri = FileProvider.getUriForFile(GroupChatActivity.this, Constants.AUTHORITY, file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(fileUri, messageInfo.getMimeType());
            startActivity(intent);
        }

        @Override
        public void onLinkClick(View view, int position) {
            ImMessageInfo messageInfo = messageInfos.get(position);
            Link link = (Link) messageInfo.getObject();
            Uri uri = Uri.parse(link.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        @Override
        public void onLongClickImage(View view, int position) {

            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageInfos.get(position));
//            chatContextMenu.setAnimationStyle();
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);

        }

        @Override
        public void onLongClickText(View view, int position) {
            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }

        @Override
        public void onLongClickItem(View view, int position) {
            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }

        @Override
        public void onLongClickFile(View view, int position) {
            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }

        @Override
        public void onLongClickLink(View view, int position) {
            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }
    };

    /**
     * 构造聊天数据
     */
    private void LoadData() {
        messageInfos = new ArrayList<>();

        ImMessageInfo messageInfo = new ImMessageInfo();
        messageInfo.setContent("一起去看电影去？");
        messageInfo.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
        messageInfos.add(messageInfo);

        ImMessageInfo messageInfo1 = new ImMessageInfo();
        messageInfo1.setFilepath("http://www.trueme.net/bb_midi/welcome.wav");
        messageInfo1.setVoiceTime(3000);
        messageInfo1.setFileType(Constants.CHAT_FILE_TYPE_VOICE);
        messageInfo1.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo1.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
        messageInfo1.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfos.add(messageInfo1);

        ImMessageInfo messageInfo2 = new ImMessageInfo();
        messageInfo2.setFilepath("http://img4.imgtn.bdimg.com/it/u=1800788429,176707229&fm=21&gp=0.jpg");
        messageInfo2.setFileType(Constants.CHAT_FILE_TYPE_IMAGE);
        messageInfo2.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo2.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
        messageInfos.add(messageInfo2);

        ImMessageInfo messageInfo3 = new ImMessageInfo();
        messageInfo3.setContent("[微笑][色][色][色]");
        messageInfo3.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
        messageInfo3.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo3.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
        messageInfo3.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfos.add(messageInfo3);

        chatAdapter.addAll(messageInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final ImMessageInfo messageInfo) {
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfo.setFilepath("http://www.trueme.net/bb_midi/welcome.wav");
        messageInfo.setVoiceTime(3000);
        messageInfo.setFileType(Constants.CHAT_FILE_TYPE_VOICE);
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        messageInfos.add(messageInfo);
        chatAdapter.notifyItemInserted(messageInfos.size() - 1);
//        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getItemCount() - 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();
            }
        }, 2000);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ImMessageInfo message = new ImMessageInfo();
                message.setContent("这是模拟消息回复");
                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                message.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
                message.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
                messageInfos.add(message);
                chatAdapter.notifyItemInserted(messageInfos.size() - 1);
                chatList.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivChatBack:
                finish();
                break;
            case R.id.ivMoreFunction:
                if(containerFunctionMore.getVisibility() == View.GONE){
                    containerFunctionMore.setVisibility(View.VISIBLE);
                }else{
                    containerFunctionMore.setVisibility(View.GONE);
                }
                break;
            //MoreFunction
            case R.id.ivFunctionChat:
            case R.id.tvFunctionChat:
                rvFunctionEnvironment.setVisibility(View.GONE);
                containerFunctionBg.setVisibility(View.GONE);

                containerKTV.setVisibility(View.GONE);
                containerGroupOwner.setVisibility(View.VISIBLE);
                containerTableRight.setVisibility(View.VISIBLE);
                containerTableLeft.setVisibility(View.VISIBLE);
                containerKTVMicroGetSing.setVisibility(View.INVISIBLE);
                containerKTVChooseMusic.setVisibility(View.INVISIBLE);

                containerFunctionMore.setVisibility(View.GONE);
                break;
            case R.id.ivFunctionKTV:
            case R.id.tvFunctionKTV:
                rvFunctionEnvironment.setVisibility(View.GONE);
                containerFunctionBg.setVisibility(View.GONE);

                containerGroupOwner.setVisibility(View.GONE);
                containerTableRight.setVisibility(View.GONE);
                containerTableLeft.setVisibility(View.GONE);
                containerKTV.setVisibility(View.VISIBLE);
                containerKTVMicroGetSing.setVisibility(View.VISIBLE);
                containerKTVChooseMusic.setVisibility(View.VISIBLE);

                containerFunctionMore.setVisibility(View.GONE);
                break;
            case R.id.ivFunctionEnvironment:
            case R.id.tvFunctionEnvironment:
                rvFunctionEnvironment.setVisibility(View.VISIBLE);
                containerFunctionBg.setVisibility(View.GONE);
                break;
            case R.id.ivFunctionBg:
            case R.id.tvFunctionBg:
                rvFunctionEnvironment.setVisibility(View.GONE);
                containerFunctionBg.setVisibility(View.VISIBLE);
                break;
            case R.id.ivFunctionSlience:
            case R.id.tvFunctionSlience:
                break;
            case R.id.ivFunctionNoDisturb:
            case R.id.tvFunctionNoDisturb:
                break;
            case R.id.ivFunctionCollopse:
            case R.id.tvFunctionCollapse:
                break;
            case R.id.ivFunctionClose:
            case R.id.tvFunctionClose:
                break;
        }
    }
}
