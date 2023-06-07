package com.xjh.xinwo.util;

import android.media.MediaPlayer;
import android.net.Uri;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.xinwo.base.BaseApplication;

import java.io.File;

/**
 * 网易云信工具类
 */
public class NimUtils {


    /**
     * 创建一条普通文本消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 会话类型, SessionTypeEnum.P2P 为单聊类型，SessionTypeEnum.Team 为群聊类型
     * @param text        文本消息内容
     * @return IMMessage 生成的消息对象
     */
    public static IMMessage createTextMessage(String sessionId, SessionTypeEnum sessionType, String text){
        IMMessage textMessage = MessageBuilder.createTextMessage(sessionId, sessionType, text);
        return textMessage;
    }

    /**
     * 创建一条图片消息
     *
     * @param sessionId   聊天对象ID
     * @param sessionType 会话类型
     * @param file        图片文件
     * @param displayName 图片文件的显示名，可不同于文件名
     * @return IMMessage 生成的消息对象
     */
    public static IMMessage createImageMessage(String sessionId, SessionTypeEnum sessionType, File file, String displayName){
        // 创建一个图片消息
        IMMessage message = MessageBuilder.createImageMessage(sessionId, sessionType, file, file.getName());
        return message;
    }

    /**
     * 创建一条音频消息
     *
     * @param sessionId   聊天对象ID
     * @param sessionType 会话类型
     * @param audioFile        音频文件对象
     * @param duration    音频文件持续时间，单位是ms
     * @return IMMessage 生成的消息对象
     */
    public static IMMessage createAudioMessage(String sessionId, SessionTypeEnum sessionType, File audioFile, long duration){
        // 创建音频消息
        IMMessage audioMessage = MessageBuilder.createAudioMessage(sessionId, sessionType, audioFile, duration);
        return audioMessage;
    }

    /**
     * 创建一条视频消息
     *
     * @param sessionId   聊天对象ID
     * @param sessionType 会话类型
     * @param file        视频文件对象
     * @param displayName 视频文件显示名，可以为空
     * @return 视频消息
     */
    public static IMMessage createVideoMessage(String sessionId, SessionTypeEnum sessionType, File file, String displayName){
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = MediaPlayer.create(BaseApplication.getInstance(), Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 视频文件持续时间
        long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
        // 视频高度
        int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
        // 视频宽度
        int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
        // 创建视频消息
        IMMessage message = MessageBuilder.createVideoMessage(sessionId, sessionType, file, duration, width, height, null);

        return message;
    }

    /**
     * 创建一条文件消息
     *
     * @param sessionId   聊天对象ID
     * @param sessionType 会话类型
     * @param file        文件
     * @param displayName 文件的显示名，可不同于文件名
     * @return IMMessage 生成的消息对象
     */
    public static IMMessage createFileMessage(String sessionId, SessionTypeEnum sessionType, File file, String displayName){
        // 创建文件消息
        IMMessage message = MessageBuilder.createFileMessage(sessionId, sessionType, file, displayName);

        return message;
    }


    /**
     * 创建一条地理位置信息
     *
     * @param sessionId   聊天对象ID
     * @param sessionType 会话类型
     * @param lat         纬度
     * @param lng         经度
     * @param addr        地理位置描述信息
     * @return IMMessage 生成的消息对象
     */
    public static IMMessage createLocationMessage(String sessionId, SessionTypeEnum sessionType, double lat, double lng, String addr){
        // 创建地理位置信息
        IMMessage message = MessageBuilder.createLocationMessage(sessionId, sessionType, lat, lng, addr);

        return message;
    }

    public static void sendMessage(IMMessage message){
        // 发送给对方
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }
}
