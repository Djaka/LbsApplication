package com.mobileapp.lbsapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.orm.SugarApp;

public class GeneralFunction extends SugarApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Bitmap compressbitmap(Bitmap realImage, boolean filter) {
        final int maxSize = 1024;
        int width;
        int height;
        int inWidth = realImage.getWidth();
        int inHeight = realImage.getHeight();
        if (inWidth > inHeight) {
            width = maxSize;
            height = (inHeight * maxSize) / inWidth;
        } else {
            height = maxSize;
            width = (inWidth * maxSize) / inHeight;
        }
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }
}
