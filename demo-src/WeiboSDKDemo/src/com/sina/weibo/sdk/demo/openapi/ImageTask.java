package com.sina.weibo.sdk.demo.openapi;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by stariy on 16-10-29.
 */

public class ImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView iv;
    public ImageTask(ImageView iv){
        this.iv = iv;
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Bitmap doInBackground(String... param) {
        String imgUrl = param[0];
        try {
            URL url = new URL(imgUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                if(bitmap!=null){
                    Bitmap bitmap1=createBitmap(400,300, Bitmap.Config.RGB_565);
                    return bitmap;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if(result != null){
            try
            {
                int width = result.getWidth();
                int height = result.getHeight();
                // 设置想要的大小
                int newWidth = 450;
                int newHeight = 500;
                // 计算缩放比例
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleHeight, scaleHeight);
                // 得到新的图片
                result = Bitmap.createBitmap(result, 0, 0, width, height, matrix,
                        true);
                iv.setImageBitmap(result);
            }
            catch (Exception e)
            {


            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}