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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.demo.AccessTokenKeeper;
import com.sina.weibo.sdk.demo.Constants;
import com.sina.weibo.sdk.demo.R;
import com.sina.weibo.sdk.demo.sharesdk;
import com.sina.weibo.sdk.demo.user_loginin;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.openapi.legacy.ActivityInvokeAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 锟斤拷锟斤拷锟斤拷要锟斤拷示锟斤拷锟斤拷锟绞癸拷锟轿拷锟? OpenAPI 锟斤拷锟斤拷取锟斤拷锟斤拷锟斤拷锟捷ｏ拷
 * <li>锟斤拷取锟矫伙拷锟斤拷息
 * <li>通锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷取锟矫伙拷锟斤拷息 
 * <li>锟斤拷锟斤拷锟斤拷取锟矫伙拷锟侥凤拷丝锟斤拷锟斤拷锟斤拷注锟斤拷锟斤拷微锟斤拷锟斤拷 
 *
 * @author SINA
 * @since 2014-04-06
 */
public class user_homeActivity extends Activity implements OnItemClickListener {
    private static final String TAG = user_homeActivity.class.getName();


    /** UI 元锟截ｏ拷ListView */
    private ListView mFuncListView;
    /** 锟斤拷锟斤拷锟叫憋拷 */
    private String[] mFuncList;
    /** 锟矫伙拷锟斤拷息锟接匡拷 */
    private UsersAPI mUsersAPI;
    /** 锟矫伙拷锟斤拷锟较接匡拷 */
    private ActivityInvokeAPI mActivityAPI;
    /** 锟斤拷锟节伙拷取微锟斤拷锟斤拷息锟斤拷锟饺诧拷锟斤拷锟斤拷API */
    private StatusesAPI mStatusesAPI;
    /** 娉ㄩ攢鎸夐挳 */
    private Button mLogoutButton;
    /** 褰撳墠 Token 淇℃伅 */
    private Oauth2AccessToken mAccessToken;
    /** 娉ㄩ攢鎿嶄綔鍥炶皟 */
    private LogOutRequestListener mLogoutRequestListener = new LogOutRequestListener();
    /** ????? */
    private Button          mSharedBtn;
    /** ??????????????? */
    private IWeiboShareAPI mWeiboShareAPI = null;
    /** ????????? **/
    private WeiboAppManager.WeiboInfo mWeiboInfo;

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
        mLogoutButton = (Button) findViewById(R.id.logout);

        // 锟斤拷取锟斤拷前锟窖憋拷锟斤拷锟斤拷锟? Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 锟斤拷取锟矫伙拷锟斤拷息锟接匡拷
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        getUserInfo();

        //搴曢儴瀵艰埅鏍忚烦杞?
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
        //鍒楄〃璺宠浆
        this.findViewById(R.id.fllowers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_homeActivity.this, user_fllowers.class));
            }
        });
        this.findViewById(R.id.friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_homeActivity.this, user_friends.class));
            }
        });
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
                    //    Toast.makeText(this, "", Toast.LENGTH_LONG).show();
                    }
                    //???
                    if(true){
                        Bundle  bundle  = new Bundle();
                        Bitmap  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
                        bundle.putString(WBConstants.SDK_WEOYOU_SHARETITLE, "share our app to others");
                        bundle.putString(WBConstants.SDK_WEOYOU_SHAREDESC, "say something...");
//                bundle.putString(WBConstants.SDK_WEOYOU_SHAREURL, "http://i2.api.weibo.com/2/short_url/info.json?source=2796559090&url_short=http://t.cn/RUEbk59");
//                bundle.putString(WBConstants.SDK_WEOYOU_SHAREURL, "http://weibo.com/p/1001603910171175841314");
                        bundle.putString(WBConstants.SDK_WEOYOU_SHAREURL, "http://github/laterlater.com");
                        bundle.putString("shareBackScheme", "weiboDemo://share");
//                bundle.putString(WBConstants.SDK_WEOYOU_SHARECARDID, "1008085766e60d6825fa86c6923a91bcff6f85");
                        bundle.putString(SHARE_APP_NAME, "app name");
                        bundle.putString(PARAM_SHARE_FROM, EXTEND_SHARE_570);
                        bundle.putByteArray(WBConstants.SDK_WEOYOU_SHAREIMAGE, bitMapToBytes(bitmap));
                        mWeiboShareAPI.shareMessageToWeiyou(user_homeActivity.this, bundle);
                    }else{
                    //    Toast.makeText(this, "???δ???", Toast.LENGTH_LONG).show();
                    }
            }
        });
        this.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(mAccessToken.getExpiresTime()));
                    String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    new LogoutAPI(user_homeActivity.this, Constants.APP_KEY, mAccessToken).logout(mLogoutRequestListener);
                } else {
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

        // ?????????handler  ????
        @Override
        public void onReceive(Context context, Intent intent) {
            //resultCode    ????????0?????1????2?????
            //actionCode    ????????0??????app??1?????????
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                final int resultCode = bundle.getInt("resultCode", -1);
                final int actionCode = bundle.getInt("actionCode", -1);
                String resultStr = "";
                String actionStr = "";
                switch (resultCode) {
                    case 0:
                        resultStr = "???";
                        break;
                    case 1:
                        resultStr = "???";
                        break;
                    case 2:
                        resultStr = "???";
                        break;
                    default:
                        break;
                }

                switch (actionCode) {
                    case 0:
                        actionStr = "????app";
                        break;
                    case 1:
                        actionStr = "???????";
                        break;
                    default:
                        break;
                }


                final String resultShowStr = resultStr;

                //    Toast.makeText(WBShareToMessageFriendActivity.this, "??????   "+resultShowStr+"  ???????    " + actionStr  , Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(user_homeActivity.this, "???????", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(shearMessageReceiver);
    }
    /**
     * 娉ㄩ攢鎸夐挳鐨勭洃鍚櫒锛屾帴鏀舵敞閿?澶勭悊缁撴灉銆傦紙API璇锋眰缁撴灉鐨勭洃鍚櫒锛?
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
    //鏄剧ず鐢ㄦ埛淇℃伅
    private RequestListener showUser = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 锟斤拷锟斤拷 User#parse 锟斤拷JSON锟斤拷锟斤拷锟斤拷锟斤拷User锟斤拷锟斤拷
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
