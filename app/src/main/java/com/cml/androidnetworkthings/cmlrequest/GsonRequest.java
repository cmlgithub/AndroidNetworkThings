package com.cml.androidnetworkthings.cmlrequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by chenmingliang on 2017/10/10.
 */

public class GsonRequest<T> extends Request<T> {

    private Response.Listener<T> mListener;

    private Class<T> mClass ;

    private Gson mGson;

    public GsonRequest(int method, String url, Class<T> tClass, Response.Listener listener , Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mClass = tClass;
        mGson = new Gson();
    }
    public GsonRequest(String url, Class<T> tClass,Response.Listener listener , Response.ErrorListener errorListener) {
        this(0, url, tClass,listener,errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String xmlString = "";
        try {
            xmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(xmlString,mClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T t) {
        mListener.onResponse(t);
    }
}
