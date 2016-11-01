package com.sina.weibo.sdk.demo.openapi;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import org.w3c.dom.Text;

/**
 * Created by sun on 2016/10/28.
 */
public class WBStatus_aweiboAPIActivity extends Activity {
    private static final String TAG = WBStatusAPIActivity.class.getName();
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI;
 //   private UsersAPI mUsersAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aweibo);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI,userAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
   //   mUsersAPI = new UsersAPI(this,Constants.APP_KEY,mAccessToken);

        Bundle bundle = this.getIntent().getExtras();
        String wid = bundle.getString("WID");
   //   TextView id = (TextView) findViewById(R.id.user_name)
   //   id.setText(String.valueOf(wid));
        Log.i("微博的id",wid);
        if(mAccessToken != null && mAccessToken.isSessionValid()){
            mStatusesAPI.show(wid,showText);
         }
    }
    //4036671941050057
    //4036002043905839


    private RequestListener showText = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if(!TextUtils.isEmpty(response)){

                Log.i("打印：",response);

                LogUtil.i(TAG,response);
                if(response.startsWith("{\"statuses\"")) {
                    // 调用 Status#parse 解析字符串成微博列表对象
                    Status statuses = Status.parse(response);
                    TextView aw = (TextView)findViewById(R.id.weibotext);
                    Log.i("打印：",response);
                    Log.i("用户",statuses.text);
                    aw.setText(statuses.text);
                    if (statuses != null) {
                    //    String text = statuses.statusList.
                    } else {
                        Toast.makeText(WBStatus_aweiboAPIActivity.this, "请检查网络连接", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        //    LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WBStatus_aweiboAPIActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
