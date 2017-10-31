package com.instamelody.instamelody.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
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

public class DiscoverAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;
    Activity activity;
    Context context;
    ArrayList<AdvertisePagingData> pagingDataArrayList = new ArrayList<>();
    public DiscoverAdapter(ArrayList<AdvertisePagingData> pagingDataArrayList, Context context) {
        this.pagingDataArrayList = pagingDataArrayList;
        this.context = context;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_advertisement_image, container, false);
        ImageView ivAdvertiseImage=(ImageView)view.findViewById(R.id.ivAdvertiseImage);
        Picasso.with(ivAdvertiseImage.getContext()).load(pagingDataArrayList.get(position).getAdv_image()).into(ivAdvertiseImage);
        ivAdvertiseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(pagingDataArrayList.get(position).getAdv_url()));
                context.startActivity(intent);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return pagingDataArrayList.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
