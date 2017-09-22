package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.Models.AdvertisePagingData;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by CBPC 41 on 9/13/2017.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    ArrayList<AdvertisePagingData> pagingDataArrayList = new ArrayList<>();
    Context context;

    public DiscoverAdapter(ArrayList<AdvertisePagingData> pagingDataArrayList, Context context) {
        this.pagingDataArrayList = pagingDataArrayList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAdvertiseImage;
        TextView tvAdvertiseLink;

        public MyViewHolder(View mView) {
            super(mView);
            this.ivAdvertiseImage = (ImageView) mView.findViewById(R.id.ivAdvertiseImage);
            this.tvAdvertiseLink = (TextView) mView.findViewById(R.id.tvAdvertiseLink);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_advertisement_image, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AdvertisePagingData advertisePagingData = pagingDataArrayList.get(position);
        holder.tvAdvertiseLink.setText(advertisePagingData.getAdv_name());
        Picasso.with(holder.ivAdvertiseImage.getContext()).load(advertisePagingData.getAdv_image()).into(holder.ivAdvertiseImage);
        holder.ivAdvertiseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(advertisePagingData.getAdv_url()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pagingDataArrayList.size();
    }
}
