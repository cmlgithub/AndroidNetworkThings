package com.cml.androidnetworkthings.cmlnetwork;

import java.util.Map;

/**
 * Created by chenmingliang on 2017/10/11.
 */

public interface Isend<T> {
    void send(String url,SendCallBack<T> sendCallBack);
    void send(int sendMethod,String url,SendCallBack<T> sendCallBack);
    void send(int sendMethod, String url, Map<String,String> mapParams, SendCallBack<T> sendCallBack);
}
