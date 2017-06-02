package com.star.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.start;

/**
 * Created by liumx on 2017/4/26.
 */

public class NoteEditActivity extends Activity {
    private EditText et_title;
    private EditText et_word;
    private Button btn_back;
    private Button btn_save;
    private String id,userid, title, word;
    private String addurl = "http://liumx95.6655.la:40994/NoteUtils/NoteAddServlet";
    private String updateurl = "http://liumx95.6655.la:40994/NoteUtils/NoteUpdateServlet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_activity);
        et_title = (EditText) findViewById(R.id.et_title);
        et_word = (EditText) findViewById(R.id.et_word);
        btn_save = (Button) findViewById(R.id.btn_save);
        if (getIntent().getStringExtra("title") == null && getIntent().getStringExtra("word") == null) {

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userid = getIntent().getStringExtra("userid");
                    title = et_title.getText().toString().trim();
                    word = et_word.getText().toString().trim();
                    connAddNote(userid, title, word);
                    Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_LONG).show();
                }
            });

        } else {
            et_title.setText(getIntent().getStringExtra("title"));
            et_word.setText(getIntent().getStringExtra("word"));
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id=getIntent().getStringExtra("id");
                    title = et_title.getText().toString().trim();
                    word = et_word.getText().toString().trim();
                    connUpdateNote(title,word,id);
                    Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void connUpdateNote(final String title, final String word, final String id) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("noteUpdate", "start");
                HttpClient client = new DefaultHttpClient();
                // 建立HttpPost对象
                HttpPost httpPost = new HttpPost(updateurl);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // 存放参数
                params.add(new BasicNameValuePair("title",title));
                params.add(new BasicNameValuePair("word", word));
                params.add(new BasicNameValuePair("id",id));
                HttpResponse response = null;
                try {
                    // 设置编码
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    Log.d("noteUpdate", String.valueOf(httpPost));
                    response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.e("noteUpdate", "OK");

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void connAddNote(final String userid, final String title, final String word) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("noteAdd", "start");
                HttpClient client = new DefaultHttpClient();
                // 建立HttpPost对象
                HttpPost httpPost = new HttpPost(addurl);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // 存放参数
                params.add(new BasicNameValuePair("userid", userid));
                params.add(new BasicNameValuePair("title", title));
                params.add(new BasicNameValuePair("word", word));
                HttpResponse response = null;
                try {
                    // 设置编码
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    Log.d("noteAdd", String.valueOf(httpPost));
                    response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.e("noteAdd", "OK");

                        }
                    } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}