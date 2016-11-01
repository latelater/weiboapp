package com.sina.weibo.sdk.demo.openapi;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.demo.AccessTokenKeeper;
import com.sina.weibo.sdk.demo.Constants;
import com.sina.weibo.sdk.demo.R;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by sun on 2016/10/27.
 */
public class WBStatus_sendAPIActivity extends Activity {
    private static final String TAG = WBStatusAPIActivity.class.getName();
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI;
    private UsersAPI mUsersAPI;
    private Button msendButton;
    private ImageButton mphotoButton;
    private Bitmap bitmap;
    private ImageButton home_page;
    private ImageButton send_page;
    private ImageButton profile_page;
    private int isphoto=0;
    private static final int IMAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        mUsersAPI = new UsersAPI(this,Constants.APP_KEY,mAccessToken);
        getUserInfo();

        mphotoButton = (ImageButton) findViewById(R.id.add_image);
        msendButton = (Button) findViewById(R.id.send);

        mphotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,IMAGE);
            }
        });
        msendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.editText);
                String edit = et.getText().toString();
                if(isphoto == 0 && edit.equals("")){
                    Toast.makeText(WBStatus_sendAPIActivity.this, "请输入发送内容", Toast.LENGTH_LONG).show();
                }
                else{
                    if (isphoto == 0 ) {
                        mStatusesAPI.update(edit, null, null, mListener);
                    }
                    else if(edit.equals("")){
                        mStatusesAPI.upload("分享图片",bitmap, null, null, mListener);
                    }
                    else{
                        mStatusesAPI.upload(edit, bitmap, null, null, mListener);
                    }
                    startActivity(new Intent(WBStatus_sendAPIActivity.this,mytestActivity.class));
                }
            }
        });
        //底部导航栏跳转
        this.findViewById(R.id.home_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WBStatus_sendAPIActivity.this, mytestActivity.class));
            }
        });
        this.findViewById(R.id.send_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WBStatus_sendAPIActivity.this, mytestActivity.class));
            }
        });
        this.findViewById(R.id.profile_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WBStatus_sendAPIActivity.this, user_homeActivity.class));
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }
    //加载图片
    private void showImage(String imaePath){
        bitmap = BitmapFactory.decodeFile(imaePath);
        ((ImageView)findViewById(R.id.add_image)).setImageBitmap(bitmap);
        if(bitmap != null) {
            isphoto = 1;
            Toast.makeText(WBStatus_sendAPIActivity.this, "添加图片成功", Toast.LENGTH_LONG).show();
        }
    }
    //显示用户名
   private void getUserInfo(){
       if(mAccessToken != null && mAccessToken.isSessionValid()){
           long uid = Long.parseLong(mAccessToken.getUid());
           mUsersAPI.show(uid,showUser);
       }
   }
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener showUser = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if(!TextUtils.isEmpty(response)){
                LogUtil.i(TAG,response);
                User user = User.parse(response);
                if (user != null) {
                    TextView user_name1 = (TextView) findViewById(R.id.user_name);
                    user_name1.setText(user.screen_name);
                }
                else{
                    Toast.makeText(WBStatus_sendAPIActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WBStatus_sendAPIActivity.this, "请检查网络连接", Toast.LENGTH_LONG).show();
        }
    };
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Toast.makeText(WBStatus_sendAPIActivity.this, "发送成功 ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(WBStatus_sendAPIActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WBStatus_sendAPIActivity.this, "不可连续发送！ ", Toast.LENGTH_LONG).show();
        }
    };
}