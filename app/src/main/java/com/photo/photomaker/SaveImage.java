package com.photo.photomaker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

public class SaveImage {

    public static void saveImageWithProgress(Context context, Activity activity, Bitmap bitmap, String title, String description, InterstitialAd mInterstitialAd) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Saving image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                String savedImagePath = saveImageToInternalStorage(context, bitmap, title);
                if (savedImagePath != null) {
                    addToGallery(context, savedImagePath, title, description);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                progressDialog.dismiss();
                if (result) {
                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(activity);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                } else {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String title, String description) {
        String savedImagePath = saveImageToInternalStorage(context, bitmap, title);
        if (savedImagePath != null) {
            // Add the image to the gallery
            addToGallery(context, savedImagePath, title, description);
            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private static void loadInterstitialAds(AdRequest adRequest, AtomicReference<InterstitialAd> mInterstitialAds, Context context) {
        InterstitialAd.load(context, context.getString(R.string.interstitial_ad_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("interstitial Ads", "Ads failed to load");
                        mInterstitialAds.set(null); // Clear the reference
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mInterstitialAds.set(interstitialAd); // Set the loaded ad
                        Log.d("interstitial Ads", "Ads loaded");
                    }
                });
    }


    private static String saveImageToInternalStorage(Context context, Bitmap bitmap, String title) {
        try {
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SketchMaker");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "image_"+ System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        // Use RELATIVE_PATH and IS_PENDING for Android 10 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SketchMaker");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri externalContentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri insertUri = context.getContentResolver().insert(externalContentUri, values);

            if (insertUri != null) {
                try {
                    try (OutputStream stream = context.getContentResolver().openOutputStream(insertUri)) {
                        if (stream != null) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.flush();
                            stream.close();
                        }
                    } finally {
                        values.put(MediaStore.Images.Media.IS_PENDING, 0);
                        context.getContentResolver().update(insertUri, values, null, null);

                        MediaScannerConnection.scanFile(context,
                                new String[]{imagePath}, null,
                                (path, uri) -> {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // For versions prior to Android 10, use this approach
            values.put(MediaStore.Images.Media.DATA, imagePath);
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }


    }
}
