package com.benjamin.mylib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by benja on 2018/9/20.
 */

public class MyBannerAdapter extends MyBanner.Adapter<MyBannerAdapter.MyViewHolder> {

    ArrayList<String> bannerItems;

    public MyBannerAdapter(ArrayList<String> bannerItems) {
        this.bannerItems = bannerItems;
    }

    class MyViewHolder extends MyBanner.ViewHolder{

        private TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.item_banner_text);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        MyViewHolder viewHolder=new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(bannerItems.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerItems.size();
    }
}
