package com.instamelody.instamelody.Models;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

/**
 * Created by Shubhansh Jaiswal on 25/01/17.
 */

public class RecentImagesModel {

    int id_;
    Bitmap selectedImageBitmap;

    public int getId_() {
        return id_;
    }

    public void setId_(int id_) {
        this.id_ = id_;
    }

    public Bitmap getSelectedImageBitmap() {
        return selectedImageBitmap;
    }

    public void setSelectedImageBitmap(Bitmap selectedImageBitmap) {
        this.selectedImageBitmap = selectedImageBitmap;
    }


    //    String filePath;
//    String  filePathColumn;
//    Cursor cursor;
//    Uri selectedImage;


//    public Bitmap getSelectedImageBitmap() {
//        return selectedImageBitmap;
//    }
//
//    public void setSelectedImageBitmap(Bitmap selectedImageBitmap) {
//        this.selectedImageBitmap = selectedImageBitmap;
//    }

//    public int getColumnIndex() {
//        return columnIndex;
//    }
//
//    public void setColumnIndex(int columnIndex) {
//        this.columnIndex = columnIndex;
//    }
//
//    public Uri getSelectedImage() {
//        return selectedImage;
//    }
//
//    public void setSelectedImage(Uri selectedImage) {
//        this.selectedImage = selectedImage;
//    }
//
//    public String getFilePath() {
//        return filePath;
//    }
//
//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }
//
//    public Cursor getCursor() {
//        return cursor;
//    }
//
//    public void setCursor(Cursor cursor) {
//        this.cursor = cursor;
//    }
}