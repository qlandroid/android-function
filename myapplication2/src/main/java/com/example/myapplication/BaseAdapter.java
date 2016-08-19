package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.OnClick;

/**
 * recyclerView的适配器
 */
public  abstract class BaseAdapter<HV extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<HV>implements View.OnClickListener,View.OnLongClickListener{


    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener{
        boolean onItemLongClick(View v,int position);
    }
    public static interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }



    @Override
    public void onBindViewHolder(HV holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        onBindViewHolder(holder,position);
    }
    public abstract void onBindToViewHolder(HV holder,int position);



    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v,(Integer)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener!=null){
            return mOnItemLongClickListener.onItemLongClick(v,(Integer)v.getTag());
        }
        return false;
    }
}
