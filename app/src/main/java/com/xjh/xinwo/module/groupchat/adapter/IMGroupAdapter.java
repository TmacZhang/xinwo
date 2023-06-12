package com.xjh.xinwo.module.groupchat.adapter;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.shehuan.niv.NiceImageView;
import com.xinwo.xinutil.Constants;
import com.xjh.xinwo.R;
import com.xinwo.social.chat.entity.ImGroupMessageInfo;
import com.xjh.xinwo.module.groupchat.adapter.holder.BaseViewHolder;
import com.xjh.xinwo.module.groupchat.adapter.holder.IMGroupAcceptViewHolder;
import com.xjh.xinwo.module.groupchat.adapter.holder.IMGroupSendViewHolder;

import java.util.ArrayList;
import java.util.List;

public class IMGroupAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private onItemClickListener onItemClickListener;
    public Handler handler;
    private List<ImGroupMessageInfo> messageInfoList;

    public IMGroupAdapter(List<ImGroupMessageInfo> messageInfoList) {
        handler = new Handler();
        this.messageInfoList = messageInfoList;
    }

//    @Override
//    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
//        BaseViewHolder viewHolder = null;
//        switch (viewType) {
//            case Constants.CHAT_ITEM_TYPE_LEFT:
//                viewHolder = new IMGroupAcceptViewHolder(parent, onItemClickListener, handler);
//                break;
//            case Constants.CHAT_ITEM_TYPE_RIGHT:
//                viewHolder = new IMGroupSendViewHolder(parent, onItemClickListener, handler);
//                break;
//        }
//        return viewHolder;
//    }
//
//    @Override
//    public int getViewType(int position) {
//        return getAllData().get(position).getType();
//    }

    public void addItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case Constants.CHAT_ITEM_TYPE_LEFT:
                viewHolder = new IMGroupAcceptViewHolder(parent, onItemClickListener, handler);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT:
                viewHolder = new IMGroupSendViewHolder(parent, onItemClickListener, handler);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.setData(messageInfoList.get(position));
        NiceImageView headerView = holder.itemView.findViewById(R.id.chat_item_header);
    }

    @Override
    public int getItemViewType(int position) {
        return messageInfoList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (messageInfoList == null) {
            return 0;
        } else {
            return messageInfoList.size();
        }
    }

    public void addAll(List<ImGroupMessageInfo> messageInfos) {
        if (messageInfoList == null) {
            messageInfoList = messageInfos;
        } else {
            messageInfoList.addAll(messageInfos);
        }

        notifyDataSetChanged();
    }

    public void add(ImGroupMessageInfo messageInfo) {
        if (messageInfoList == null) {
            messageInfoList = new ArrayList<>();
        }

        messageInfoList.add(messageInfo);

        notifyDataSetChanged();
    }

    public interface onItemClickListener {
        void onHeaderClick(int position);

        void onImageClick(View view, int position);

        void onVoiceClick(ImageView imageView, int position);

        void onFileClick(View view, int position);

        void onLinkClick(View view, int position);

        void onLongClickImage(View view, int position);

        void onLongClickText(View view, int position);

        void onLongClickItem(View view, int position);

        void onLongClickFile(View view, int position);

        void onLongClickLink(View view, int position);
    }
}
