package com.xinwo.xinutil;

import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import java.util.Date;
import java.util.List;

/**
 * 解密 * @author 张超 *
 */
public final class Decrypt {
    private final static String TAG = "JWT";

    public static void JWTParse(String token) {
        JWT jwt = new JWT(token);
/**
 * Registered Claims
 */
//Returns the Issuer value or null if it's not defined.
        String issuer = jwt.getIssuer();
        Log.i(TAG,"issuer = " + issuer);

//Returns the Subject value or null if it's not defined.
        String subject = jwt.getSubject();
        Log.i(TAG,"subject  = " + subject);

//Returns the Audience value or an empty list if it's not defined.
        List<String> audience = jwt.getAudience();
        Log.i(TAG,"audience = " + audience);

//Returns the Expiration Time value or null if it's not defined.
        Date expiresAt = jwt.getExpiresAt();
        Log.i(TAG,"expiresAt = " + expiresAt);

//Returns the Not Before value or null if it's not defined.
        Date notBefore = jwt.getNotBefore();
        Log.i(TAG,"notBefore = " + notBefore);

//Returns the Issued At value or null if it's not defined.
        Date issuedAt = jwt.getIssuedAt();
        Log.i(TAG,"issuedAt = " + issuedAt);

//Returns the JWT ID value or null if it's not defined.
        String id = jwt.getId();
        Log.i(TAG,"id = " + id);

//Time Validation
        boolean isExpired = jwt.isExpired(10); // 10 seconds leeway
        Log.i(TAG,"isExpired = " + isExpired);
/**
 * Private Claims
 */
        Claim claim = jwt.getClaim("isAdmin");

    }

}

