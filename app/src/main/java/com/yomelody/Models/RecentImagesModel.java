package com.yomelody.Models;

import android.graphics.Bitmap;

/**
 * Created by Shubhansh Jaiswal on 25/01/17.
 */

public class RecentImagesModel {
    String name, filepath;
    Bitmap bitmap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}