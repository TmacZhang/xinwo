package com.xinwo.social.chat.entity;


import com.xjh.xinwo.mvp.model.BaseBean;

/**
 * 用户信息
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class WXLoginBean extends BaseBean {

    /**
     * message : {"code":200,"msg":"SUCCESS"}
     * appid : wxa78ebfd006b71da9
     * user_id : 5d70d6af3226ca5f0af9a77f
     * jwt_token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3eGE3OGViZmQwMDZiNzFkYTkiLCJleHAiOjE2MDQxODcwNzcsImp0aSI6ImJsc2E1aGUzbnYwMjdsNDAwN25nLjVkNzBkNmFmMzIyNmNhNWYwYWY5YTc3ZiIsImlhdCI6MTU2ODE4NzA3NywiaXNzIjoiNWQ3MGQ2YWYzMjI2Y2E1ZjBhZjlhNzdmIn0.W-AUFLSKCmKZjfQYzAmVuZWK6bN6OgbSO104owcijcM
     * nick : test
     */

    public String appid;
    public String user_id;
    public String jwt_token;
    public String nick;

}
