package com.photo.photomaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.sketchimage.SketchImage;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.tabs.TabLayout;
import com.photo.photomaker.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    
    private ActivityMainBinding binding;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int STORAGE_PERMISSION_CODE = 777;
    private static final int GALLERY_PICKER_CODE = 888;
    private SketchImage sketchImage,sketchImage1;
    private int MAX_PROGRESS = 100;
    private int effectType = SketchImage.ORIGINAL_TO_GRAY;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private String imageFileName = null;
    private String visibility="GONE";
    private Bitmap imageBitmap;
    private Uri compressedUri;
    private TabLayout tabLayout;
    private Bitmap resultBitmap=null;
    private Dialog dialog;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean checkImageSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
         tabLayout =  binding.tabLayout;

        // Banner Ads Code
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Interstitial Ads Code
        InterstitialAd.load(this,getResources().getString(R.string.interstitial_ad_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_layout));

       setImagesForEachTab();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                binding.pb.setVisibility(View.VISIBLE);
                tab.getCustomView().findViewById(R.id.tabName).setBackground(getDrawable(R.drawable.black_border));
                tab.getCustomView().findViewById(R.id.tabImage).setBackground(getDrawable(R.drawable.black_border_with_white));
                showCustomDialog("Please Wait...");
                effectType = tabLayout.getSelectedTabPosition();
                if (effectType == 0){
                    binding.tvPb.setText(String.format("%d %%", MAX_PROGRESS));
                    binding.simpleSeekBar.setMax(MAX_PROGRESS);
                    binding.simpleSeekBar.setProgress(MAX_PROGRESS);
                    binding.ivTarget.setImageBitmap(imageBitmap);
//                    binding.pb.setVisibility(View.GONE);
                    dialog.dismiss();
                    visibility = "GONE";
                    binding.saveImageIcon.setVisibility(View.GONE);
                }else if (effectType == 1){
                    backgroundTasks(1,true,100);
                }else if (effectType == 2){
                    backgroundTasks(9,true,100);
                }else if (effectType == 3){
                    backgroundTasks(5,true,100);
                }else if (effectType == 4){
                    backgroundTasks(4,true,99);
                }else if (effectType == 5){
                    backgroundTasks(6,true,100);
                }else if (effectType == 6){
                    backgroundTasks(7,true,99);
                }else if (effectType == 7){
                    backgroundTasks(2,true,100);
                }else if (effectType == 8){
                    backgroundTasks(8,true,99);
                }else if (effectType == 9){
                    backgroundTasks(3,true,99);
                }else{
                    backgroundTasks(0,true,100);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                 tab.getCustomView().findViewById(R.id.tabName).setBackground(getDrawable(R.drawable.border1));
                 tab.getCustomView().findViewById(R.id.tabImage).setBackground(getDrawable(R.drawable.border));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.tvPb.setText(String.format("%d %%", seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                binding.pb.setVisibility(View.VISIBLE);
                showCustomDialog("Please wait...");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                binding.pb.setVisibility(View.INVISIBLE);

                if (effectType == 0){
                    binding.ivTarget.setImageBitmap(imageBitmap);
                }else if (effectType == 1){
                    backgroundTasks(1,false,seekBar.getProgress());
                }else if (effectType == 2){
                    backgroundTasks(9,false,seekBar.getProgress());
                }else if (effectType == 3){
                    backgroundTasks(5,false,seekBar.getProgress());
                }else if (effectType == 4){
                    backgroundTasks(4,false,seekBar.getProgress());
                }else if (effectType == 5){
                    backgroundTasks(6,false,seekBar.getProgress());
                }else if (effectType == 6){
                    backgroundTasks(7,false,seekBar.getProgress());
                }else if (effectType == 7){
                    backgroundTasks(2,false,seekBar.getProgress());
                }else if (effectType == 8){
                    backgroundTasks(8,false,seekBar.getProgress());
                }else if (effectType == 9){
                    backgroundTasks(3,false,seekBar.getProgress());
                }else{
                    backgroundTasks(0,false,seekBar.getProgress());
                }
            }
        });

        checkPermission();
        
        binding.chooseImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission()){
                    openGallery();
                }else {
                    checkPermission();
                }
            }
        });

        binding.saveImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToGallery();
            }
        });

    }

    private void saveImageToGallery(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                checkImageSave = SaveImage.saveImageToGallery(MainActivity.this,resultBitmap,"Sketch Image","This is the image created by sketch maker app");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (checkImageSave){
                            Toast.makeText(MainActivity.this, "Image Saved To Gallery", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Image Not Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void backgroundTasks(int type,boolean status,int progress){

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform heavy computations in the background
                if (status){
                      resultBitmap = sketchImage.getImageAs(type, progress);
                }else {
                      resultBitmap = sketchImage.getImageAs(type, progress);

                }


                // Post the result back to the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (status){
                            binding.tvPb.setText(String.format("%d %%", MAX_PROGRESS));
                            binding.simpleSeekBar.setMax(MAX_PROGRESS);
                            binding.simpleSeekBar.setProgress(MAX_PROGRESS);
                            binding.ivTarget.setImageBitmap(resultBitmap);
//                            binding.pb.setVisibility(View.GONE);
                            dialog.dismiss();
                            visibility = "VISIBLE";
                            binding.saveImageIcon.setVisibility(View.VISIBLE);
                        }else {
                            binding.ivTarget.setImageBitmap(resultBitmap);
//                            binding.pb.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    }
                });
            }
        }).start();

    }

    private void setImagesForEachTab() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.download);

                sketchImage1 = new SketchImage.Builder(MainActivity.this, bmOriginal).build();

                Bitmap tabImage2Bitmap = sketchImage1.getImageAs(SketchImage.ORIGINAL_TO_SKETCH, MAX_PROGRESS);
                Bitmap tabImage3Bitmap = sketchImage1.getImageAs(SketchImage.ORIGINAL_TO_COLORED_SKETCH, MAX_PROGRESS);
                Bitmap tabImage4Bitmap = sketchImage1.getImageAs(SketchImage.GRAY_TO_SKETCH, MAX_PROGRESS);
                Bitmap tabImage5Bitmap = sketchImage1.getImageAs(SketchImage.ORIGINAL_TO_SOFT_COLOR_SKETCH, MAX_PROGRESS);
                Bitmap tabImage6Bitmap = sketchImage1.getImageAs(SketchImage.GRAY_TO_COLORED_SKETCH, MAX_PROGRESS);
                Bitmap tabImage7Bitmap = sketchImage1.getImageAs(SketchImage.GRAY_TO_SOFT_SKETCH, MAX_PROGRESS);
                Bitmap tabImage8Bitmap = sketchImage1.getImageAs(SketchImage.SKETCH_TO_COLOR_SKETCH, MAX_PROGRESS);
                Bitmap tabImage9Bitmap = sketchImage1.getImageAs(SketchImage.GRAY_TO_SOFT_COLOR_SKETCH, MAX_PROGRESS);
                Bitmap tabImage10Bitmap = sketchImage1.getImageAs(SketchImage.ORIGINAL_TO_SOFT_SKETCH, MAX_PROGRESS);
                Bitmap tabImage11Bitmap = sketchImage1.getImageAs(SketchImage.ORIGINAL_TO_GRAY, MAX_PROGRESS);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        binding.tvPb.setText(String.format("%d %%", MAX_PROGRESS));
                        binding.simpleSeekBar.setMax(MAX_PROGRESS);
                        binding.simpleSeekBar.setProgress(MAX_PROGRESS);
//                        binding.ivTarget.setImageBitmap(sketchImage1.getImageAs(effectType,
//                                MAX_PROGRESS));

                        ImageView tabImage1 = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName1 = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tabName);
                        tabImage1.setImageResource(R.drawable.download);
                        tabName1.setText(R.string.original);
                        tabImage1.setBackground(getDrawable(R.drawable.black_border_with_white));
                        tabName1.setBackground(getDrawable(R.drawable.black_border));

                        ImageView tabImage2 = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName2 = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tabName);
                        tabImage2.setImageBitmap(tabImage2Bitmap);
                        tabName2.setText(R.string.sketch_1);

                        ImageView tabImage3 = tabLayout.getTabAt(2).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName3 = tabLayout.getTabAt(2).getCustomView().findViewById(R.id.tabName);
                        tabImage3.setImageBitmap(tabImage3Bitmap);
                        tabName3.setText(R.string.sketch_2);

                        ImageView tabImage4 = tabLayout.getTabAt(3).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName4 = tabLayout.getTabAt(3).getCustomView().findViewById(R.id.tabName);
                        tabImage4.setImageBitmap(tabImage4Bitmap);
                        tabName4.setText(R.string.sketch_3);

                        ImageView tabImage5 = tabLayout.getTabAt(4).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName5 = tabLayout.getTabAt(4).getCustomView().findViewById(R.id.tabName);
                        tabImage5.setImageBitmap(tabImage5Bitmap);
                        tabName5.setText(R.string.sketch_4);

                        ImageView tabImage6 = tabLayout.getTabAt(5).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName6 = tabLayout.getTabAt(5).getCustomView().findViewById(R.id.tabName);
                        tabImage6.setImageBitmap(tabImage6Bitmap);
                        tabName6.setText(R.string.sketch_5);

                        ImageView tabImage7 = tabLayout.getTabAt(6).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName7 = tabLayout.getTabAt(6).getCustomView().findViewById(R.id.tabName);
                        tabImage7.setImageBitmap(tabImage7Bitmap);
                        tabName7.setText(R.string.sketch_6);

                        ImageView tabImage8 = tabLayout.getTabAt(7).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName8 = tabLayout.getTabAt(7).getCustomView().findViewById(R.id.tabName);
                        tabImage8.setImageBitmap(tabImage8Bitmap);
                        tabName8.setText(R.string.sketch_7);

                        ImageView tabImage9 = tabLayout.getTabAt(8).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName9 = tabLayout.getTabAt(8).getCustomView().findViewById(R.id.tabName);
                        tabImage9.setImageBitmap(tabImage9Bitmap);
                        tabName9.setText(R.string.sketch_8);

                        ImageView tabImage10 = tabLayout.getTabAt(9).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName10 = tabLayout.getTabAt(9).getCustomView().findViewById(R.id.tabName);
                        tabImage10.setImageBitmap(tabImage10Bitmap);
                        tabName10.setText(R.string.sketch_9);

                        ImageView tabImage11 = tabLayout.getTabAt(10).getCustomView().findViewById(R.id.tabImage);
                        TextView tabName11 = tabLayout.getTabAt(10).getCustomView().findViewById(R.id.tabName);
                        tabImage11.setImageBitmap(tabImage11Bitmap);
                        tabName11.setText(R.string.b_w);
                    }
                });
            }
        }).start();
    }

    private void selectFirstTab() {
        TabLayout.Tab firstTab = tabLayout.getTabAt(0);
        if (firstTab != null) {
            firstTab.select();
        }
    }

    private boolean hasPermission(){
        if (Build.VERSION.SDK_INT <= 32){
            return EasyPermissions.hasPermissions(MainActivity.this,permissions);
        }else {
            return true;
        }

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT <= 32){
            if (!EasyPermissions.hasPermissions(MainActivity.this,permissions)){
                EasyPermissions.requestPermissions(MainActivity.this,"Please Grant Storage Permission",STORAGE_PERMISSION_CODE,permissions);
            }
        }

    }

    private void openGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(Intent.createChooser(pickPhoto,"Select Picture"),GALLERY_PICKER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICKER_CODE && resultCode == RESULT_OK) {
            String timeStamps = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timeStamps + "_";

            Uri uri = data.getData();

            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(uri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            imageBitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //binding.imgLost.setImageBitmap(imageBitmap);
            File compressedFile = new File(getExternalFilesDir(null), imageFileName + ".jpeg");
            try {
                FileOutputStream fos = new FileOutputStream(compressedFile);
                fos.write(outputStream.toByteArray());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            compressedUri = Uri.fromFile(compressedFile);

            binding.ivTarget.setVisibility(View.VISIBLE);
            binding.ivTarget.setImageBitmap(imageBitmap);
            binding.chooseImageLayout.setVisibility(View.GONE);
            binding.fixLayout.setVisibility(View.VISIBLE);
            visibility = "GONE";
            binding.saveImageIcon.setVisibility(View.GONE);
            sketchImage = new SketchImage.Builder(this, imageBitmap).build();
            selectFirstTab();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
          if (EasyPermissions.somePermissionPermanentlyDenied(MainActivity.this,perms)){
              new AppSettingsDialog.Builder(this).build().show();
          }
    }

    private void showCustomDialog(String text) {
        dialog = new Dialog(MainActivity.this, R.style.CustomDialog);
        dialog.setContentView(R.layout.custom_dialog);
        TextView spinnertext = dialog.findViewById(R.id.spinnertext);
        spinnertext.setText(text);
        dialog.show();
        dialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (visibility.equals("GONE")){
            menu.findItem(R.id.action_saveimage).setVisible(false);
            menu.findItem(R.id.action_newimage).setVisible(false);
            menu.findItem(R.id.action_resetimage).setVisible(false);
        }else {
            menu.findItem(R.id.action_saveimage).setVisible(true);
            menu.findItem(R.id.action_newimage).setVisible(true);
            menu.findItem(R.id.action_resetimage).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_saveimage) {
            saveImageToGallery();
            return true;
        } else if (id == R.id.action_newimage) {
            resultBitmap = null;
            if (hasPermission()){
                openGallery();
            }else {
                checkPermission();
            }
            return true;
        } else if (id == R.id.action_resetimage){
            resultBitmap = null;
            binding.ivTarget.setVisibility(View.GONE);
            binding.fixLayout.setVisibility(View.GONE);
            visibility = "GONE";
            binding.chooseImageLayout.setVisibility(View.VISIBLE);
            binding.saveImageIcon.setVisibility(View.GONE);
            return true;
        }else if (id == R.id.action_more){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/developer?id=Paki+Sol")));
            }catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/developer?id=Paki+Sol")));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}