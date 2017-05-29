package com.instamelody.instamelody.Adapters;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.instamelody.instamelody.Models.RecentImagesModel;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Shubhansh Jaiswal on 23/01/17.
 */

public class RecentImagesAdapter extends RecyclerView.Adapter<RecentImagesAdapter.MyViewHolder> {


    RecentImagesModel rim = new RecentImagesModel();
    //  private ArrayList<RecentImagesModel> imagesList;
    ArrayList<String> fileList = new ArrayList<>();
    Context context;

    public RecentImagesAdapter(ArrayList<String> filesArray, Context context) {
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

    // create constructor to initialize context and data sent from MainActivity
//    public RecentImagesAdapter(ArrayList<RecentImagesModel> data) {
//        this.imagesList = data;
//    }

    // Inflate the layout when ViewHolder created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.veiw_image, parent, false);
        return new MyViewHolder(view);
    }

    // Bind data
//    @TargetApi(16)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {

        holder.galleryImages.setImageBitmap(rim.getSelectedImageBitmap());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

//        RecentImagesModel rim = imagesList.get(listPosition);
////      holder.galleryImages.setBackgroundDrawable(rim.getSelectedImageBitmap()); // Deprecated
//        holder.galleryImages.setBackground(rim.getSelectedImageBitmap());
////        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
////        holder.galleryImages.setImageBitmap(rim.getSelectedImageBitmap());
//    }

/*
* public void bindPhoto(Photo photo) {
    mPhoto = photo;
    mPhotoFile=PhotoLab.get(getActivity()).getPhotoFile(mPhoto);
    Uri uri=Uri.fromFile(mPhotoFile);

    if(mPhotoFile==null || !mPhotoFile.exists()){
        int imgdrawable=R.drawable.ic_action_name3;
        mThumbnail.setImageDrawable(getResources().getDrawable(imgdrawable));
    } else {
        Picasso.with(getActivity()).load(uri).fit().into(mThumbnail);
    }


    mTitleTextView.setText(mPhoto.getTitle());
    String dateFormat = "EEE, MMM dd";
    dateString = DateFormat.format(dateFormat, mPhoto.getDate()).toString();
    mDateTextView.setText(dateString);
}
* */

//    @Override
//    public int getItemCount() {
//        return imagesList.size();
//    }


}

