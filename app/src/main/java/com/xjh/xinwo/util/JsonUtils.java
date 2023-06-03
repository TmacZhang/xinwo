package com.xjh.xinwo.util;

import com.xinwo.log.LibLog;

public class JsonUtils {
    public static String parse2Json(String ...args){
        StringBuilder sb = new StringBuilder();
        int cout = args.length / 2;

        sb.append("{");

        for(int i = 0; i<cout; ++i){
            sb.append("\"")
                    .append(args[2*i])
                    .append("\":\"")
                    .append(args[2*i+1])
                    .append("\",");

        }
        sb.deleteCharAt(sb.length()-1);

        sb.append("}");

        LibLog.i("JsonUtils", sb.toString());
        return sb.toString();
    }
}
