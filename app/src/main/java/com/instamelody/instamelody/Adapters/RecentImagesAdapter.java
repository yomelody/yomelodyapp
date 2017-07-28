package com.instamelody.instamelody.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;

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

        public MyViewHolder(View mView) {
            super(mView);
            this.galleryImages = (ImageView) mView.findViewById(R.id.galleryImages);

//            galleryImages.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    fileList.get(getAdapterPosition()).
//                }
//            });
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


        Bitmap myBitmap = BitmapFactory.decodeFile(fileList.get(listPosition).getFilepath());
        holder.galleryImages.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 10, myBitmap.getHeight() / 10, false));
        if (myBitmap != null) {
            myBitmap.recycle();
            myBitmap = null;
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}

