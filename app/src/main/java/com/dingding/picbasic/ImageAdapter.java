package com.dingding.picbasic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv;
        public TextView tv;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            iv =  itemLayoutView.findViewById(R.id.iv);
            tv = itemLayoutView.findViewById(R.id.tv);
        }
    }
    private ArrayList<MyImage> itemsDatas;

    public ImageAdapter(ArrayList<MyImage> itemsDatas) {
        this.itemsDatas = itemsDatas;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
//        Glide.with(this).load(fs[0].getUri()).fitCenter().into(iv_f);

        viewHolder.tv.setText(""  + itemsDatas.get(position).getName());
        viewHolder.iv.setImageURI(itemsDatas.get(position).getUri());
    }

    @Override
    public int getItemCount() {
        return itemsDatas.size();
    }
}
