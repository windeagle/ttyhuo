package cn.ttyhuo.utils;

import android.graphics.Bitmap;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-6-26
 * Time: 下午7:02
 * To change this template use File | Settings | File Templates.
 */
public class ApacheHttpRequestUtil {

    public static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    /**
     * 发送GET请求并获取服务器端返回值
     */
    public static String handleGet(String strUrl,
                                   Map<String, String> params, Map<String, String> headers) {
//        CookieManager cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        String result;
        DefaultHttpClient client = new DefaultHttpClient();//实例化客户端

        Set<Map.Entry<String, String>> entries;
        StringBuilder buf = new StringBuilder(strUrl);
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue()))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }

        HttpGet request = new HttpGet(buf.toString());//实例化get请求

        if (headers != null && !headers.isEmpty()) {
            entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                //mPost.addHeader(new BasicHeader(entry.getKey(), URLEncoder.encode(entry.getValue())));
                request.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }

        try {
            HttpResponse response = client.execute(request);//执行该请求,得到服务器端的响应内容
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());//把响应结果转成String
            } else {
                result = response.getStatusLine().toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return result;
    }

    private String handlePost(String strUrl,
                              Map<String, String> params, Map<String, String> headers){
        CookieHandler.setDefault(cookieManager);
        String result;
        HttpPost mPost = new HttpPost(strUrl); //实例化post请求
        DefaultHttpClient mHttpClient = new DefaultHttpClient();//实例化客户端

        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        Set<Map.Entry<String, String>> entries;
        if (params != null && !params.isEmpty()) {
            entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                //pairs.add(new BasicNameValuePair(entry.getKey(), URLEncoder.encode(entry.getValue())));
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        if (headers != null && !headers.isEmpty()) {
            entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                //mPost.addHeader(new BasicHeader(entry.getKey(), URLEncoder.encode(entry.getValue())));
                mPost.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
        }

        try {
            mPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            HttpResponse response = mHttpClient.execute(mPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());//把响应结果转成String
            } else {
                result = response.getStatusLine().toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return result;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] getHtmlByteArray(final String url) {
        InputStream inStream = getInputStream(url);
        return inputStreamToByte(inStream);
    }

    public static InputStream getInputStream(String url) {
        InputStream inStream = null;
        try {
            URL htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inStream;
    }

    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                byteStream.write(ch);
            }
            byte imgData[] = byteStream.toByteArray();
            byteStream.close();
            return imgData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
