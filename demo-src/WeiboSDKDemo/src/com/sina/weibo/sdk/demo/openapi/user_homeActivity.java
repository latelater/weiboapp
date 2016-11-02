/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sina.weibo.sdk.demo.openapi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.demo.AccessTokenKeeper;
import com.sina.weibo.sdk.demo.Constants;
import com.sina.weibo.sdk.demo.R;
import com.sina.weibo.sdk.demo.user_loginin;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.openapi.legacy.ActivityInvokeAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 该类主要演示了如何使用微博 OpenAPI 来获取以下内容：
 * <li>获取用户信息
 * <li>通过个性域名获取用户信息
 * <li>批量获取用户的粉丝数、关注数、微博数
 *
 * @author SINA
 * @since 2014-04-06
 */
public class user_homeActivity extends Activity implements OnItemClickListener {
    private static final String TAG = user_homeActivity.class.getName();


    /** UI 元素：ListView */
    private ListView mFuncListView;
    /** 功能列表 */
    private String[] mFuncList;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;
    /** 用户资料接口 */
    private ActivityInvokeAPI mActivityAPI;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
    /** 注销操作回调 */
    private LogOutRequestListener mLogoutRequestListener = new LogOutRequestListener();
    /** 微博分享接口实例*/
    private IWeiboShareAPI mWeiboShareAPI = null;

    public static  final String ACTION_SHEAR_RESULT = "extend_third_share_result";
    public static final String SHARE_APP_NAME = "shareAppName";
    public static final String PARAM_SHARE_FROM = "share_from";//???????????
    public static final String EXTEND_SHARE_570 = "extend_share_570";//???????????

    public  ShearMessageReceiver  shearMessageReceiver;


    /**
     * @see {@link Activity#onCreate}
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_item);
        //mLogoutButton = (Button) findViewById(R.id.logout);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 获取用户信息接口
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        getUserInfo();

        //底部按钮跳转
        this.findViewById(R.id.home_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_homeActivity.this, mytestActivity.class));
            }
        });
        this.findViewById(R.id.send_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_homeActivity.this, WBStatus_sendAPIActivity.class));
            }
        });
        //列表跳转
        //关注人
        this.findViewById(R.id.fllowers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_homeActivity.this, user_fllowers.class));
            }
        });
        //粉丝
        this.findViewById(R.id.friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_homeActivity.this, user_friends.class));
            }
        });
        //分享
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        mWeiboShareAPI.registerApp();
        shearMessageReceiver = new ShearMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHEAR_RESULT);
        registerReceiver(shearMessageReceiver, filter);
        this.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!mWeiboShareAPI.isWeiboAppInstalled()){
                        Toast.makeText(user_homeActivity.this, "", Toast.LENGTH_LONG).show();
                    }
                    //显示
                    if(true){
                        Bundle  bundle  = new Bundle();
                        Bitmap  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
                        bundle.putString(WBConstants.SDK_WEOYOU_SHARETITLE, "share our app to others");
                        bundle.putString(WBConstants.SDK_WEOYOU_SHAREDESC, "say something...");
                        bundle.putString(WBConstants.SDK_WEOYOU_SHAREURL, "http://github/laterlater.com");
                        bundle.putString("shareBackScheme", "weiboDemo://share");
                        bundle.putString(SHARE_APP_NAME, "app name");
                        bundle.putString(PARAM_SHARE_FROM, EXTEND_SHARE_570);
                        bundle.putByteArray(WBConstants.SDK_WEOYOU_SHAREIMAGE, bitMapToBytes(bitmap));
                        mWeiboShareAPI.shareMessageToWeiyou(user_homeActivity.this, bundle);
                    }else{
                        Toast.makeText(user_homeActivity.this, "微博未安装", Toast.LENGTH_LONG).show();
                    }
            }
        });
        this.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    new LogoutAPI(user_homeActivity.this, Constants.APP_KEY, mAccessToken).logout(mLogoutRequestListener);
                }
            }
        });
    }


    private void getUserInfo() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            long uid = Long.parseLong(mAccessToken.getUid());
            mUsersAPI.show(uid, showUser);
        }
    }

    public byte[] bitMapToBytes(Bitmap  bitmap) {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            return  os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Weibo.ImageObject", "put thumb failed");
        }finally{
            try {
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    class ShearMessageReceiver extends BroadcastReceiver {
        // 这里可以用handler处理
        @Override
        public void onReceive(Context context, Intent intent) {
            //resultCode    分享状态：0成功；1失败；2取消；
            //actionCode    分享完成：0，返回app；1，留在微博
            Bundle bundle  =   intent.getExtras();
            if (bundle != null) {
                final int resultCode = bundle.getInt("resultCode", -1);
                final int actionCode = bundle.getInt("actionCode", -1);
                String resultStr = "";
                String actionStr = "";
                switch (resultCode) {
                    case 0:
                        resultStr ="成功";
                        break;
                    case 1:
                        resultStr ="失败";
                        break;
                    case 2:
                        resultStr ="取消";
                        break;
                    default:
                        break;
                }

                switch (actionCode) {
                    case 0:
                        actionStr ="返回app";
                        break;
                    case 1:
                        actionStr ="留在微博";
                        break;
                    default:
                        break;
                }
                final String resultShowStr = resultStr;
                     Toast.makeText(user_homeActivity.this, "分享结果"+resultShowStr+"操作结果" + actionStr  , Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(user_homeActivity.this, "分享失败", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(shearMessageReceiver);
    }
    /**
     * 注销按钮的监听器，接收注销处理结果。（API请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(user_homeActivity.this);
                        mAccessToken = null;
                        startActivity(new Intent(user_homeActivity.this,user_loginin.class));
                        Toast.makeText(user_homeActivity.this, "Logout Successfully", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }
    //显示用户信息
    private RequestListener showUser = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用User#parse将JSON串解析成User对象
                User user = User.parse(response);

                if (user != null) {
                    ImageView user_name3 = (ImageView) findViewById(R.id.icon_image);
                    Log.i("list",user.profile_image_url);
                    byte[] data = new byte[0];
                    try {
                        data = ImageService.getImage(user.profile_image_url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    user_name3.setImageBitmap(bitmap);
//
                    TextView user_name1 = (TextView) findViewById(R.id.user_name);
                    user_name1.setText(user.screen_name);

                    TextView user_name2 = (TextView) findViewById(R.id.personal_description);
                    user_name2.setText(user.weihao);
//                    Log.i("list", String.valueOf(user.status));
                    TextView user_name4 = (TextView) findViewById(R.id.user_friends_count);
                    user_name4.setText(String.valueOf(user.friends_count));

                    TextView user_name5 = (TextView) findViewById(R.id.user_followers_count);
                    user_name5.setText(String.valueOf(user.followers_count));

                    TextView user_name6 = (TextView) findViewById(R.id.status_conut);
                    user_name6.setText(String.valueOf(user.statuses_count));

                    Log.i("UserStatus", user.status.id);
                    TextView status_text = (TextView) findViewById(R.id.user_status);
                    status_text.setText(user.status.text);
                } else {
                    Log.i("error", response);
                    Toast.makeText(user_homeActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(user_homeActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
