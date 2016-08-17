package com.example.myapplication;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class MyActvity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> mList;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
}
