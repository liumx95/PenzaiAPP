package com.star.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liumx on 2017/4/14.
 */

public class RegisterActivity extends Activity implements  View.OnClickListener{
    private Button back_login;
    private Button  register;
    private EditText et_nickname;
    private EditText et_name;
    private EditText et_pwd;
    private EditText et_pwd_again;
    private EditText et_mail;
    public static  final  String url="http://liumx95.6655.la:40994/Plant/RegServlet";
    public static  final  int MSG_RESULT=1;
    ProgressDialog pDialog=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_RESULT:
                    if (pDialog!=null){
                        pDialog.dismiss();
                    }
                    JSONObject json= (JSONObject) msg.obj;
                    hanleRegister(json);

            }


            super.handleMessage(msg);

        }
    };
    //校验邮箱格式
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    /**
     *
     * 0：注册成功 1:用户名已存在 2：注册失败
     * @param json
     */

    private void hanleRegister(JSONObject json) {
        int result;
        try {
            result=json.getInt("result_code");
        } catch (JSONException e) {
            Toast.makeText(this,"没有获得网络的响应！",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        if (result==1){
            Toast.makeText(this,"用户名已存在！",Toast.LENGTH_LONG).show();
            return;
        }
        if (result==2){
            Toast.makeText(this,"注册失败！服务端出现异常",Toast.LENGTH_LONG).show();
            return;
        }
        if (result==0){
            Toast.makeText(this, "注册成功！请前往登陆界面！", Toast.LENGTH_LONG).show();
            return;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        back_login= (Button) findViewById(R.id.back_login);
        register= (Button) findViewById(R.id.register);
        et_nickname= (EditText) findViewById(R.id.et_nickname);
        et_name= (EditText) findViewById(R.id.et_name);
        et_pwd= (EditText) findViewById(R.id.et_pwd);
        et_pwd_again= (EditText) findViewById(R.id.et_pwd_again);
        et_mail= (EditText) findViewById(R.id.et_mail);
        back_login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_login:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.register:
                String pwd=et_pwd.getText().toString();
                String pwd_again=et_pwd_again.getText().toString();
                if (isEmail(et_mail.getText().toString())) {
                    if (pwd.equals(pwd_again)){
                            handleSubmit();
                    }else{
                        Toast.makeText(getApplicationContext(),"两次输入密码不一致",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"输入邮箱格式有误",Toast.LENGTH_LONG).show();
                }
                break;
        }


    }

    private void handleSubmit() {
          Log.e("test","开始");
        pDialog=ProgressDialog.show(RegisterActivity.this,null,"正在注册...",false,true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("test","线程开始");
                HttpClient client=new DefaultHttpClient();
                //建立HttpPost对象
                HttpPost httpPost=new HttpPost(url);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params= new ArrayList<NameValuePair>();
                //存放参数
                params.add(new BasicNameValuePair("petname", et_nickname
                        .getText().toString()));
                params.add(new BasicNameValuePair("uname", et_name
                        .getText().toString()));
                params.add(new BasicNameValuePair("pwd", et_pwd.getText()
                        .toString()));

                params.add(new BasicNameValuePair("mail", et_mail.getText()
                        .toString()));
                //设置编码
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    HttpResponse httpResponse = client.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode()==200){
                        Log.e("test","请求成功");
                        //获取服务器数据
                        HttpEntity entity=httpResponse.getEntity();
                        //把httpEntity转出string
                        String entityString=EntityUtils.toString(entity);
                        String jsonString=entityString.substring(entityString.indexOf("{"));
                        Log.e("test","json字符串为"+jsonString);
                        //转成json
                        JSONObject jsonObject=new JSONObject(jsonString);
                        //发送handler显示到界面
                        sendMessage(MSG_RESULT, jsonObject);

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    private void sendMessage(int what,JSONObject obj) {
        Message msg=Message.obtain();
        msg.what=what;
        msg.obj=obj;
        handler.sendMessage(msg);
    }
}
