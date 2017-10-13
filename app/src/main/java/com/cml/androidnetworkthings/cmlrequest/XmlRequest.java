package com.cml.androidnetworkthings.cmlrequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by chenmingliang on 2017/10/10.
 */

public class XmlRequest extends Request<XmlPullParser> {

    private Response.Listener<XmlPullParser> mListener;

    public XmlRequest(int method, String url, Response.Listener<XmlPullParser> listener, Response.ErrorListener errorListener){
        super(method, url, errorListener);
        this.mListener = listener;
    }

    public XmlRequest(String url, Response.Listener<XmlPullParser> listener,Response.ErrorListener errorListener) {
        this(0,url,listener,errorListener);

    }

    @Override
    protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
        try {
            String xmlString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (XmlPullParserException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(XmlPullParser xmlPullParser) {
        mListener.onResponse(xmlPullParser);
    }
}
