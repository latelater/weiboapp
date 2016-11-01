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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 该类主要演示了如何使用微博 OpenAPI 来获取以下内容：
 * <li>获取最新的公共微博
 * <li>获取当前登录用户及其所关注用户的最新微博
 * <li>获取当前登录用户及其所关注用户的最新微博的ID
 * <li>...
 *
 * @author SINA
 * @since 2013-11-24
 */
public class mytestActivity extends Activity {
    private static final String TAG = WBStatusAPIActivity.class.getName();

    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;

    private LinearLayout l;
    /**
     * 用于获取微博信息流等操作的API
     */
    private   test[]te;
    private   test[]ti;
    private StatusesAPI mStatusesAPI;
    private String[] name;
    private String[] text;
    private String[] wid;
    private TextView loginname;
    private UsersAPI mUsersAPI;
    private ImageView loginpicture;
    private Button btu;
    private ImageButton nextbutton;
    private ImageButton  lastbutton;
    private Context  mycontext;
    private  String []picarr;
    private int count=1;

    private  String oneurl;
    private Status originalstatus;

    /**
     * @see {@link Activity#onCreate}
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybasedlayout);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        btu=(Button)findViewById(R.id.refresh) ;
        nextbutton=(ImageButton)findViewById(R.id.next) ;
        lastbutton=(ImageButton)findViewById(R.id.last) ;
        Log.i("开始了吗ＡＰＰ","到底开没开是");

        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);

        long uid = Long.parseLong(mAccessToken.getUid());

        count=0;
        l = (LinearLayout) findViewById(R.id.li);
        loginname = (TextView) findViewById(R.id.loginname);
        loginpicture = (ImageView) findViewById(R.id.loginpicture);
        te =new test[10];
        ti=new test[10];
        name = new String[10];
        text = new String[10];

        mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);

        getUserInfo();
        //上十条下十条加载
        mycontext=this;
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                l.removeAllViews();
                mStatusesAPI.friendsTimeline(0L, 0L, 10 * (count+1), 1, false, 0, false, mListener);
            }
        });
        lastbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count--;
                l.removeAllViews();
                if(count<=0) {
                    count = 0;
                }
                mStatusesAPI.friendsTimeline(0L, 0L, 10 * (count+1), 1, false, 0, false, mListener);
            }
        });
        //刷新
        btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l.removeAllViews();
                mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
            }
        });
        //底部导航栏跳转
        this.findViewById(R.id.send_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mytestActivity.this, WBStatus_sendAPIActivity.class));
            }
        });
        this.findViewById(R.id.profile_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mytestActivity.this, user_homeActivity.class));
            }
        });
//        for(int i = 0;i<10;i++){
//            l.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(mytestActivity.this,WBStatus_aweiboAPIActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("WID", wid[0]);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            });
//        }
    }
    private void getUserInfo() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            long uid = Long.parseLong(mAccessToken.getUid());
            mUsersAPI.show(uid, showUser);
        }
    }

    /**
     * @see {@link OnItemClickListener#onItemClick}
     */


    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Log.i("\"获取微博信息流成功, 条数:", String.valueOf(statuses.statusList.size()));
                        for (int i = count*10; i<10+count*10; i++) {
                            te[i % 10] = new test(mycontext);
                            Log.i("没图：", String.valueOf(i));
                            name[i % 10] = "\n" + statuses.statusList.get(i).user.name;
                            text[i % 10] = statuses.statusList.get(i).text;
                            te[i % 10].setcontent(name[i % 10], text[i % 10]);
                            te[i % 10].getpicture(statuses.statusList.get(i).user.avatar_large);
                            if (statuses.statusList.get(i).pic_urls == null) {
                                l.addView(te[i % 10]);
                            } else {
                                te[i % 10].manypic(statuses.statusList.get(i).pic_urls);
                                l.addView(te[i % 10]);
                            }
                            te[i%10].setBackgroundResource(R.drawable.cg);
                            if (statuses.statusList.get(i).retweeted_status!=null)
                            {
                                Log.i("转发","转发");
                                ti[i % 10] = new test(mycontext);
                                name[i % 10] = "\n" + statuses.statusList.get(i).retweeted_status.user.name;
                                text[i % 10] = statuses.statusList.get(i).retweeted_status.text;
                                ti[i % 10].setcontent(name[i % 10], text[i % 10]);
                                ti[i % 10].getpicture(statuses.statusList.get(i).retweeted_status.user.avatar_large);
                                if (statuses.statusList.get(i).retweeted_status.pic_urls== null) {
                                    Log.i("无图","无图");
                                    l.addView(ti[i % 10]);
                                } else {
                                    // Log.i("配图",statuses.statusList.get(i).pic_urls.get(0));
                                    ti[i % 10].manypic(statuses.statusList.get(i).retweeted_status.pic_urls);
                                    l.addView(ti[i % 10]);
                                }
                                ti[i%10].setBackgroundResource(R.drawable.bg);
                            }
                            item fengge=new item(mycontext);
                            l.addView(fengge);
                        }
                    }

                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(mytestActivity.this, "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mytestActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mytestActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
    private RequestListener showUser = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    byte[] data = new byte[0];
                    try {
                        data = ImageService.getImage(user.avatar_large);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    loginpicture.setImageBitmap(bitmap);
                    loginname.setText(user.screen_name);


                } else {
                    Log.i("error", response);
                    Toast.makeText(mytestActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mytestActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }


    };
}
