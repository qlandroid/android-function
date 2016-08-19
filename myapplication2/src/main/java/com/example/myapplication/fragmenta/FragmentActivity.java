package com.example.myapplication.fragmenta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.BaseActvity;
import com.example.myapplication.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentActivity extends BaseActvity implements AdapterView.OnItemClickListener {


    @Bind(R.id.list_view)
    ListView listView;

    private String[] items = {"把Fragment添加到回退栈"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ButterKnife.bind(this);
        initListView();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0://带回退栈
                break;
        }
    }

    private void initListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


}
