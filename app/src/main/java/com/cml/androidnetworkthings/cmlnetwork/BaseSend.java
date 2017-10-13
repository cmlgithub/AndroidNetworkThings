package com.cml.androidnetworkthings.cmlnetwork;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cml.androidnetworkthings.VolleyActivity;
import com.cml.androidnetworkthings.cmlrequest.GsonRequest;
import com.cml.androidnetworkthings.util.Bean;
import com.cml.androidnetworkthings.util.Contasts;
import com.google.gson.Gson;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;

import java.util.Map;
import java.util.Set;

/**
 * Created by chenmingliang on 2017/10/11.
 */

public class BaseSend<T> implements  Isend<T>{


    private final  RequestQueue mRequestQueue;
    public static final int GET = Request.Method.GET;
    public static final int POST = Request.Method.POST;
    private Class mClass;
    private com.cml.androidnetworkthings.cmlnetwork.SendCallBack<T> sendCallBack;


    public BaseSend(Context context,Class mClass){
        mRequestQueue = Volley.newRequestQueue(context);
        this.mClass = mClass;
    }

    @Override
    public void send(String url, com.cml.androidnetworkthings.cmlnetwork.SendCallBack sendCallBack) {
        send(GET,url,sendCallBack);
    }

    @Override
    public void send(int sendMethod, String url, com.cml.androidnetworkthings.cmlnetwork.SendCallBack<T> sendCallBack) {
        send(sendMethod,url,null,sendCallBack);
    }

    @Override
    public void send(int sendMethod, String url, final Map<String, String> mapParams, final com.cml.androidnetworkthings.cmlnetwork.SendCallBack<T> sendCallBack) {

        this.sendCallBack = sendCallBack;

        sendCallBack.sendPre();

        GsonRequest<T> tGsonRequest = new GsonRequest(sendMethod,Contasts.URLSTRING, mClass, new Response.Listener<T>() {
            @Override
            public void onResponse(T t) {
                sendCallBack.sendSuccess(t);
                sendCallBack.sendFinish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                sendCallBack.sendError(volleyError.networkResponse.statusCode, volleyError.getMessage());
                sendCallBack.sendFinish();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mapParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };

        mRequestQueue.add(tGsonRequest);

        HttpParams httpParams = new HttpParams();
        for (String key : mapParams.keySet()){
            httpParams.put(key,mapParams.get(key));
        }
        KJHttp kjHttp = new KJHttp();
        if(GET == sendMethod){
            kjHttp.get(url, httpParams, new SendCallBack());
        }else {
            kjHttp.post(url, httpParams, new SendCallBack());
        }

    }

    class SendCallBack extends HttpCallBack{
        @Override
        public void onPreStart() {
            super.onPreStart();
            sendCallBack.sendPre();
        }

        @Override
        public void onSuccess(String t) {
            super.onSuccess(t);
            sendCallBack.sendSuccess(t);
        }

        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            sendCallBack.sendError(errorNo,strMsg);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            sendCallBack.sendFinish();
        }
    }
}
