package com.lxy.basemodel.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.lxy.basemodel.BaseApp;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Creator : lxy
 * date: 2019/1/30
 */

public class AddCookieInterceptor implements Interceptor {
    private static final String COOKIE_PREF = "cookies_prefs";


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        HashSet cookies = getCookie(request.url().toString(), request.url().host());

        for (Object header : cookies) {
            Log.d("OkCookieAdd",header.toString());
            builder.addHeader("Cookie",(String)header);
        }

        return chain.proceed(builder.build());
    }

    private HashSet getCookie(String url, String domain) {
        SharedPreferences sp = BaseApp.getContext().getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(url) && sp.contains(url)) {
            return (HashSet) sp.getStringSet(url,new HashSet<>());
        }
        if (!TextUtils.isEmpty(domain) && sp.contains(domain) ) {
            return (HashSet) sp.getStringSet(domain,new HashSet<>());
        }
        return new HashSet<>();
    }
}
