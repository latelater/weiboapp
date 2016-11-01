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

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.demo.openapi.mytestActivity;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * ������Ҫ��ʾ��ν�����Ȩ��SSO��½��
 *
 * @author SINA
 * @since 2013-09-29
 */
public class user_loginin extends Activity {

    private static final String TAG = "weibosdk";

    /** ��ʾ��֤�����Ϣ���� AccessToken */
    private TextView mTokenText;

    private AuthInfo mAuthInfo;

    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ�����  */
    private Oauth2AccessToken mAccessToken;

    /** ע�⣺SsoHandler ���� SDK ֧�� SSO ʱ��Ч */
    private SsoHandler mSsoHandler;

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginin);

        // ��ȡ Token View��������ʾ View �����ݿɹ�����С��Ļ������ʾ��ȫ��
        mTokenText = (TextView) findViewById(R.id.token_text_view);
        TextView hintView = (TextView) findViewById(R.id.obtain_token_hint);
        hintView.setMovementMethod(new ScrollingMovementMethod());

        // ����΢��ʵ��
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // ������Ȩʱ���벻Ҫ���� SCOPE��������ܻ���Ȩ���ɹ�
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(user_loginin.this, mAuthInfo);

        // SSO ��Ȩ, ���ͻ���
        findViewById(R.id.obtain_token_via_sso).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorizeClientSso(new AuthListener());
            }
        });

        // �ֻ�������Ȩ 
        //  title ����ע��ҳ��title����ѡ������ʱĬ��Ϊ""��֤���¼""���˴�WeiboAuthListener ���� listener 
        //�����Ǻ�sso ͬһ�� listener   �ص����� Ҳ�����ǲ�ͬ�ġ������߸�����Ҫѡ��
        findViewById(R.id.obtain_token_via_mobile).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.registerOrLoginByMobile("��֤���¼",new AuthListener());
            }
        });

        // SSO ��Ȩ, ��Web
        findViewById(R.id.obtain_token_via_web).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorizeWeb(new AuthListener());
            }
        });

        // SSO ��Ȩ, ALL IN ONE   ����ֻ���װ��΢���ͻ�����ʹ�ÿͻ�����Ȩ,û���������ҳ��Ȩ
        findViewById(R.id.obtain_token_via_signature).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new AuthListener());
            }
        });

        // �û��ǳ�
        findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessTokenKeeper.clear(getApplicationContext());
                mAccessToken = new Oauth2AccessToken();
                updateTokenView(false);
            }
        });

        // ͨ�� Code ��ȡ Token
        findViewById(R.id.obtain_token_via_code).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_loginin.this, WBAuthCodeActivity.class));
            }
        });

        // �� SharedPreferences �ж�ȡ�ϴ��ѱ���� AccessToken ����Ϣ��
        // ��һ��������Ӧ�ã�AccessToken ������
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
        }
    }

    /**
     * �� SSO ��Ȩ Activity �˳�ʱ���ú��������á�
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO ��Ȩ�ص�
        // ��Ҫ������ SSO ��½�� Activity ������д onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    /**
     * ΢����֤��Ȩ�ص��ࡣ
     * 1. SSO ��Ȩʱ����Ҫ�� {@link #onActivityResult} �е��� {@link SsoHandler#authorizeCallBack} ��
     *    �ûص��Żᱻִ�С�
     * 2. �� SSO ��Ȩʱ������Ȩ�����󣬸ûص��ͻᱻִ�С�
     * ����Ȩ�ɹ����뱣��� access_token��expires_in��uid ����Ϣ�� SharedPreferences �С�
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // �� Bundle �н��� Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //�������ȡ�û������ �绰������Ϣ 
            String  phoneNum =  mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // ��ʾ Token
                updateTokenView(false);
                // ���� Token �� SharedPreferences
                AccessTokenKeeper.writeAccessToken(user_loginin.this, mAccessToken);
                Toast.makeText(user_loginin.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(user_loginin.this, mytestActivity.class));

            } else {
                // ���¼�������������յ� Code��
                // 1. ����δ��ƽ̨��ע���Ӧ�ó���İ�����ǩ��ʱ��
                // 2. ����ע���Ӧ�ó��������ǩ������ȷʱ��
                // 3. ������ƽ̨��ע��İ�����ǩ��������ǰ���Ե�Ӧ�õİ�����ǩ����ƥ��ʱ��
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(user_loginin.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(user_loginin.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(user_loginin.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ��ʾ��ǰ Token ��Ϣ��
     *
     * @param hasExisted �����ļ����Ƿ��Ѵ��� token ��Ϣ���ҺϷ�
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));

        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        mTokenText.setText(message);
    }
}