package com.star.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.star.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.R.id.list;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by liumx on 2017/4/17.
 */

public class MessageFragment extends Fragment {

    private MessageReceiver mMessageReceiver;
    public TextView tuisong;
    public TextView tv_message;
    public SharedPreferences pref;
    public SharedPreferences sp;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static boolean isForeground = false;
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout, null);
        registerMessageReceiver();
        tv_message= (TextView) view.findViewById(R.id.tv_message);
        tv_message.getPaint().setFakeBoldText(true);
        tuisong= (TextView) view.findViewById(R.id.tv_tuisong);
        tuisong.setText((new Date().getMonth()+1)+"."+new Date().getDate()+"特别推送");
        tuisong.getPaint().setFakeBoldText(true);
        sp = view.getContext().getSharedPreferences("message",MODE_PRIVATE);
        String today_message=sp.getString("message","");
        tv_message.setText(today_message);
        return  view;
    }

    @Override
    public void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    public void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    public void onDestroy() {
        this.getActivity().unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        this.getActivity().registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//接收推送的自定义消息Message
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String message = intent.getStringExtra(KEY_MESSAGE);
                    pref=context.getSharedPreferences("message", MODE_PRIVATE);
                    pref.edit().putString("message", message).commit();
                Log.e("test",message);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + message + "\n");
                if (!TextUtils.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }

            }
        }
    }
}




