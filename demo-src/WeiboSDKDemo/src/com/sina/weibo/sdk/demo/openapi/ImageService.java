package com.sina.weibo.sdk.demo.openapi;

/**
 * Created by 夜凉如梦 on 2016/10/27.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sina.weibo.sdk.demo.openapi.StreamTool;

public class ImageService {

    public static byte[] getImage(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");   //设置请求方法为GET
        conn.setReadTimeout(5*1000);    //设置请求过时时间为5秒
        InputStream inputStream = conn.getInputStream();   //通过输入流获得图片数据
        byte[] data = StreamTool.readInputStream(inputStream);     //获得图片的二进制数据
        return data;

    }
}