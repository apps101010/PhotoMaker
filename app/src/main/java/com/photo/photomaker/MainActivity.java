package com.photo.photomaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Type;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.sketchimage.SketchImage;
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
    private SketchImage sketchImage;
    private int MAX_PROGRESS = 100;
    private int effectType = SketchImage.ORIGINAL_TO_GRAY;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private String imageFileName = null;
    private Bitmap imageBitmap;
    private Uri compressedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.download);

        binding.ivTarget.setImageBitmap(bmOriginal);

        sketchImage = new SketchImage.Builder(this, bmOriginal).build();

        binding.tvPb.setText(String.format("%d %%", MAX_PROGRESS));
        binding.simpleSeekBar.setMax(MAX_PROGRESS);
        binding.simpleSeekBar.setProgress(MAX_PROGRESS);
        binding.ivTarget.setImageBitmap(sketchImage.getImageAs(effectType,
                MAX_PROGRESS));

        final TabLayout tabLayout =  binding.tabLayout;
//        tabLayout.addTab(tabLayout.newTab().setText("Original to Gray"));
//        tabLayout.addTab(tabLayout.newTab().setText("Original to Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Original to Colored Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Original to Soft Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Original to Soft Color Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Gray to Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Gray to Colored Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Gray to Soft Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Gray to Soft Color Sketch"));
//        tabLayout.addTab(tabLayout.newTab().setText("Sketch to Color Sketch"));

        // Add tabs with custom views
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
        // Add more tabs as needed

        // Set images for each tab
        ImageView tabImage1 = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tabImage);
        tabImage1.setImageResource(R.drawable.download);

        ImageView tabImage2 = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tabImage);
        tabImage2.setImageBitmap(sketchImage.getImageAs(SketchImage.ORIGINAL_TO_GRAY,
                MAX_PROGRESS));
        ImageView tabImage3 = tabLayout.getTabAt(2).getCustomView().findViewById(R.id.tabImage);
        tabImage3.setImageBitmap(sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage4 = tabLayout.getTabAt(3).getCustomView().findViewById(R.id.tabImage);
        tabImage4.setImageBitmap(sketchImage.getImageAs(SketchImage.ORIGINAL_TO_COLORED_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage5 = tabLayout.getTabAt(4).getCustomView().findViewById(R.id.tabImage);
        tabImage5.setImageBitmap(sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SOFT_COLOR_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage6 = tabLayout.getTabAt(5).getCustomView().findViewById(R.id.tabImage);
        tabImage6.setImageBitmap(sketchImage.getImageAs(SketchImage.GRAY_TO_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage7 = tabLayout.getTabAt(6).getCustomView().findViewById(R.id.tabImage);
        tabImage7.setImageBitmap(sketchImage.getImageAs(SketchImage.GRAY_TO_COLORED_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage8 = tabLayout.getTabAt(7).getCustomView().findViewById(R.id.tabImage);
        tabImage8.setImageBitmap(sketchImage.getImageAs(SketchImage.GRAY_TO_SOFT_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage9 = tabLayout.getTabAt(8).getCustomView().findViewById(R.id.tabImage);
        tabImage9.setImageBitmap(sketchImage.getImageAs(SketchImage.GRAY_TO_SOFT_COLOR_SKETCH,
                MAX_PROGRESS));
        ImageView tabImage10 = tabLayout.getTabAt(9).getCustomView().findViewById(R.id.tabImage);
        tabImage10.setImageBitmap(sketchImage.getImageAs(SketchImage.SKETCH_TO_COLOR_SKETCH,
                MAX_PROGRESS));
//        ImageView tabImage2 = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tabImage);
//        tabImage2.setImageBitmap(sketchImage.getImageAs(SketchImage.ORIGINAL_TO_SKETCH,
//                MAX_PROGRESS));



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                effectType = tabLayout.getSelectedTabPosition();
                if (effectType == 0){
                    binding.tvPb.setText(String.format("%d %%", MAX_PROGRESS));
                    binding.simpleSeekBar.setMax(MAX_PROGRESS);
                    binding.simpleSeekBar.setProgress(MAX_PROGRESS);
                    binding.ivTarget.setImageResource(R.drawable.download);
                }else {
                    binding.tvPb.setText(String.format("%d %%", MAX_PROGRESS));
                    binding.simpleSeekBar.setMax(MAX_PROGRESS);
                    binding.simpleSeekBar.setProgress(MAX_PROGRESS);
                    binding.ivTarget.setImageBitmap(sketchImage.getImageAs(effectType-1,
                            MAX_PROGRESS));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

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
                binding.pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.pb.setVisibility(View.INVISIBLE);
                binding.ivTarget.setImageBitmap(sketchImage.getImageAs(effectType-1,
                        seekBar.getProgress()));
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
}