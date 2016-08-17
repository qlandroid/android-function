package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.biaotilan.TitleActivity;
import com.example.myapplication.bitmap.BitmapActivity;
import com.example.myapplication.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    @Bind(R.id.list_view)
    ListView listView;
    private List<String> itemList = new ArrayList<>();
    private List<Class> classList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addItem("webView的使用",WebViewActivity.class);
        addItem("设置状态栏状态", TitleActivity.class);
        addItem("照片的压缩", BitmapActivity.class);


        initView();

    }

    private void initView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1,itemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void addItem(String item,Class clazz){
        itemList.add(item);
        classList.add(clazz);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,classList.get(position));
        startActivity(intent);
    }



}
