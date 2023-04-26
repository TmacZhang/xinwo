package com.xjh.xinwo.module.groupchat.adapter.holder;

import android.content.Context;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.shehuan.niv.NiceImageView;
import com.xjh.xinwo.R;
import com.xjh.xinwo.enity.IMContact;
import com.xjh.xinwo.enity.ImGroupMessageInfo;
import com.xjh.xinwo.enity.Link;
import com.xjh.xinwo.module.groupchat.adapter.IMGroupAdapter;
import com.xjh.xinwo.util.Constants;
import com.xjh.xinwo.util.FileUtils;
import com.xjh.xinwo.util.Utils;
import com.xjh.xinwo.widget.BubbleImageView;
import com.xjh.xinwo.widget.BubbleLinearLayout;
import com.xjh.xinwo.widget.GifTextView;


public class IMGroupSendViewHolder extends BaseViewHolder<ImGroupMessageInfo> {
    private static final String TAG = "IMGroupSendViewHolder";
    TextView chatItemDate;
    NiceImageView chatItemHeader;
    GifTextView chatItemContentText;
    BubbleImageView chatItemContentImage;
    ImageView chatItemFail;
    ProgressBar chatItemProgress;
    ImageView chatItemVoice;
    BubbleLinearLayout chatItemLayoutContent;
    TextView chatItemVoiceTime;
    BubbleLinearLayout chatItemLayoutFile;
    ImageView ivFileType;
    TextView tvFileName;
    TextView tvFileSize;

    BubbleLinearLayout chatItemLayoutContact;
    TextView tvContactSurname;
    TextView tvContactName;
    TextView tvContactPhone;

    BubbleLinearLayout chatItemLayoutLink;
    TextView tvLinkSubject;
    TextView tvLinkText;
    ImageView ivLinkPicture;
    private IMGroupAdapter.onItemClickListener onItemClickListener;
    private Handler handler;
    private RelativeLayout.LayoutParams layoutParams;
    private Context mContext;

    public IMGroupSendViewHolder(ViewGroup parent, IMGroupAdapter.onItemClickListener onItemClickListener, Handler handler) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send, parent, false));
        findViewByIds(itemView);
        setItemLongClick();
        setItemClick();
        this.mContext = parent.getContext();
        this.onItemClickListener = onItemClickListener;
        this.handler = handler;
        layoutParams = (RelativeLayout.LayoutParams) chatItemLayoutContent.getLayoutParams();
    }

    private void findViewByIds(View itemView) {
        chatItemDate = (TextView) itemView.findViewById(R.id.chat_item_date);
        chatItemHeader = (NiceImageView) itemView.findViewById(R.id.chat_item_header);
        chatItemContentText = (GifTextView) itemView.findViewById(R.id.chat_item_content_text);
        chatItemFail = (ImageView) itemView.findViewById(R.id.chat_item_fail);
        chatItemProgress = (ProgressBar) itemView.findViewById(R.id.chat_item_progress);
        chatItemContentImage = (BubbleImageView) itemView.findViewById(R.id.chat_item_content_image);
        chatItemVoice = (ImageView) itemView.findViewById(R.id.chat_item_voice);
        chatItemLayoutContent = (BubbleLinearLayout) itemView.findViewById(R.id.chat_item_layout_content);
        chatItemVoiceTime = (TextView) itemView.findViewById(R.id.chat_item_voice_time);
        chatItemLayoutFile = (BubbleLinearLayout) itemView.findViewById(R.id.chat_item_layout_file);
        ivFileType = (ImageView) itemView.findViewById(R.id.iv_file_type);
        tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
        tvFileSize = (TextView) itemView.findViewById(R.id.tv_file_size);
        chatItemLayoutContact = (BubbleLinearLayout) itemView.findViewById(R.id.chat_item_layout_contact);
        tvContactSurname = (TextView) itemView.findViewById(R.id.tv_contact_surname);
        tvContactPhone = (TextView) itemView.findViewById(R.id.tv_contact_phone);
        chatItemLayoutLink = (BubbleLinearLayout) itemView.findViewById(R.id.chat_item_layout_link);
        tvLinkSubject = (TextView) itemView.findViewById(R.id.tv_link_subject);
        tvLinkText = (TextView) itemView.findViewById(R.id.tv_link_text);
        ivLinkPicture = (ImageView) itemView.findViewById(R.id.iv_link_picture);
    }


    @Override
    public void setData(ImGroupMessageInfo data) {
        chatItemDate.setText(data.getTime() != null ? data.getTime() : "");
//        Glide.with(mContext).load(data.getHeader()).into(chatItemHeader);
        chatItemHeader.setImageResource(R.drawable.header_man);
        chatItemHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onHeaderClick((Integer) itemView.getTag());
            }
        });

        Log.e(TAG,"setData --> " + data);

        switch (data.getFileType()) {
            case Constants.CHAT_FILE_TYPE_TEXT:
                chatItemContentText.setSpanText(handler, data.getContent(), true);
                chatItemVoice.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemLayoutFile.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);
                chatItemLayoutLink.setVisibility(View.GONE);

                chatItemContentText.setVisibility(View.VISIBLE);
                chatItemLayoutContent.setVisibility(View.VISIBLE);
                TextPaint paint = chatItemContentText.getPaint();
                paint.setColor(ContextCompat.getColor(mContext, R.color.chat_send_text));
                // 计算textview在屏幕上占多宽
                int len = (int) paint.measureText(chatItemContentText.getText().toString().trim());
                if (len < Utils.dp2px(mContext, 200)){
                    layoutParams.width = len + Utils.dp2px(mContext, 30);
                } else {
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                }
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                chatItemLayoutContent.setLayoutParams(layoutParams);
                break;
            case Constants.CHAT_FILE_TYPE_IMAGE:
                chatItemVoice.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemLayoutFile.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);
                chatItemLayoutLink.setVisibility(View.GONE);

                chatItemContentImage.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(data.getFilepath()).into(chatItemContentImage);
                layoutParams.width = Utils.dp2px(mContext, 120);
                layoutParams.height = Utils.dp2px(mContext, 48);
                chatItemLayoutContent.setLayoutParams(layoutParams);
                break;
            case Constants.CHAT_FILE_TYPE_VOICE:
                chatItemVoice.setVisibility(View.VISIBLE);
                chatItemLayoutContent.setVisibility(View.VISIBLE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.VISIBLE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);
                chatItemLayoutLink.setVisibility(View.GONE);

                chatItemLayoutFile.setVisibility(View.GONE);
                chatItemVoiceTime.setText(Utils.formatTime(data.getVoiceTime()));
                layoutParams.width = Utils.dp2px(mContext, 120);
                layoutParams.height = Utils.dp2px(mContext, 48);
                chatItemLayoutContent.setLayoutParams(layoutParams);
                break;
            case Constants.CHAT_FILE_TYPE_FILE:
                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);
                chatItemLayoutLink.setVisibility(View.GONE);

                chatItemLayoutFile.setVisibility(View.VISIBLE);
                tvFileName.setText(FileUtils.getFileName(data.getFilepath()));
                try {
                    tvFileSize.setText(FileUtils.getFileSize(data.getFilepath()));
                    switch (FileUtils.getExtensionName(data.getFilepath())) {
                        case "doc":
                        case "docx":
                            ivFileType.setImageResource(R.mipmap.icon_file_word);
                            break;
                        case "ppt":
                        case "pptx":
                            ivFileType.setImageResource(R.mipmap.icon_file_ppt);
                            break;
                        case "xls":
                        case "xlsx":
                            ivFileType.setImageResource(R.mipmap.icon_file_excel);
                            break;
                        case "pdf":
                            ivFileType.setImageResource(R.mipmap.icon_file_pdf);
                            break;
                        default:
                            ivFileType.setImageResource(R.mipmap.icon_file_other);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Constants.CHAT_FILE_TYPE_CONTACT:
                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemLayoutFile.setVisibility(View.GONE);

                chatItemLayoutContact.setVisibility(View.VISIBLE);

                IMContact imContact = (IMContact) data.getObject();
                tvContactSurname.setText(imContact.getSurname());
                tvContactName.setText(imContact.getName());
                tvContactPhone.setText(imContact.getPhonenumber());
                break;
            case Constants.CHAT_FILE_TYPE_LINK:
                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemLayoutFile.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);

                chatItemLayoutLink.setVisibility(View.VISIBLE);
                Link link = (Link) data.getObject();

                tvLinkSubject.setText(link.getSubject());
                tvLinkText.setText(link.getText());
                Glide.with(mContext).load(link.getStream()).into(ivLinkPicture);
                break;
        }
        switch (data.getSendState()) {
            case Constants.CHAT_ITEM_SENDING:
                chatItemProgress.setVisibility(View.VISIBLE);
                chatItemFail.setVisibility(View.GONE);
                break;
            case Constants.CHAT_ITEM_SEND_ERROR:
                chatItemProgress.setVisibility(View.GONE);
                chatItemFail.setVisibility(View.VISIBLE);
                break;
            case Constants.CHAT_ITEM_SEND_SUCCESS:
                chatItemProgress.setVisibility(View.GONE);
                chatItemFail.setVisibility(View.GONE);
                break;
        }
    }

    public void setItemLongClick() {
        chatItemContentImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongClickImage(v, (Integer) itemView.getTag());
                return true;
            }
        });
        chatItemContentText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongClickText(v, (Integer) itemView.getTag());
                return true;
            }
        });
        chatItemLayoutContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongClickItem(v, (Integer) itemView.getTag());
                return true;
            }
        });
        chatItemLayoutFile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongClickFile(v, (Integer) itemView.getTag());
                return true;
            }
        });
    }

    public void setItemClick() {
        chatItemContentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onImageClick(chatItemContentImage, (Integer) itemView.getTag());
            }
        });

        chatItemLayoutFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onVoiceClick(chatItemVoice, (Integer) itemView.getTag());
            }
        });

        chatItemLayoutFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onFileClick(v, (Integer) itemView.getTag());
            }
        });

        chatItemLayoutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onLinkClick(v, (Integer) itemView.getTag());
            }
        });
    }
}