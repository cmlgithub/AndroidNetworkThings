package com.cml.androidnetworkthings;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cml.androidnetworkthings.R;
import com.cml.androidnetworkthings.cmlnetwork.BaseSend;
import com.cml.androidnetworkthings.cmlnetwork.SendCallBack;
import com.cml.androidnetworkthings.cmlrequest.GsonRequest;
import com.cml.androidnetworkthings.cmlrequest.XmlRequest;
import com.cml.androidnetworkthings.util.Bean;
import com.cml.androidnetworkthings.util.BitmapCache;
import com.cml.androidnetworkthings.util.Contasts;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * volley源码解析
 *
 * http://www.jianshu.com/p/15e6209d2e6f
 * http://a.codekk.com/detail/Android/grumoon/Volley%20源码解析
 */
public class VolleyActivity extends AppCompatActivity {


    private RequestQueue queue = null;
    private ImageView mImageView;
    private ImageView mImageView2;
    private NetworkImageView mImageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mImageView3 = (NetworkImageView) findViewById(R.id.imageView3);

        queue = Volley.newRequestQueue(this);

        volleyGetRequest();
        volleyPostRequest();
        volleyJsonRequest();
        volleyImageRequest();
        volleyImageLoader();
        volleyNetWorkImageView();
        cmlVolleyXmlRequest();
        cmlGsonRequest();

        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("appSystemPack","com.yzt.youzitang");
        mapParams.put("appSystemVersionInside","20");
        new BaseSend(this,Bean.class).send(BaseSend.POST,Contasts.URLSTRING, mapParams,new SendCallBack<Bean>() {
            @Override
            public void sendSuccess(Bean bean) {
                super.sendSuccess(bean);
                Toast.makeText(VolleyActivity.this, bean.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void sendSuccess(String json) {
                super.sendSuccess(json);
                Toast.makeText(VolleyActivity.this, json, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void volleyGetRequest() {
        queue.add(new StringRequest(Contasts.URLBaiDu, new ResponseSuccess(), new ResponseError()));
    }

    private void volleyPostRequest(){
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Contasts.URLSTRING, new ResponseSuccess(), new ResponseError()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put(Contasts.PARAMS1, Contasts.VALUE1);
                map.put(Contasts.PARAMS2, Contasts.VALUE2);
                return map;
            }
        };
        queue.add(stringPostRequest);
    }

    private void volleyJsonRequest(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Contasts.URLSTRING, null, new ResponseSuccessJSON(), new ResponseError());
        queue.add(jsonObjectRequest);
    }

    private void volleyImageRequest(){
        ImageRequest imageRequest = new ImageRequest(Contasts.IMAGEURL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new ResponseError());
        queue.add(imageRequest);
    }

    private void volleyImageLoader(){
        ImageLoader imageLoader = new ImageLoader(queue, new BitmapCache());

        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(mImageView2, R.mipmap.ic_launcher, R.mipmap.ic_launcher_round);

        imageLoader.get(Contasts.IMAGEURL,imageListener,0,0);
    }

    private void volleyNetWorkImageView(){
        ImageLoader imageLoader = new ImageLoader(queue, new BitmapCache());
        mImageView3.setDefaultImageResId(R.mipmap.ic_launcher);
        mImageView3.setErrorImageResId(R.mipmap.ic_launcher_round);
        mImageView3.setImageUrl(Contasts.IMAGEURL,imageLoader);
    }

    private void cmlVolleyXmlRequest(){
        queue.add(new XmlRequest(Contasts.XMLURL,new ResponseSuccessXML(),new ResponseError()));
    }

    private void cmlGsonRequest(){
        queue.add(new GsonRequest<Bean>(Contasts.URLSTRING,Bean.class, new ResponseSuccessGson(), new ResponseError()));
    }

    class ResponseSuccess implements Response.Listener<String>{

        @Override
        public void onResponse(String s) {
            Log.e(Contasts.TAG, s + "");
        }
    }
    class ResponseSuccessJSON implements Response.Listener<JSONObject>{

        @Override
        public void onResponse(JSONObject s) {
            Log.e(Contasts.TAG, s + "");
        }
    }
    class ResponseSuccessXML implements Response.Listener<XmlPullParser>{

        @Override
        public void onResponse(XmlPullParser response) {
            try {
                int eventType = response.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            String nodeName = response.getName();
                            if ("versioncode".equals(nodeName)) {
                                Log.e(Contasts.TAG, nodeName);
                            }
                            break;
                    }
                    eventType = response.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ResponseSuccessGson implements Response.Listener<Bean>{

        @Override
        public void onResponse(Bean bean) {
            Log.e(Contasts.TAG,bean.msg);
        }
    }


    class ResponseError implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e(Contasts.TAG, volleyError.toString());
//            mImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }
}
