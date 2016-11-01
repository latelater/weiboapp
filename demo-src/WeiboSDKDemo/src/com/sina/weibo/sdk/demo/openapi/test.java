package com.sina.weibo.sdk.demo.openapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.demo.R;
import com.sina.weibo.sdk.demo.openapi.ImageTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by stariy on 16-10-27.
 */
public class test extends LinearLayout {
    private ImageView headpic;
    private TextView nametext;
    private TextView usertext;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private Context mycontext;
    private LinearLayout linearLayout3;
    private ImageView []im;
    private LinearLayout backg;

    //    private ImageView anthorpic;
    private void initView(Context context) {
        // TODO Auto-generated method stub
        View.inflate(context, R.layout.showweibo, this);
        headpic = (ImageView) findViewById(R.id.user_weibo_head);
        nametext = (TextView) findViewById(R.id.user_wei_name);
        usertext = (TextView) findViewById(R.id.user_weibo_text);
        linearLayout1 = (LinearLayout) findViewById(R.id.hang1);

    }

    public test(Context context) {
        super(context);
        initView(context);
        this.mycontext = context;
    }

    public test(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setcontent(String name, String text) {
        nametext.setText(name);
        usertext.setText(text);

    }

    public void getpicture(String url) {
        byte[] data = new byte[0];
        try {
            data = ImageService.getImage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        headpic.setImageBitmap(bitmap);


    }

    public void manypic(ArrayList<String> picarr) {
        im=new ImageView[picarr.size()];
        Log.i("图片的张数", String.valueOf(picarr.size()));
        for (int j = 0; j < picarr.size(); j++) {

            String url = picarr.get(j).replace("thumbnail", "mw1024");
            im[j] = new ImageView(mycontext);
            ImageTask imageTask = new ImageTask(im[j]);
            imageTask.execute(url);

            linearLayout1.addView(im[j]);
            item1 item1 = new item1(mycontext);
            linearLayout1.addView(item1);
        }

    }





//    public void iszuanfa()
//    {
//        backg=(LinearLayout)findViewById(R.id.setback);
//        backg.setBackgroundColor(R.color.zhuanfa);
//
//    }
}

