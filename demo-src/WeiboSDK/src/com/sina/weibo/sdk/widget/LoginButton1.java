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

package com.sina.weibo.sdk.widget;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sina.weibo.sdk.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * �����ṩ��һ���򵥵ĵ�¼/ע���ؼ���
 * �ÿؼ��ṩ���õĵ�¼��SSO ��½��Ȩ����ע�����ܣ�����������ʽ����ɫ����ɫ����
 * ע�⣺ʹ���߿��������޸� /res/values/styles.xml �ļ��е���ʽ��
 *
 * @author SINA
 * @since 2013-11-04
 */
public class LoginButton1 extends Button implements OnClickListener {
    private static final String TAG = "LoginButton";

    /** ΢����Ȩʱ������ SSO ����� Activity */
    private Context mContext;
    /** ��Ȩ��֤����Ҫ����Ϣ */
    private AuthInfo mAuthInfo;
    /** SSO ��Ȩ��֤ʵ�� */
    private SsoHandler mSsoHandler;
    /** ΢����Ȩ��֤�ص� */
    private WeiboAuthListener mAuthListener;
    /** Access Token ʵ��  */
    private Oauth2AccessToken mAccessToken;
    /** ע���ص� */
    private RequestListener mLogoutListener;
    /** ��� Button ʱ������� Listener */
    private OnClickListener mExternalOnClickListener;

    /**
     * ����һ����¼/ע����ť��
     *
     * @see View#View(Context)
     */
    public LoginButton1(Context context) {
        this(context, null);
    }

    /**
     * �� XML �����ļ��д���һ����¼/ע����ť��
     *
     * @see View#View(Context, AttributeSet)
     */
    public LoginButton1(Context context, AttributeSet attrs) {
        this(context, attrs, /*R.style.com_sina_weibo_sdk_loginview_default_style*/0);
    }

    /**
     * �� XML �����ļ��Լ���ʽ�д���һ����¼/ע����ť��
     *
     * @see View#View(Context, AttributeSet, int)
     */
    public LoginButton1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    /**
     * ����΢����Ȩ������Ϣ�Լ��ص�������
     *
     * @param authInfo     ���ڱ�����Ȩ��֤����Ҫ����Ϣ
     * @param authListener ΢����Ȩ��֤�ص��ӿ�
     */
    public void setWeiboAuthInfo(AuthInfo authInfo, WeiboAuthListener authListener) {
        mAuthInfo = authInfo;
        mAuthListener = authListener;
    }

    /**
     * ����΢����Ȩ������Ϣ��
     *
     * @param appKey       ������Ӧ�õ� APP_KEY
     * @param redirectUrl  ������Ӧ�õĻص�ҳ
     * @param scope        ������Ӧ�������Ȩ��
     * @param authListener ΢����Ȩ��֤�ص��ӿ�
     */
    public void setWeiboAuthInfo(String appKey, String redirectUrl, String scope, WeiboAuthListener authListener) {
        mAuthInfo = new AuthInfo(mContext, appKey, redirectUrl, scope);
        mAuthListener = authListener;
    }

    /**
     * ����ע��ʱ����Ҫ���õ� Token ��Ϣ�Լ�ע����Ļص��ӿڡ�
     *
     * @param accessToken    AccessToken ��Ϣ
     * @param logoutListener ע���ص�
     */
    public void setLogoutInfo(Oauth2AccessToken accessToken, RequestListener logoutListener) {
        mAccessToken = accessToken;
        mLogoutListener = logoutListener;

        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            setText(R.string.com_sina_weibo_sdk_logout);
        }
    }

    /**
     * ����ע���ص���
     *
     * @param logoutListener ע���ص�
     */
    public void setLogoutListener(RequestListener logoutListener) {
        mLogoutListener = logoutListener;
    }

    /**
     * ����һ������� Button ���ʱ�� Listener��
     * ������ Button ����¼�ʱ�����ȵ��ø� Listener����ʹ����һ���ɷ��ʵĻ��ᣬ
     * Ȼ���ٵ����ڲ�Ĭ�ϵĴ���
     * <p><b>ע�⣺һ������£�ʹ���߲���Ҫ���ø÷�����������������Ҫ�ԡ�<b></p>
     *
     *
     */
    public void setExternalOnClickListener(OnClickListener l) {
        mExternalOnClickListener = l;
    }

    /**
     * ʹ�øÿؼ�������Ȩ��½ʱ����Ҫ�ֶ����øú�����
     * <p>
     * ��Ҫ��ʹ�øÿؼ��� Activity ������д {@link Activity#onActivityResult(int, int, Intent)}��
     *       �����ڲ����øú����������޷���Ȩ�ɹ���</p>
     * <p>Sample Code��</p>
     * <pre class="prettyprint">
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *     super.onActivityResult(requestCode, resultCode, data);
     *
     *     // �ڴ˴�����
     *     mLoginoutButton.onActivityResult(requestCode, resultCode, data);
     * }
     * </pre>
     * @param requestCode ��鿴 {@link Activity#onActivityResult(int, int, Intent)}
     * @param resultCode  ��鿴 {@link Activity#onActivityResult(int, int, Intent)}
     * @param data        ��鿴 {@link Activity#onActivityResult(int, int, Intent)}
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * ��ť�����ʱ�����øú�����
     */
    @Override
    public void onClick(View v) {
        // Give a chance to external listener
        if (mExternalOnClickListener != null) {
            mExternalOnClickListener.onClick(v);
        }

        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            logout();
        } else {
            login();
        }
    }

    /**
     * ��ʼ��������
     *
     * @param context �����Ļ�����һ��Ϊ���ø� Button �� Activity
     * @param attrs   XML ���Լ��϶���
     */
    private void initialize(Context context, AttributeSet attrs) {
        mContext = context;
        this.setOnClickListener(this);

        // ��������ļ���δ���� style������Ĭ�ϵ� style
        loadDefaultStyle(attrs);
    }

    /**
     * ����Ĭ�ϵ���ʽ����ɫ����
     *
     * @param attrs XML ���Լ��϶���
     */
    private void loadDefaultStyle(AttributeSet attrs) {
        if (attrs != null && 0 == attrs.getStyleAttribute()) {
            Resources res = getResources();
            this.setBackgroundResource(R.drawable.com_sina_weibo_sdk_button_blue);
            this.setPadding(res.getDimensionPixelSize(R.dimen.com_sina_weibo_sdk_loginview_padding_left),
                    res.getDimensionPixelSize(R.dimen.com_sina_weibo_sdk_loginview_padding_top),
                    res.getDimensionPixelSize(R.dimen.com_sina_weibo_sdk_loginview_padding_right),
                    res.getDimensionPixelSize(R.dimen.com_sina_weibo_sdk_loginview_padding_bottom));
            this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_com_sina_weibo_sdk_logo, 0, 0, 0);
            this.setCompoundDrawablePadding(
                    res.getDimensionPixelSize(R.dimen.com_sina_weibo_sdk_loginview_compound_drawable_padding));
            this.setTextColor(res.getColor(R.color.com_sina_weibo_sdk_loginview_text_color));
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimension(R.dimen.com_sina_weibo_sdk_loginview_text_size));
            this.setTypeface(Typeface.DEFAULT_BOLD);
            this.setGravity(Gravity.CENTER);
            this.setText(R.string.com_sina_weibo_sdk_login_withweb);
        }
    }

    /**
     * ���� SSO ��½�����δ��װ΢���ͻ��ˣ��� SSO ��֤ʧ�ܣ������ת�������� Web ��Ȩ��
     */
    private void login() {
        LogUtil.i(TAG, "Click to login");

        if (null == mSsoHandler && mAuthInfo != null) {
            mSsoHandler = new SsoHandler((Activity)mContext, mAuthInfo);
        }

        if (mSsoHandler != null) {
            mSsoHandler.authorize(new WeiboAuthListener() {

                @Override
                public void onComplete(Bundle values) {
                    // �� Bundle �н��� Token
                    mAccessToken = Oauth2AccessToken.parseAccessToken(values);
                    if (mAccessToken.isSessionValid()) {
                        setText(R.string.com_sina_weibo_sdk_logout);
                    }

                    if (mAuthListener != null) {
                        mAuthListener.onComplete(values);
                    }
                }

                @Override
                public void onCancel() {
                    if (mAuthListener != null) {
                        mAuthListener.onCancel();
                    }
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    if (mAuthListener != null) {
                        mAuthListener.onWeiboException(e);
                    }
                }
            });
        } else {
            LogUtil.e(TAG, "Please setWeiboAuthInfo(...) for first");
        }
    }

    /**
     * ���� {@link LogoutAPI#logout(RequestListener)} ��ע����
     */
    private void logout() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            LogUtil.i(TAG, "Click to logout");

            new LogoutAPI(mContext, mAuthInfo.getAppKey(), mAccessToken).logout(new RequestListener() {
                @Override
                public void onComplete(String response) {
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.isNull("error")){
                                String value = obj.getString("result");

                                // ע���ɹ�
                                if ("true".equalsIgnoreCase(value)) {
                                    // XXX: �����Ƿ���Ҫ�� AccessTokenKeeper �ŵ� SDK �У���
                                    //AccessTokenKeeper.clear(getContext());
                                    // ��յ�ǰ Token
                                    mAccessToken = null;

                                    setText(R.string.com_sina_weibo_sdk_login_withweb);
                                }
                            } else {
                                String error_code = obj.getString("error_code");
                                if(error_code.equals("21317")){
                                    mAccessToken = null;
                                    setText(R.string.com_sina_weibo_sdk_login_withweb);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mLogoutListener != null) {
                        mLogoutListener.onComplete(response);
                    }
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    LogUtil.e(TAG, "WeiboException�� " + e.getMessage());
                    // ע��ʧ��
                    setText(R.string.com_sina_weibo_sdk_logout);
                    if (mLogoutListener != null) {
                        mLogoutListener.onWeiboException(e);
                    }
                }
            });
        }
    }
}
