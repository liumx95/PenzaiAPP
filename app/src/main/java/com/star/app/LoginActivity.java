package com.star.app;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.star.app.R.id.et_name;
import static com.star.app.R.id.username;


/**
 * Created by liumx on 2017/4/12.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText et_username;
    private EditText et_password;
    private CheckBox cb_login;
    private CheckBox cb_password;
    Button btn_login;
    Button btn_register;
    ProgressDialog pDialog=null;
    private SharedPreferences sp;
    private SharedPreferences.Editor mEditor;
    public static final String url = "http://liumx95.6655.la:40994/Plant/LoginServlet";
    private static final int RESULT_MSG = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESULT_MSG:
                    if (pDialog!=null){
                        pDialog.dismiss();
                    }
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    handlerLogin(jsonObject);
                    break;

            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏模式
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        et_username = (EditText) findViewById(R.id.username);
        et_password = (EditText) findViewById(R.id.login_password);
        cb_password= (CheckBox) findViewById(R.id.cb_password);
        cb_login= (CheckBox) findViewById(R.id.cb_login);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        SharedPreferences pref = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        cb_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_password.isChecked()) {
                    System.out.println("记住密码已选中");
                    editor.putBoolean("cb_password", true).commit();
                } else {
                    System.out.println("记住密码没有选中");
                     editor.putBoolean("cb_password", false).commit();
                }
            }
        });
        cb_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_login.isChecked()) {
                    System.out.println("自动登录已选中");
                    editor.putBoolean("cb_login", true).commit();
                } else {
                    System.out.println("自动登录没有选中");
                    editor.putBoolean("cb_login", false).commit();
                }
            }
        });
        sp = getSharedPreferences("user",MODE_PRIVATE);
        if(sp.getBoolean("cb_password", false)){
            et_username.setText(sp.getString("username", ""));
            et_password.setText(sp.getString("password", ""));
            cb_password.setChecked(true);
        }
        if(sp.getBoolean("cb_login", false)){
            handleLogin();
            cb_login.setChecked(true);

        }
    }

    /*
         * login_result: -1：登陆失败，未知错误！ 0: 登陆成功！ 1：登陆失败，用户名或密码错误！ 2：登陆失败，用户名不存在！
         */
    private void handlerLogin(JSONObject jsonObject) {
        int resultCode = -1;
        try {
            resultCode = jsonObject.getInt("result_code");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (resultCode) {
            case 0:
                onLoginSuccess(jsonObject);
                break;
            case 1:
                Toast.makeText(this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, "用户名不存在！", Toast.LENGTH_LONG).show();
                break;
            case -1:

            default:
                Toast.makeText(this, "登陆失败！未知错误！", Toast.LENGTH_LONG).show();
                break;
        }

    }

    private void onLoginSuccess(JSONObject jsonObject) {
        Intent intent = new Intent(this, MainActivity.class);
        try {
            intent.putExtra("id",jsonObject.getInt("id"));
            intent.putExtra("petname", jsonObject.getString("petname"));
            intent.putExtra("uname", jsonObject.getString("uname"));
            intent.putExtra("pwd", jsonObject.getString("pwd"));
            intent.putExtra("mail", jsonObject.getString("mail"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                handleLogin();
                break;
            case R.id.btn_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }


    }

    private void handleLogin() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (cb_password.isChecked()) {
            // 记住用户名、密码、
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.commit();
        }
        Log.d("login线程", username+password);
        login(username, password);
    }

    private void login(final String username,final String password) {
        pDialog= ProgressDialog.show(LoginActivity.this,null,"正在登录...",false,true);
        new Thread(new Runnable() {
            public void run() {
                Log.d("login线程", "start");
                HttpClient client = new DefaultHttpClient();
                // 建立HttpPost对象
                HttpPost httpPost = new HttpPost(url);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // 存放参数

                params.add(new BasicNameValuePair("uname",username));
                params.add(new BasicNameValuePair("pwd",password));
                Log.d("login线程", username+password);
                HttpResponse response = null;
                try {
                    // 设置编码
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    Log.d("login线程", String.valueOf(httpPost));
                    response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.e("login线程", "OK");
                        // 获取服务器数据
                        HttpEntity entity = response.getEntity();
                        // 把httpEntity转成String
                        String entityString = EntityUtils.toString(entity);
                        String jsonString = entityString.substring(entityString
                                .indexOf("{"));
                        Log.e("login线程", "entity = " + jsonString);
                        // 转成json
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Log.e("login线程", "jsonObject = " + jsonObject);
                        // 发到handler显示到界面
                        sendMessage(RESULT_MSG, jsonObject);
                    }

                } catch (UnsupportedEncodingException e) {
                    Log.d("login线程", "UnsupportedEncodingException");
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    Log.d("login线程", "ClientProtocolException");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("login线程", "IOException");
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.d("login线程", "JSONException");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendMessage(int what, JSONObject obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;

        handler.sendMessage(msg);
    }
}
