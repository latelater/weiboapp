package com.sina.weibo.sdk.demo.openapi;

import android.app.Activity;
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
public class user_fllowers extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = user_homeActivity.class.getName();

    /**
     * UI Ԫ�أ�ListView
     */
    private ListView mFuncListView;
    /**
     * �����б�
     */
    private String[] mFuncList;
    /**
     * ��ǰ Token ��Ϣ
     */
    private Oauth2AccessToken mAccessToken;

    /** �û���Ϣ�ӿ� */
    private UsersAPI mUsersAPI;
    /**
     * �û�followers�ӿ�
     */
    private FriendshipsAPI mFriendsAPI;

    /**
     * @see {@link Activity#onCreate}
     */
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_followers);

        // ��ȡ��ǰ�ѱ������ Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // ��ȡ�û���Ϣ�ӿ�
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        //��ȡ�û�followers�ӿ�
        mFriendsAPI = new FriendshipsAPI(this, Constants.APP_KEY, mAccessToken);
        getUserInfo();
    }

    private void getUserInfo() {
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            long uid = Long.parseLong(mAccessToken.getUid());
            mFriendsAPI.followers(uid, 50, 0, true, showFllowers);
        }
    }


    private RequestListener showFllowers = new RequestListener(){
        @Override
        public void onComplete(String response) {
            lv = (ListView) findViewById(R.id.lv);/*����һ����̬����*/
            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();/*�������д������*/

            try {
                JSONObject arr = new JSONObject(response);
                JSONArray users = arr.getJSONArray("users");
                Log.d("followerscount", String.valueOf(users.length()));
//                List<User> followers = new ArrayList<>();
//                String str;
                for (int i = 0; i < response.length(); i++){
                    HashMap<String, Object> map = new HashMap<>();
                    User follower = User.parse(users.getJSONObject(i));
                    Log.i("follower+++++++", follower.screen_name);
//                    ImageView user_name3 = (ImageView) findViewById(R.id.icon_image);
//                    Log.i("list",user.profile_image_url);
//                    byte[] data = new byte[0];
//                    try {
//                        data = ImageService.getImage(user.profile_image_url);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    user_name3.setImageBitmap(bitmap);


//                    Log.i("followerlenth", String.valueOf(follower.screen_name.length()));//������name����
//                    Log.i("followertype", follower.screen_name.getClass().toString());//String
//

                    map.put("followers_name", follower.screen_name);
                    map.put("followers_description", follower.location);
//                    map.put("followers_icon", R.drawable.into_icon);
                    listItem.add(map);


                }

            }

            catch (JSONException e) {
                e.printStackTrace();
            }
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(user_fllowers.this,listItem,
                    R.layout.followers,
                    new String[] {"followers_name","followers_description"},
                    new int[] {R.id.followers_name,R.id.followers_description});
            lv.setAdapter(mSimpleAdapter);
//            SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
//            ����context�������ģ�����this������SimpleAdapter���е���ͼ������
//            ����data��Map�б��б�Ҫ��ʾ�����ݣ��ⲿ����Ҫ�Լ�ʵ�֣��������е�getData()������Ҫ�������һ�£�ÿ����ĿҪ��from��ָ����Ŀһ��
//            ����resource��ListView������ļ���Id,������־������Զ���Ĳ����ˣ�������ʾʲô���ӵĲ��ֶ�����������С���������б��������to�ж���Ŀؼ�id
//            ���� from��һ������ӵ�Map�Ϲ���ÿһ����Ŀ�����Ƶ��б�����������������
//            ���� to����һ��int���飬���������id���Զ��岼���и����ؼ���id����Ҫ�������from��Ӧ


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