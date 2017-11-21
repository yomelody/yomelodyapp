package com.yomelody.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yomelody.R;

import java.util.ArrayList;

/**
 * Created by Shubhansh Jaiswal on 27/04/17.
 */

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.MyViewHolder> {


    Context context;
    ArrayList<String> fileList = new ArrayList<>();

    public JoinAdapter(ArrayList<String> filesArray, Context context) {
        this.fileList = filesArray;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryImages;

        public MyViewHolder(View mView) {
            super(mView);
            this.galleryImages = (ImageView) mView.findViewById(R.id.galleryImages);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.veiw_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {

//        holder.galleryImages.setImageBitmap(rim.getSelectedImageBitmap());
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}