package com.example.myapplication.biaotilan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.myapplication.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TitleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.list_view)
    ListView listView;
    /**
     * 1. View.SYSTEM_UI_FLAG_VISIBLE：显示状态栏，Activity不全屏显示(恢复到有状态的正常情况)。
     * <p/>
     * 2. View.INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示。
     * <p/>
     * 3. View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。
     * <p/>
     * 4. View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
     * <p/>
     * 5. View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     * <p/>
     * 6. View.SYSTEM_UI_LAYOUT_FLAGS：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     * <p/>
     * 7. View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
     * <p/>
     * 8. View.SYSTEM_UI_FLAG_LOW_PROFILE：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。
     */
    String[] itemList = {"View.SYSTEM_UI_FLAG_VISIBLE",
            "View.INVISIBLE",
            "View.SYSTEM_UI_FLAG_FULLSCREEN",
            "View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN",
            "View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION",
            "View.SYSTEM_UI_LAYOUT_FLAGS",
            "View.SYSTEM_UI_FLAG_HIDE_NAVIGATION",
            "View.SYSTEM_UI_FLAG_LOW_PROFILE"};
    int[] itemView ={View.SYSTEM_UI_FLAG_VISIBLE,
            View.INVISIBLE,
            View.SYSTEM_UI_FLAG_FULLSCREEN,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
            View.SYSTEM_UI_LAYOUT_FLAGS,
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_LOW_PROFILE};
    @Bind(R.id.layout)
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        ButterKnife.bind(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, itemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        layout.setSystemUiVisibility(itemView[position]);
    }
}
