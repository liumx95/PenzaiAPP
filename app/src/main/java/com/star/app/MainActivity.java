package com.star.app;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mzule.fantasyslide.SideBar;
import com.github.mzule.fantasyslide.SimpleFantasyListener;
import com.github.mzule.fantasyslide.Transformer;
import com.star.fragment.DataFragment;
import com.star.fragment.MessageFragment;
import com.star.fragment.NoteFragment;
import com.star.fragment.RelationFragment;

import static com.star.app.R.id.turn_left;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_petname;
    private TextView uname;
    private TextView pwd;
    private TextView update;
    private TextView mail;
    private TextView exit;
    private LinearLayout content;
    private DrawerLayout drawerLayout;
    private ImageView message,data,relation,note,turn_left,touxing;
    private  TextView tv_title;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ImageView location;
    private int TAG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        tv_petname.setText(getIntent().getStringExtra("petname"));
        final DrawerArrowDrawable indicator = new DrawerArrowDrawable(this);
        indicator.setColor(Color.GREEN);
        setTransformer();
        setListener();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (((ViewGroup) drawerView).getChildAt(1).getId() == R.id.leftSideBar) {
                    indicator.setProgress(slideOffset);
                }
            }
        });
    }

    private void initView() {
        tv_petname= (TextView) findViewById(R.id.tv_petname);
        uname= (TextView) findViewById(R.id.uname);
        pwd= (TextView) findViewById(R.id.pwd);
//        update= (TextView) findViewById(R.id.update);
        mail= (TextView) findViewById(R.id.mail);
        exit= (TextView) findViewById(R.id.exit);
        content = (LinearLayout) findViewById(R.id.content);
        message = (ImageView) findViewById(R.id.message);
        data = (ImageView) findViewById(R.id.data);
        relation = (ImageView) findViewById(R.id.relation);
        note= (ImageView) findViewById(R.id.note);
        tv_title = (TextView) findViewById(R.id.tv_title);
        touxing= (ImageView) findViewById(R.id.touxiang);
        turn_left = (ImageView) findViewById(R.id.turn_left);
        location = (ImageView) findViewById(R.id.location);
        message.setOnClickListener(this);
        data.setOnClickListener(this);
        relation.setOnClickListener(this);
        note.setOnClickListener(this);
        location.setOnClickListener(this);
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.content, new DataFragment());
        ft.commit();
    }

    private void setListener() {
        final TextView tipView = (TextView) findViewById(R.id.tipView);
        SideBar leftSideBar = (SideBar) findViewById(R.id.leftSideBar);
        leftSideBar.setFantasyListener(new SimpleFantasyListener() {
            @Override
            public boolean onHover(@Nullable View view, int index) {
                tipView.setVisibility(View.VISIBLE);
                if (view != null && view.getId() == R.id.uname) {
                    tipView.setText(getIntent().getStringExtra("uname"));
                }else if(view != null && view.getId() == R.id.pwd){
                    tipView.setText(getIntent().getStringExtra("pwd"));
//                }else if (view != null && view.getId() == R.id.update){
//                    tipView.setText("修改");
                }else if (view != null && view.getId() == R.id.mail){
                    tipView.setText(getIntent().getStringExtra("mail"));
                }else if (view != null && view.getId() == R.id.exit){
                    tipView.setText("退出");
                }else if (view != null && view.getId() == R.id.userInfo) {
                    tipView.setText(getIntent().getStringExtra("petname"));
                } else {
                    tipView.setText(null);
                }
                return false;

            }

            @Override
            public boolean onSelect(View view, int index) {
                tipView.setVisibility(View.INVISIBLE);
                if (view != null && view.getId() == R.id.uname) {
                    tipView.setText(getIntent().getStringExtra("uname"));
                  Toast.makeText(getApplicationContext(),getIntent().getStringExtra("uname"),Toast.LENGTH_LONG).show();
                }else if(view != null && view.getId() == R.id.pwd){
                    Toast.makeText(getApplicationContext(),getIntent().getStringExtra("pwd"),Toast.LENGTH_LONG).show();
//                }else if (view != null && view.getId() == R.id.update){
//                    Toast.makeText(getApplicationContext(),"修改",Toast.LENGTH_LONG).show();
                }else if (view != null && view.getId() == R.id.mail){
                    Toast.makeText(getApplicationContext(),getIntent().getStringExtra("mail"),Toast.LENGTH_LONG).show();
                }else if (view != null && view.getId() == R.id.exit){
                    Toast.makeText(getApplicationContext(),"已退出登录",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }else if (view != null && view.getId() == R.id.userInfo) {
                    Toast.makeText(getApplicationContext(),getIntent().getStringExtra("petname"),Toast.LENGTH_LONG).show();
                } else {
                    tipView.setText(null);
                }
                return false;
            }

            @Override
            public void onCancel() {
                tipView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setTransformer() {
        final float spacing = getResources().getDimensionPixelSize(R.dimen.spacing);
        SideBar rightSideBar = (SideBar) findViewById(R.id.rightSideBar);
        rightSideBar.setTransformer(new Transformer() {
            private View lastHoverView;

            @Override
            public void apply(ViewGroup sideBar, View itemView, float touchY, float slideOffset, boolean isLeft) {
                boolean hovered = itemView.isPressed();
                if (hovered && lastHoverView != itemView) {
                    animateIn(itemView);
                    animateOut(lastHoverView);
                    lastHoverView = itemView;
                }
            }

            private void animateOut(View view) {
                if (view == null) {
                    return;
                }
                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", -spacing, 0);
                translationX.setDuration(200);
                translationX.start();
            }

            private void animateIn(View view) {
                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", 0, -spacing);
                translationX.setDuration(200);
                translationX.start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.touxiang) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    public void onClick(View view) {
        if (view instanceof TextView) {
            String title = ((TextView) view).getText().toString();
            if (title.startsWith("星期")) {
                Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            }
       }

        ft = fm.beginTransaction();
        switch (view.getId()) {
            case R.id.data:
                reSet();
                tv_title.setText("精选");
                data.setImageResource(R.drawable.data_press);
                ft.replace(R.id.content, new DataFragment());
                TAG = 1;
                break;
            case R.id.relation:
                reSet();
                tv_title.setText("花语");
                relation.setImageResource(R.drawable.relation_press);
                ft.replace(R.id.content, new RelationFragment());
                TAG = 2;
                break;
            case R.id.message:
                reSet();
                tv_title.setText("消息");
                message.setImageResource(R.drawable.message_press);
                ft.replace(R.id.content, new MessageFragment());
                TAG = 3;
                break;
            case R.id.note:
                reSet();
                tv_title.setText("笔记");
                note.setImageResource(R.drawable.service_press);
                ft.replace(R.id.content, new NoteFragment());
                TAG = 4;
                break;
            case R.id.location:
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                break;
        }
        ft.commit();

    }

    private void reSet() {
        if (TAG == 1) {
            data.setImageResource(R.drawable.data);
        } else if (TAG == 2) {
            relation.setImageResource(R.drawable.relation);
        } else if (TAG == 3) {
            message.setImageResource(R.drawable.message);
        } else if (TAG == 4) {
            note.setImageResource(R.drawable.service);
        }
    }
}
