package com.example.myapplication.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/18.
 */
public class HttpUtils {
    private static final int CONNECT_TIME_OUT = 10_000;
    private  static final int READ_TIME_OUT  = 10_000;


    public static byte[] downLoadGET(String downUrl){
        BufferedInputStream bis =null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(downUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.connect();
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK){
                bis = new BufferedInputStream(conn.getInputStream());
                baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];//8*1024;
                int len = -1;
                while ((len = bis.read(buf)) != -1){
                    baos.write(buf,0,len);
                }
                return baos.toByteArray();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String downLoadGETString(String downUrl){
        byte[] buf = downLoadGET(downUrl);
        return new String(buf,0,buf.length);
    }

}
