package com.photo.photomaker;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageSaver {

    public static void saveImageToGallery(Context context, Bitmap bitmap, String title, String description) {
        String savedImagePath = saveImageToInternalStorage(context, bitmap, title);
        if (savedImagePath != null) {
            // Add the image to the gallery
            addToGallery(context, savedImagePath, title, description);
            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private static String saveImageToInternalStorage(Context context, Bitmap bitmap, String title) {
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "SketchMaker");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "image_"+ System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addToGallery(Context context, String imagePath, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DATA, imagePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
