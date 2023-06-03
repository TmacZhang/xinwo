package com.xjh.xinwo.mvp.model;

public class BaseBean {
    public MessageBean message;

    public static class MessageBean {
        /**
         * code : 200
         * msg : SUCCESS
         */

        public int code;
        public String msg;
    }
}
