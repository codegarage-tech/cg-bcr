package com.meembusoft.businesscardreader.compresshelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64Util {

    public static String encodeToBase64(Bitmap bitmap, int quality, Bitmap.CompressFormat format) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static String encodeToBase64(Context context, int drawableResId, int quality, Bitmap.CompressFormat format) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        bitmap.compress(format, quality, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static long getBase64DataLength(Bitmap bitmap, int quality, Bitmap.CompressFormat format) {
        return encodeToBase64(bitmap, quality, format).getBytes().length;
    }

    public static Bitmap decodeFromBase64(String base64String) {
        byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}