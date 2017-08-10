package com.instamelody.instamelody.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.instamelody.instamelody.Models.RecentImagesModel;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.instamelody.instamelody.R.attr.failureImage;
import static com.instamelody.instamelody.R.attr.placeholderImage;

/**
 * Created by Shubhansh Jaiswal on 23/01/17.
 */

public class RecentImagesAdapter extends RecyclerView.Adapter<RecentImagesAdapter.MyViewHolder> {

    ArrayList<RecentImagesModel> fileList = new ArrayList<>();
    Context context;

    public RecentImagesAdapter(ArrayList<RecentImagesModel> filesArray, Context context) {
        this.fileList = filesArray;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryImages;
        SimpleDraweeView mSimpleDraweeView;

        public MyViewHolder(View mView) {
            super(mView);
            this.galleryImages = (ImageView) mView.findViewById(R.id.galleryImages);
            this.mSimpleDraweeView = (SimpleDraweeView) mView.findViewById(R.id.sdvGalleryImages);

            galleryImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileList.get(getAdapterPosition()).getFilepath();
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.veiw_image, parent, false);
        return new MyViewHolder(view);
    }

    @TargetApi(19)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {
//        Picasso.with(context).load(new File(fileList.get(listPosition).getFilepath())).into(holder.galleryImages);
        holder.mSimpleDraweeView.getHierarchy().setPlaceholderImage(placeholderImage);
        holder.mSimpleDraweeView.setImageURI(fileList.get(listPosition).getFilepath());
        holder.mSimpleDraweeView.getHierarchy().setFailureImage(failureImage);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}

