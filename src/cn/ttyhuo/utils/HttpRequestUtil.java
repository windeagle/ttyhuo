package cn.ttyhuo.utils;

import cn.ttyhuo.common.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-6-26
 * Time: 下午6:57
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestUtil {

    public static final String LOG_TAG = "cn.ttyhuo.utils.HttpRequestUtil";
    public static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    /**
     * 发送GET请求
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendGetRequest(String url,
                                               Map<String, String> params, Map<String, String> headers)
            throws Exception{
        LogUtils.d(LOG_TAG, url);
        cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        MyApplication.getJavaCookieStore(cookieManager.getCookieStore());
        CookieHandler.setDefault(cookieManager);
        StringBuilder buf = new StringBuilder(url);
        // 如果是GET请求，则请求参数在URL中
        if (params != null && !params.isEmpty()) {
            buf.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(buf.toString());
        LogUtils.d(LOG_TAG, buf.toString());
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("GET");
        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.setConnectTimeout(5 * 1000);
//        conn.getResponseCode(); // 为了发送成功
        return conn;
    }

    /**
     * 发送POST请求
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    public static URLConnection sendPostRequest(String url,
                                                Map<String, String> params, Map<String, String> headers)
            throws Exception {
        LogUtils.d(LOG_TAG, url);
        cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        MyApplication.getJavaCookieStore(cookieManager.getCookieStore());
        CookieHandler.setDefault(cookieManager);
        StringBuilder buf = new StringBuilder();
        // 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
        URL url1 = new URL(url);
        LogUtils.d(LOG_TAG, buf.toString());
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("POST");
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();
        out.write(buf.toString().getBytes("UTF-8"));
        conn.setConnectTimeout(5 * 1000);
        out.flush();
        out.close();
//        conn.getResponseCode(); // 为了发送成功
        return conn;
    }

    /**
     * 将输入流转为字节数组
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] read2Byte(InputStream inStream)throws Exception{
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = inStream.read(buffer)) !=-1 ){
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    /**
     * 将输入流转为字符串
     * @param inStream
     * @return
     * @throws Exception
     */
    public static String read2String(InputStream inStream)throws Exception{
        return new String(read2Byte(inStream),"UTF-8");
    }

    /**
     * 发送xml数据
     * @param path 请求地址
     * @param xml xml数据
     * @param encoding 编码
     * @return
     * @throws Exception
     */
    public static byte[] postXml(String path, String xml, String encoding) throws Exception{
        byte[] data = xml.getBytes(encoding);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset="+ encoding);
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        conn.setConnectTimeout(5 * 1000);
        outStream.flush();
        outStream.close();
        if(conn.getResponseCode()==200){
            return read2Byte(conn.getInputStream());
        }
        return null;
    }
}
