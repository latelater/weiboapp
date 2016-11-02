package com.sina.weibo.sdk.demo.openapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.demo.AccessTokenKeeper;
import com.sina.weibo.sdk.demo.Constants;
import com.sina.weibo.sdk.demo.R;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ҹ������ on 2016/10/28.
 */
public class user_friends extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = user_homeActivity.class.getName();

    /**
     * UI 元素：ListView
     */
    private ListView mFuncListView;
    /**
     * 功能列表
     */
    private String[] mFuncList;
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;

    /** 用户信息接口 */
    private UsersAPI mUsersAPI;
    /**
     * 用户friends接口
     */
    private FriendshipsAPI mFriendsAPI;

    /**
     * @see {@link Activity#onCreate}
     */
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_friends);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 获取用户信息接口
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        //获取用户friends接口
        mFriendsAPI = new FriendshipsAPI(this, Constants.APP_KEY, mAccessToken);
        getUserInfo();
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_friends.this, user_homeActivity.class));
            }
        });
    }

    private void getUserInfo() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            long uid = Long.parseLong(mAccessToken.getUid());
            mFriendsAPI.friends(uid, 50, 0, true, showFriends);
        }
    }


    private RequestListener showFriends = new RequestListener(){
        @Override
        public void onComplete(String response) {
            lv = (ListView) findViewById(R.id.lv);/*定义一个动态数组*/
            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();/*在数组中存放数据*/

            try {
                JSONObject arr = new JSONObject(response);
                JSONArray users = arr.getJSONArray("users");
                Log.d("friendsscount", String.valueOf(users.length()));
//                List<User> followers = new ArrayList<>();
//                String str;
                for (int i = 0; i < response.length(); i++){
                    HashMap<String, Object> map = new HashMap<>();
                    User friends = User.parse(users.getJSONObject(i));
                    Log.i("follower+++++++", friends.screen_name);

                    map.put("followers_name", friends.screen_name);
                    map.put("followers_description", friends.location);
//                    map.put("followers_icon", R.drawable.into_icon);
                    listItem.add(map);
                }
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(user_friends.this,listItem,
                    R.layout.followers,
                    new String[] {"followers_name","followers_description"},
                    new int[] {R.id.followers_name,R.id.followers_description});
            lv.setAdapter(mSimpleAdapter);
//            SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
//            参数context：上下文，比如this。关联SimpleAdapter运行的视图上下文
//            参数data：Map列表，列表要显示的数据，这部分需要自己实现，如例子中的getData()，类型要与上面的一致，每条项目要与from中指定条目一致
//            参数resource：ListView单项布局文件的Id,这个布局就是你自定义的布局了，你想显示什么样子的布局都在这个布局中。这个布局中必须包括了to中定义的控件id
//            参数 from：一个被添加到Map上关联每一个项目列名称的列表，数组里面是列名称
//            参数 to：是一个int数组，数组里面的id是自定义布局中各个控件的id，需要与上面的from对应
        }


        @Override
        public void onWeiboException(WeiboException e) {

        }
    };

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}