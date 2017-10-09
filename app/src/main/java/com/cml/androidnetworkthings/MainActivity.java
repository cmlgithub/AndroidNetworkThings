package com.cml.androidnetworkthings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 200;
    public static final int HttpURLConnection = 0;
    public static final int HttpsURLConnection = 2;
    public static final int HttpClient = 1;
    public static final int HttpsClient = 3;
    public String urlString = "http://www.61yzt.com/yztapp_v4/v5.1/dataCenter/appSystem/query.j";
//    public String urlString = "https://www.baidu.com";
    private ProgressBar mProgressBar;
    private boolean isGet = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,TwoActivity.class));

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void HttpClient(View view){
        asynTask(HttpClient);
    }



    public void HttpURLConnection(View view){
        asynTask(HttpURLConnection);
    }
    public void HttpsURLConnection(View view){
        asynTask(HttpsURLConnection);
    }

    private void asynTask(final int code){
        new AsyncTask<String,String,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String result = "";
                try {
                    switch (code){
                        case HttpURLConnection:
                            result = excuteHttpUrlConnectionRequest(params[0]);
                            break;
                        case HttpClient:
                            result = excuteHttpClientRequest(params[0]);
                            break;
                        case HttpsURLConnection:
                            result = excuteHttpsClientRequest(params[0].replace("http","https"));
                            break;
                    }
                } catch (Exception e) {
                    Log.e("CML",e.toString());
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("CML",s);
                mProgressBar.setVisibility(View.GONE);
            }
        }.execute(urlString);
    }

    class MyX509TrustManager implements TrustManager{

    }

    private String excuteHttpsClientRequest(String param) throws Exception{
//        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(null);
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        trustManagerFactory.init(keyStore);

//        TrustManager[] tm = { new MyX509TrustManager() };
//        SSLContext sslContext = SSLContext.getInstance("TSL");
//        sslContext.init(null, tm, new java.security.SecureRandom());

        /** 1. 获取连接*/
        if(isGet){
            urlString = urlString+"?appSystemPack=com.yzt.youzitang&appSystemVersionInside=20";
        }
        URL url = new URL(urlString);
        javax.net.ssl.HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
//        httpURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        /** 2. 设置相关参数*/
        httpURLConnection.setConnectTimeout(5000);// 连接超时时间
        httpURLConnection.setReadTimeout(5000); //资源连接成功之后,读取input流的超时时间
        httpURLConnection.setUseCaches(false); //缓存
        if(isGet){
            httpURLConnection.setRequestMethod("GET");
        }else {
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true); //可下载(输入流)
            httpURLConnection.setDoOutput(true); //可上传(输出流)
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            printWriter.write("appSystemPack=com.yzt.youzitang&appSystemVersionInside=20");
            printWriter.flush();
//            httpURLConnection.getOutputStream().write("appSystemPack=com.yzt.youzitang&appSystemVersionInside=20".getBytes());
        }
        /** 3.获取返回结果*/
        httpURLConnection.connect();
        StringBuffer stringBuffer = new StringBuffer("");
        if(REQUEST_CODE == httpURLConnection.getResponseCode()){
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String inputLine = null;
            while ((inputLine = bufferedReader.readLine()) != null){
                stringBuffer.append(inputLine);
            }
            inputStream.close();
        }else {
            stringBuffer.append("error"+httpURLConnection.getResponseCode());
        }
        httpURLConnection.disconnect();
        return stringBuffer.toString();
    }

    private String excuteHttpClientRequest(String urlString)throws Exception{
        String result = "";
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        if(isGet){
            HttpGet httpGet = new HttpGet(urlString+"?appSystemPack=com.yzt.youzitang&appSystemVersionInside=20");
            try {
                HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    result = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            HttpPost httpPost = new HttpPost(urlString);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("appSystemPack","com.yzt.youzitang"));
            params.add(new BasicNameValuePair("appSystemVersionInside","20"));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                result = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            }
        }

        return result;
    }

    private String excuteHttpUrlConnectionRequest(String urlString)throws Exception{
        /** 1. 获取连接*/
        if(isGet){
            urlString = urlString+"?appSystemPack=com.yzt.youzitang&appSystemVersionInside=20";
        }
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        /** 2. 设置相关参数*/
        httpURLConnection.setConnectTimeout(5000);// 连接超时时间
        httpURLConnection.setReadTimeout(5000); //资源连接成功之后,读取input流的超时时间
        httpURLConnection.setUseCaches(false); //缓存
        if(isGet){
            httpURLConnection.setRequestMethod("GET");
        }else {
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true); //可下载(输入流)
            httpURLConnection.setDoOutput(true); //可上传(输出流)
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            printWriter.write("appSystemPack=com.yzt.youzitang&appSystemVersionInside=20");
            printWriter.flush();
//            httpURLConnection.getOutputStream().write("appSystemPack=com.yzt.youzitang&appSystemVersionInside=20".getBytes());
        }
        /** 3.获取返回结果*/
        httpURLConnection.connect();
        StringBuffer stringBuffer = new StringBuffer("");
        if(REQUEST_CODE == httpURLConnection.getResponseCode()){
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String inputLine = null;
            while ((inputLine = bufferedReader.readLine()) != null){
                stringBuffer.append(inputLine);
            }
            inputStream.close();
        }else {
            stringBuffer.append("error"+httpURLConnection.getResponseCode());
        }
        httpURLConnection.disconnect();
        return stringBuffer.toString();
    }



}
