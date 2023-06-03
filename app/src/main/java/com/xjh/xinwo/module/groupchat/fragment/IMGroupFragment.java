package com.xjh.xinwo.module.groupchat.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.xjh.xinwo.R;
import com.xinwo.feed.base.BaseFragment;
import com.xjh.xinwo.enity.FullImageInfo;
import com.xjh.xinwo.enity.ImGroupMessageInfo;
import com.xjh.xinwo.enity.Link;
import com.xjh.xinwo.module.chat.activity.FullImageActivity;
import com.xjh.xinwo.module.groupchat.adapter.IMGroupAdapter;
import com.xjh.xinwo.util.Constants;
import com.xjh.xinwo.util.MediaManager;
import com.xjh.xinwo.widget.GroupChatContextMenu;
import com.xjh.xinwo.widget.IMGroupInputDetector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class IMGroupFragment extends BaseFragment {

    private RecyclerView rvChatGroup;
    private IMGroupAdapter imGroupAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<ImGroupMessageInfo> groupMessageInfos;
    private IMGroupInputDetector mDetector;
    private ImageView animView;
    //录音相关
    int animationRes = 0;
    int res = 0;
    private AnimationDrawable animationDrawable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_im_group, null);
        return root;
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
        initChatData();
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        rvChatGroup = getView().findViewById(R.id.rvChatGroup);

        imGroupAdapter = new IMGroupAdapter(groupMessageInfos);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvChatGroup.setLayoutManager(layoutManager);
        rvChatGroup.setAdapter(imGroupAdapter);
        rvChatGroup.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        imGroupAdapter.handler.removeCallbacksAndMessages(null);
                        imGroupAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        imGroupAdapter.handler.removeCallbacksAndMessages(null);
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
        imGroupAdapter.addItemClickListener(itemClickListener);
    }

    @Override
    public void loadData() {

    }


    /**
     * item点击事件
     */
    private IMGroupAdapter.onItemClickListener itemClickListener = new IMGroupAdapter.onItemClickListener() {
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
            fullImageInfo.setImageUrl(groupMessageInfos.get(position).getFilepath());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(getContext(), FullImageActivity.class));
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (groupMessageInfos.get(position).getType()) {
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
            MediaManager.playSound(groupMessageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });
        }

        @Override
        public void onFileClick(View view, int position) {

            ImGroupMessageInfo messageInfo = groupMessageInfos.get(position);
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
            ImGroupMessageInfo messageInfo = groupMessageInfos.get(position);
            Link link = (Link) messageInfo.getObject();
            Uri uri = Uri.parse(link.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        @Override
        public void onLongClickImage(View view, int position) {

            GroupChatContextMenu chatContextMenu = new GroupChatContextMenu(view.getContext(), groupMessageInfos.get(position));
//            chatContextMenu.setAnimationStyle();
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);

        }

        @Override
        public void onLongClickText(View view, int position) {
            GroupChatContextMenu chatContextMenu = new GroupChatContextMenu(view.getContext(), groupMessageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }

        @Override
        public void onLongClickItem(View view, int position) {
            GroupChatContextMenu chatContextMenu = new GroupChatContextMenu(view.getContext(), groupMessageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }

        @Override
        public void onLongClickFile(View view, int position) {
            GroupChatContextMenu chatContextMenu = new GroupChatContextMenu(view.getContext(), groupMessageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }

        @Override
        public void onLongClickLink(View view, int position) {
            GroupChatContextMenu chatContextMenu = new GroupChatContextMenu(view.getContext(), groupMessageInfos.get(position));
            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
                    RelativePopupWindow.HorizontalPosition.CENTER);
        }
    };


    /**
     * 构造聊天数据
     */
    private void initChatData() {
        groupMessageInfos = new ArrayList<>();

        ImGroupMessageInfo messageInfo = new ImGroupMessageInfo();
        messageInfo.setContent("去听周杰伦的演唱会不？");
        messageInfo.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
        groupMessageInfos.add(messageInfo);

        ImGroupMessageInfo messageInfo1 = new ImGroupMessageInfo();
        messageInfo1.setContent("哇！什么时候的演唱会？在哪里举办？买票");
        messageInfo1.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
        messageInfo1.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo1.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        groupMessageInfos.add(messageInfo1);

        ImGroupMessageInfo messageInfo2 = new ImGroupMessageInfo();
        messageInfo2.setFilepath("http://img4.imgtn.bdimg.com/it/u=1800788429,176707229&fm=21&gp=0.jpg");
        messageInfo2.setFileType(Constants.CHAT_FILE_TYPE_IMAGE);
        messageInfo2.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo2.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
        groupMessageInfos.add(messageInfo2);
    }

    public void setDetector(IMGroupInputDetector imGroupInputDetector) {
        mDetector = imGroupInputDetector;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final ImGroupMessageInfo messageInfo) {
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        groupMessageInfos.add(messageInfo);
        imGroupAdapter.notifyItemInserted(groupMessageInfos.size() - 1);
//        chatAdapter.add(messageInfo);
        rvChatGroup.scrollToPosition(imGroupAdapter.getItemCount() - 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                imGroupAdapter.notifyDataSetChanged();
            }
        }, 2000);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ImGroupMessageInfo message = new ImGroupMessageInfo();
                message.setContent("这是模拟消息回复");
                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                message.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
                message.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
                groupMessageInfos.add(message);
                imGroupAdapter.notifyItemInserted(groupMessageInfos.size() - 1);
                rvChatGroup.scrollToPosition(imGroupAdapter.getItemCount() - 1);
            }
        }, 3000);
    }
}
