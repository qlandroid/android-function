package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/14.
 */
public class MyFragment extends Fragment{


    public static MyFragment getMyFragment(String name){
        MyFragment myFragment = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = (TextView) view.findViewById(R.id.tv);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name");
            tv.setText(name);
        }

    }
}
