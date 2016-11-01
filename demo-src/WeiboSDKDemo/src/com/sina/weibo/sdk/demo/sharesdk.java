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

package com.sina.weibo.sdk.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboAppManager.WeiboInfo;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.demo.openapi.user_homeActivity;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * ???????????????????????????????  ????   ???   ????????
 * ???????? ??????->???->?????
 *
 * @author SINA
 * @since 2015-11-16
 */
public class sharesdk extends Activity implements OnClickListener{

    /** ????? */
    private Button          mSharedBtn;
    /** ??????????????? */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    /** ????????? **/
    private WeiboInfo mWeiboInfo;

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
//       mWeiboInfo = WeiboAppManager.getInstance(this).getWeiboInfo();

        // ????????????????
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        mWeiboShareAPI.registerApp();

        shearMessageReceiver = new ShearMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHEAR_RESULT);
        registerReceiver(shearMessageReceiver, filter);


        this.findViewById(R.id.share).setOnClickListener(this);//????????id

    }

    @Override
    protected void onNewIntent( Intent intent ) {
        super.onNewIntent(intent);
    }

    /**
     * ???????????????????????????з???
     */
    @Override
    public void onClick(View v) {

        if (R.id.share == v.getId()) {
//            if( isWeiboAppInstalled()){
            if(!mWeiboShareAPI.isWeiboAppInstalled()){
                Toast.makeText(this, "???δ???", Toast.LENGTH_LONG).show();
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
                mWeiboShareAPI.shareMessageToWeiyou(sharesdk.this, bundle);
            }else{
                Toast.makeText(this, "???δ???", Toast.LENGTH_LONG).show();
            }
        }
    }



    public boolean isWeiboAppInstalled() {
        return (mWeiboInfo != null && mWeiboInfo.isLegal()) ? true : false;
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

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
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
            Bundle bundle  =   intent.getExtras();

            if(bundle!=null){
                final  int  resultCode = bundle.getInt("resultCode",-1);
                final  int  actionCode = bundle.getInt("actionCode",-1);
                String  resultStr = "";
                String  actionStr = "";
                switch (resultCode) {
                    case 0:
                        resultStr ="???";
                        break;
                    case 1:
                        resultStr ="???";
                        break;
                    case 2:
                        resultStr ="???";
                        break;
                    default:
                        break;
                }

                switch (actionCode) {
                    case 0:
                        actionStr ="????app";
                        break;
                    case 1:
                        actionStr ="???????";
                        break;
                    default:
                        break;
                }


                final String resultShowStr =resultStr ;

                //    Toast.makeText(WBShareToMessageFriendActivity.this, "??????   "+resultShowStr+"  ???????    " + actionStr  , Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(sharesdk.this, "???????", Toast.LENGTH_LONG).show();
            }


//         final String  result =   intent.getStringExtra("resultCode");
//            if(ACTION_SHEAR_RESULT.equals(intent.getAction())){
//                  runOnUiThread(new Runnable() {
//                    public void run() {
//                         Toast.makeText(WBShareToMessageFriendActivity.this, "????????????  result"+result, Toast.LENGTH_LONG).show();
//                    }
//                });
//                
//            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(shearMessageReceiver);
    }



}
