package com.xjh.xinwo.module.groupchat.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.xjh.xinwo.R;
import com.xjh.xinwo.adapter.ChatAdapter;
import com.xjh.xinwo.adapter.CommonFragmentPagerAdapter;
import com.xinwo.feed.base.BaseApplication;
import com.xjh.xinwo.enity.ChatGameEntity;
import com.xjh.xinwo.enity.FullImageInfo;
import com.xjh.xinwo.enity.FunctionEnvEntity;
import com.xjh.xinwo.enity.ImMessageInfo;
import com.xjh.xinwo.enity.KTVPeopleEntity;
import com.xjh.xinwo.enity.Link;
import com.xjh.xinwo.module.MainActivity;
import com.xjh.xinwo.module.chat.activity.FullImageActivity;
import com.xjh.xinwo.module.chat.fragment.ChatEmotionFragment;
import com.xjh.xinwo.module.chat.fragment.ChatFunctionFragment;
import com.xjh.xinwo.module.groupchat.adapter.ChatGameAdapter;
import com.xjh.xinwo.module.groupchat.adapter.FunctionEvnAdapter;
import com.xjh.xinwo.module.groupchat.adapter.KTVPeopleAdapter;
import com.xjh.xinwo.module.ktv.BaseAgoraFragment;
import com.xinwo.feed.base.AGEventHandler;
import com.xinwo.feed.base.ConstantApp;
import com.xjh.xinwo.util.Constants;
import com.xjh.xinwo.util.GlobalOnItemClickManagerUtils;
import com.xjh.xinwo.util.MediaManager;
import com.xjh.xinwo.widget.ChatContextMenu;
import com.xjh.xinwo.widget.EmotionInputDetector;
import com.xjh.xinwo.widget.IMGroupInputDetector;
import com.xjh.xinwo.widget.NoScrollViewPager;
import com.xjh.xinwo.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.agora.ktvkit.IKTVKitEventHandler;
import io.agora.ktvkit.KTVKit;
import io.agora.ktvkit.VideoPlayerView;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class GroupChatFragment extends BaseAgoraFragment implements View.OnClickListener, AGEventHandler {
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

    private ChatAdapter chatAdapter;
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
    private FrameLayout containerAgoraKTV;
    private ImageView ivKTVPic;

    private KTVKit mKTVKit;
    private VideoPlayerView xPlayerView;
    private boolean isBroadcast;
    private FrameLayout containerIMGroup;
    private IMGroupFragment imGroupFragment;
    private IMGroupInputDetector imGroupInputDetector;
    //    private ImageView ivChatAudioVideo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mKTVKit = KTVKit.create(worker().getRtcEngine(), BaseApplication.getInstance(), new IKTVKitEventHandler() {

                @Override
                public void onPlayerPrepared() {
                    super.onPlayerPrepared();
                    Log.e(TAG,"onCreate --> onPlayerPrepared");
                }

                @Override
                public void onPlayerStopped() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            int duration = (mKTVKit.getDuration() / 1000);
//
//                            mMediaMetaArea.setText("Done, " + (int) Math.floor(mKTVKit.getCurrentPosition() * duration) + " " + duration);
//                        }
//                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"onCreate --> " + e.toString());
        }

        Log.e(TAG,"onCreate --> mKTVKit = " + mKTVKit);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_group_chat, null, false);
        return root;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMoreFunction();
        findViewByIds();
        EventBus.getDefault().register(this);
        initWidget();
//        handleIncomeAction();
    }



    @Override
    public void onResume() {
        super.onResume();
        PermissionsUtil.requestPermission(getContext(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Toast.makeText(getContext(), "权限申请失败", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO});
    }



    private void initMoreFunction() {
        containerFunctionMore = getView().findViewById(R.id.containerFunctionMore);
        ivFunctionChat = getView().findViewById(R.id.ivFunctionChat);
        tvFunctionChat = getView().findViewById(R.id.tvFunctionChat);
        ivFunctionKTV = getView().findViewById(R.id.ivFunctionKTV);
        tvFunctionKTV = getView().findViewById(R.id.tvFunctionKTV);
        ivFunctionEnvironment = getView().findViewById(R.id.ivFunctionEnvironment);
        tvFunctionEnvironment = getView().findViewById(R.id.tvFunctionEnvironment);
        ivFunctionBg = getView().findViewById(R.id.ivFunctionBg);
        tvFunctionBg = getView().findViewById(R.id.tvFunctionBg);
        ivFunctionSlience = getView().findViewById(R.id.ivFunctionSlience);
        tvFunctionSlience = getView().findViewById(R.id.tvFunctionSlience);
        ivFunctionNoDisturb = getView().findViewById(R.id.ivFunctionNoDisturb);
        tvFunctionNoDisturb = getView().findViewById(R.id.tvFunctionNoDisturb);
        ivFunctionCollopse = getView().findViewById(R.id.ivFunctionCollopse);
        tvFunctionCollapse = getView().findViewById(R.id.tvFunctionCollapse);
        ivFunctionClose = getView().findViewById(R.id.ivFunctionClose);
        tvFunctionClose = getView().findViewById(R.id.tvFunctionClose);

        rvFunctionEnvironment = getView().findViewById(R.id.rvFunctionEnvironment);
        containerFunctionBg = getView().findViewById(R.id.containerFunctionBg);

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

        rvFunctionEnvironment.setLayoutManager(new GridLayoutManager(getContext(), 3));
        functionEvnAdapter = new FunctionEvnAdapter(R.layout.item_function_env, data);
        rvFunctionEnvironment.setAdapter(functionEvnAdapter);
    }


    private void findViewByIds() {
        //顶部
        ivChatBack = getView().findViewById(R.id.ivChatBack);
        ivMoreFunction = getView().findViewById(R.id.ivMoreFunction);
        tvGroupName = getView().findViewById(R.id.tvGroupName);

        ivChatBack.setOnClickListener(this);
        ivMoreFunction.setOnClickListener(this);

        //中间
        containerGroupOwner = getView().findViewById(R.id.containerGroupOwner);
        containerTableLeft = getView().findViewById(R.id.containerTableLeft);
        containerTableRight = getView().findViewById(R.id.containerTableRight);

        containerKTV = getView().findViewById(R.id.containerKTV);
        containerAgoraKTV = getView().findViewById(R.id.containerAgoraKTV);
        ivKTVPic = getView().findViewById(R.id.ivKTVPic);
        rvKTVPeople = getView().findViewById(R.id.rvKTVPeople);

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
        rvKTVPeople.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvKTVPeople.setAdapter(ktvPeopleAdapter);

        //中间聊天框
        //TODO 聊天框
        containerIMGroup = getView().findViewById(R.id.containerIMGroup);
        imGroupFragment = new IMGroupFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(R.id.containerIMGroup, imGroupFragment,null)
                .commit();




        //底部上面
        containerKTVMicroGetSing = getView().findViewById(R.id.containerKTVMicroGetSing);
        containerKTVChooseMusic = getView().findViewById(R.id.containerKTVChooseMusic);
        ivRedPackage = getView().findViewById(R.id.ivRedPackage);
        ivGift = getView().findViewById(R.id.ivGift);

        containerKTVMicroGetSing.setOnClickListener(this);
        containerKTVChooseMusic.setOnClickListener(this);

        //底部
        chatList = (RecyclerView) getView().findViewById(R.id.chat_list);
        emotionVoice = (ImageView) getView().findViewById(R.id.emotion_voice);
        editText = (EditText) getView().findViewById(R.id.edit_text);
        emotionButton = (ImageView) getView().findViewById(R.id.emotion_button);
        emotionAdd = (ImageView) getView().findViewById(R.id.emotion_add);
        emotionSend = (StateButton) getView().findViewById(R.id.emotion_send);
        emotionLayout = (RelativeLayout) getView().findViewById(R.id.emotion_layout);
        viewpager = (NoScrollViewPager) getView().findViewById(R.id.viewpager);

        ivChatCamera = getView().findViewById(R.id.ivChatCamera);
        ivChatPic = getView().findViewById(R.id.ivChatPic);
        ivChatGame = getView().findViewById(R.id.ivChatGame);
//        ivChatAudioVideo = getView().findViewById(R.id.ivChatAudioVideo);


        int[] gamePicResIDs = new int[]{
                R.mipmap.game_ma_jiang,
                R.mipmap.game_dou_di_zhu,
                R.mipmap.game_piao_yi,
        };
        List<ChatGameEntity> chatGameEntities = new ArrayList<>();
        for(int i = 0; i <= 2; ++i){
            chatGameEntities.add(new ChatGameEntity(gamePicResIDs[i], "http://demo.ligx.top/ht" + (i+2)));
        }

        rvGame = getView().findViewById(R.id.rvGame);
        Log.e(TAG,"findViewByIds --> rvGame = " + rvGame);
        chatGameAdapter = new ChatGameAdapter(chatGameEntities);

        rvGame.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvGame.setAdapter(chatGameAdapter);
    }


//    private void handleIncomeAction() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle == null) {
//            return;
//        }
//
//        MessageCenter.handleIncoming(bundle, getIntent().getType(), this);
//    }

    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        imGroupInputDetector = IMGroupInputDetector.with(getActivity())
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

        imGroupFragment.setDetector(imGroupInputDetector);

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(getContext());
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(messageInfos);
        layoutManager = new LinearLayoutManager(getContext());
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
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(getContext(), "onHeaderClick", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(getContext(), FullImageActivity.class));
//            overridePendingTransition(0, 0);
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
            Uri fileUri = FileProvider.getUriForFile(getContext(), Constants.AUTHORITY, file);
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

//    @Override
//    public void onBackPressed() {
//        if (!mDetector.interceptBackPress()) {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(getContext());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivChatBack:
                ((MainActivity)getActivity()).switchToLastTab();
                break;
            case R.id.ivMoreFunction:
                if(containerFunctionMore.getVisibility() == View.GONE){
                    containerFunctionMore.setVisibility(View.VISIBLE);
                }else{
                    containerFunctionMore.setVisibility(View.GONE);
                }
                break;

            // 底部上面
            case R.id.containerKTVMicroGetSing:
                break;
            case R.id.containerKTVChooseMusic:
                Log.e(TAG,"onClick --> containerKTVChooseMusic --> mKTVKit = " + mKTVKit);
                mKTVKit.openAndPlayVideoFile("http://download.agora.io/usecase/ktv01.mp4");
                ivKTVPic.setVisibility(View.INVISIBLE);
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

    protected void initUIandEvent() {
//        event().addEventHandler(this);
//        Intent i = getIntent();
//        int cRole = i.getIntExtra(ConstantApp.ACTION_KEY_CROLE, 0);
//        if (cRole == 0) {
//            throw new RuntimeException("Should not reach here");
//        }
//        String roomName = i.getStringExtra(ConstantApp.ACTION_KEY_ROOM_NAME);

        int cRole = 1;
        String roomName = "心窝";

        doConfigEngine(cRole);

        if (isBroadcaster(cRole)) {//广播者
            addXplayView();
            isBroadcast = false;
        } else {//听众
            isBroadcast = true;
        }

        worker().getRtcEngine().setParameters(String.format(Locale.US, "{\"che.audio.profile\":{\"scenario\":%d}}", 1));
        worker().getRtcEngine().setParameters(String.format(Locale.US, "{\"che.audio.headset.monitoring,true\"}"));
        worker().getRtcEngine().setParameters(String.format(Locale.US, "{\"che.audio.enable.androidlowlatencymode,true\"}"));
        worker().getRtcEngine().enableInEarMonitoring(true);
        worker().joinChannel(roomName, config().mUid);

    }

    @Override
    protected void deInitUIandEvent() {
        event().removeEventHandler(this);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        doRenderRemoteUi(uid);
    }

    private void doRenderRemoteUi(final int uid) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDetached()) {
                    return;
                }
                SurfaceView surfaceView = RtcEngine.CreateRendererView(BaseApplication.getInstance());
                containerAgoraKTV.addView(surfaceView);
                surfaceView.setZOrderOnTop(true);
                surfaceView.setZOrderMediaOverlay(true);
                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
            }
        });
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        int index = -1;
        int count = containerAgoraKTV.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = containerAgoraKTV.getChildAt(i);
            if (!(v instanceof VideoPlayerView)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            containerAgoraKTV.removeViewAt(index);
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
            Log.d("heheda", "onUserJoined --> heelo");
    }

    private boolean isBroadcaster(int cRole) {
        return cRole == io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;
    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        if (prefIndex > ConstantApp.VIDEO_PROFILES.length - 1) {
            prefIndex = ConstantApp.DEFAULT_PROFILE_IDX;
        }
        int vProfile = ConstantApp.VIDEO_PROFILES[prefIndex];

        worker().configEngine(cRole, vProfile);
    }

    // 添加显示 view
    private void addXplayView() {
        xPlayerView = new VideoPlayerView(getContext(), mKTVKit);
        xPlayerView.setZOrderOnTop(true);
        xPlayerView.setZOrderMediaOverlay(true);
        containerAgoraKTV.addView(xPlayerView);
    }
}
