package com.cml.androidnetworkthings.cmlnetwork;

/**
 * Created by chenmingliang on 2017/10/11.
 */

public abstract class SendCallBack<T> {
     public void sendPre(){}
     public void sendSuccess(T t){}
     public void sendSuccess(String json){}
     public void sendError(int errorCode,String msg){}
     public void sendFinish(){}
}
