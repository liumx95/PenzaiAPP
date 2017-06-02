package com.star.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.star.app.R;

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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by liumx on 2017/4/17.
 */

public class DataFragment extends Fragment {

    public EditText et_fname;
    public TextView tv_fname;
    public TextView tv_fword;
    public ImageView iv_fimage;
    public Button btn_flower;
    public String fname;
    public SharedPreferences pref;
    public SharedPreferences sp;

    public static  String urlimage=null;
    public static final String url = "http://liumx95.6655.la:40994/Plant/FlowerServlet";
    private static final int RESULT_MSG = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what== RESULT_MSG) {

                JSONObject jsonObject = (JSONObject) msg.obj;
                try {
                    ShowMessage(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (msg.what==1){
                    Bitmap bitmap= (Bitmap) msg.obj;
                    iv_fimage.setImageBitmap(bitmap);
            }
        };
    };

    private void ShowMessage(JSONObject jsonObject) throws Exception {
        tv_fname.setText(jsonObject.getString("fname"));
       tv_fword.setText(jsonObject.getString("fword"));
        urlimage= (String) jsonObject.get("fimage");
        returnBitMap(urlimage);
        Log.e("Flower",jsonObject.getString("fimage"));
        pref=getActivity().getApplicationContext().getSharedPreferences("flower", MODE_PRIVATE);
        pref.edit().putString("fname",jsonObject.getString("fname")).commit();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_layout, null);


        if (isFristRun()){
            if (fname==null) {
                fname = "文竹";
                connectData(fname);
            }else {
                Log.e("fname",sp.getString("fname",""));
                connectData(fname);
            }

        }else {
            sp = getActivity().getApplication().getSharedPreferences("flower",MODE_PRIVATE);
            fname=sp.getString("fname","");
            connectData(fname);
        }


        et_fname= (EditText) view.findViewById(R.id.et_fname);
        tv_fname= (TextView) view.findViewById(R.id.tv_fname);
        tv_fword= (TextView) view.findViewById(R.id.tv_fword);
        iv_fimage= (ImageView) view.findViewById(R.id.iv_fimage);
        btn_flower= (Button) view.findViewById(R.id.btn_flower);
        btn_flower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname=et_fname.getText().toString().trim();
                if (et_fname.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity(),"请输入您要查询的花名", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        connectData(fname);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return  view;
    }
    private boolean isFristRun() {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "share", MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!isFirstRun) {
            return false;
        } else {
            //保存数据 （第三步）
            editor.putBoolean("isFirstRun", false);
            //提交当前数据 （第四步）
            editor.commit();
            return true;
        }
    }
    public  void returnBitMap(final String url) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myFileUrl=new URL(url);
                    HttpURLConnection conn= (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is=conn.getInputStream();
                    Bitmap bitmap=BitmapFactory.decodeStream(is);
                    Message msg1=new Message();
                    msg1.what=1;
                    msg1.obj=bitmap;
                    handler.sendMessage(msg1);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void connectData(final String fname) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("Flower线程", "start");
                HttpClient client = new DefaultHttpClient();
                // 建立HttpPost对象
                HttpPost httpPost = new HttpPost(url);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // 存放参数
                params.add(new BasicNameValuePair("fname",fname));
                HttpResponse response = null;
                try {
                    // 设置编码
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    Log.d("Flower线程", String.valueOf(httpPost));
                    response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.e("Flower线程", "OK");
                        // 获取服务器数据
                        HttpEntity entity = response.getEntity();
                       // 把httpEntity转成byte[]
                        String entityString = EntityUtils.toString(entity);
                        String jsonString = entityString.substring(entityString.indexOf("{"));
                        Log.e("Flower线程", "entity = " + jsonString);
                        // 转成json
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Log.e("Flower线程", "jsonObject = " + jsonObject);
                        // 发到handler显示到界面
                        sendMessage(RESULT_MSG, jsonObject);

                    }

                } catch (UnsupportedEncodingException e) {
                    Log.d("Flower线程", "UnsupportedEncodingException");
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    Log.d("Flower线程", "ClientProtocolException");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("Flower线程", "IOException");
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;

        handler.sendMessage(msg);
    }


}
