package com.star.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.star.app.NoteEditActivity;

import com.star.app.R;
import com.star.tools.DialogUtils;
import com.star.tools.ToastUtils;

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
import java.util.Objects;

/**
 * Created by liumx on 2017/4/17.
 */

public class NoteFragment extends Fragment implements AdapterView.OnItemLongClickListener{
    private List<Map<String,Object>> mListnote=new ArrayList<Map<String, Object>>();
    private MyAdapter myAdapter=new MyAdapter();
    private ProgressDialog pDialog=null;
    private ListView notes_list;
    private Button btn_new_note;
    public String userid,noteid;
    public String url="http://liumx95.6655.la:40994/NoteUtils/NoteShowServlet";
    public String deleteurl="http://liumx95.6655.la:40994/NoteUtils/NoteDeleteServlet";
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (pDialog!=null){
                pDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                   notes_list.setAdapter(myAdapter);
                    break;
                case 2:
                    ToastUtils.maktText(getActivity(),"连接服务器失败");
                    break;
            }

        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_layout, null);
        userid= String.valueOf((getActivity().getIntent().getIntExtra("id",0)));
        notes_list= (ListView) view.findViewById(R.id.notes_list);
        btn_new_note= (Button) view.findViewById(R.id.btn_new_note);
        notes_list.setOnItemLongClickListener(this);
        notes_list.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.note_list_footer,null));
        notes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),NoteEditActivity.class);

                intent.putExtra("id",mListnote.get(position).get("id").toString());
                intent.putExtra("title",mListnote.get(position).get("title").toString());
                intent.putExtra("word",mListnote.get(position).get("word").toString());
                startActivity(intent);
            }
        });

        btn_new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),NoteEditActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);

            }
        });
       return  view;
    }
    private void connectData(final String userid) {
        new Thread(new Runnable() {
            public void run() {
                Message msg = handler.obtainMessage();
                Log.d("note线程", "start");
                HttpClient client = new DefaultHttpClient();
                // 建立HttpPost对象
                HttpPost httpPost = new HttpPost(url);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // 存放参数
                Log.e("note线程",userid);
                params.add(new BasicNameValuePair("userid",userid));
                HttpResponse response = null;
                try {
                    // 设置编码
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    Log.d("note线程", String.valueOf(httpPost));
                    response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.e("note线程", "OK");
                        // 获取服务器数据
                        HttpEntity entity = response.getEntity();
                        // 把httpEntity转成byte[]
                        String jsonString = EntityUtils.toString(entity);
//                        String jsonString = entityString.substring(entityString.indexOf("{"));
                        Log.e("note线程", "jsonString = " + jsonString);
                      // 转成json
                       JSONArray jsonArray=new JSONArray(jsonString);
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            Map<String,Object> row=new HashMap<String, Object>();
                            row.put("id",jsonObject.getString("id"));
                            row.put("userid",jsonObject.getString("userid"));
                            row.put("title",jsonObject.getString("title"));
                            row.put("word",jsonObject.getString("word"));
                            Log.e("note线程", "jsonObject="+jsonObject);
                            mListnote.add(row);
                            msg.what=1;
                        }
                    }else {
                        msg.what=2;
                    }
                    handler.sendMessage(msg);

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

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        noteid=mListnote.get(position).get("id").toString();
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setView(R.layout.dialog)
                .setTitle("删除笔记")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                connDeleteNote(noteid);
                                onResume();
                                Toast.makeText(getActivity().getApplicationContext(),"已删除",Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogUtils.setClosable(dialog,
                                true);

                    }
                }).show();
        return true;
    }

    private void connDeleteNote(final String noteid) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("noteDelete", "start");
                HttpClient client = new DefaultHttpClient();
                // 建立HttpPost对象
                HttpPost httpPost = new HttpPost(deleteurl);
                // 定义了一个list，该list的数据类型是NameValuePair（简单名称值对节点类型）
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // 存放参数
                params.add(new BasicNameValuePair("id", noteid));
                HttpResponse response = null;
                try {
                    // 设置编码
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //发送post,并返回一个httpResponse对象
                    Log.d("noteDelete", String.valueOf(httpPost));
                    response = client.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.e("noteDelete", "OK");

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

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mListnote.size();
        }

        @Override
        public Object getItem(int position) {
            return mListnote.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getActivity(),R.layout.note_item,null);
            TextView tv_title= (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText(mListnote.get(position).get("title").toString());
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListnote.clear();
        pDialog = ProgressDialog.show(getActivity(),null,"正在加载",false,true);
        new Thread() {
            @Override
            public void run() {
              connectData(userid);
            }
        }.start();
        myAdapter.notifyDataSetChanged();
        notes_list.setAdapter(myAdapter);
        //访问服务器，跟新数据

        Toast.makeText(getActivity(),"刷新",Toast.LENGTH_SHORT).show();
    }
}
