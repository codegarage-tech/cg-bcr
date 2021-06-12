package com.meembusoft.businesscardreader.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.meembusoft.businesscardreader.R;
import com.meembusoft.businesscardreader.util.Logger;
import com.meembusoft.businesscardreader.util.PermissionUtils;

import java.io.File;

import static com.meembusoft.businesscardreader.util.AllConstants.*;

public class HomeActivity extends AppCompatActivity {

    // Toolbar
    private LinearLayout llLeftMenu, llRightMenu;
    private ImageView rightMenu;
    private TextView toolbarTitle;

    private FABToolbarLayout layout;
    private View fab;
    private ImageView ivCamera, ivGallery, ivClose;
    //    private File fileOriginal, fileCompressed;
//    private Bitmap bitmapOriginal, bitmapCompressed;
    private String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Contacts");
        llRightMenu = findViewById(R.id.ll_right_menu);
        llLeftMenu = findViewById(R.id.ll_left_menu);
        rightMenu = findViewById(R.id.right_menu);

        layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        fab = findViewById(R.id.fabtoolbar_fab);
        ivCamera = findViewById(R.id.iv_camera);
        ivGallery = findViewById(R.id.iv_gallery);
        ivClose = findViewById(R.id.iv_close);

        llLeftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.show();
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraChooser();
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGalleryChooser();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.hide();
            }
        });
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAMERA:
                if (PermissionUtils.permissionGranted(requestCode, PERMISSION_REQUEST_CODE_CAMERA, grantResults)) {
                    startCameraChooser();
                }
                break;

            case PERMISSION_REQUEST_CODE_GALLERY:
                if (PermissionUtils.permissionGranted(requestCode, PERMISSION_REQUEST_CODE_GALLERY, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    private void startCameraChooser() {
        if (PermissionUtils.requestPermission(HomeActivity.this, PERMISSION_REQUEST_CODE_CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(HomeActivity.this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, INTENT_REQUEST_CODE_CAMERA);
        }
    }

    private void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, PERMISSION_REQUEST_CODE_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"), INTENT_REQUEST_CODE_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    // Hide fab toolbar
                    layout.hide();

                    try {
                        Uri mPhotoUri = FileProvider.getUriForFile(HomeActivity.this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
                        Logger.d(TAG, "PhotoUriCamera: " + mPhotoUri.toString());

                        navigateImageProcessing(mPhotoUri);
                    } catch (Exception ex) {
                        Logger.d(TAG, "Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                break;

            case INTENT_REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    // Hide fab toolbar
                    layout.hide();

                    try {
                        Uri mPhotoUri = (Uri) data.getData();
                        Logger.d(TAG, "PhotoUriGallery: " + mPhotoUri.toString());

                        navigateImageProcessing(mPhotoUri);
                    } catch (Exception ex) {
                        Logger.d(TAG, "Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                break;
        }
    }

    private void navigateImageProcessing(Uri photoUri) {
        Intent intentEditImage = new Intent(HomeActivity.this, EditImageActivity.class);
        intentEditImage.putExtra(INTENT_KEY_IMAGE_URI, photoUri);
        startActivityForResult(intentEditImage, INTENT_REQUEST_CODE_EDIT_IMAGE);

//        try {
//            // Process original file
//            fileOriginal = FileUtil.getTempFile(HomeActivity.this, photoUri);
//            if (bitmapOriginal != null) {
//                bitmapOriginal.recycle();
//                bitmapOriginal = null;
//            }
//            bitmapOriginal = BitmapFactory.decodeFile(fileOriginal.getAbsolutePath());
//
//            // Compress original file
//            fileCompressed = CompressHelper.getDefault(getApplicationContext()).compressToFile(fileOriginal);
//            if (bitmapCompressed != null) {
//                bitmapCompressed.recycle();
//                bitmapCompressed = null;
//            }
//            bitmapCompressed = BitmapFactory.decodeFile(fileCompressed.getAbsolutePath());
//
//            ScannerConstants.selectedImageBitmap = bitmapCompressed;
//            startActivityForResult(new Intent(HomeActivity.this, ImageProcessingActivity.class), INTENT_REQUEST_CODE_IMAGE_PROCESSING);
//        } catch (Exception ex) {
//            Logger.d(TAG, "Exception: " + ex.getMessage());
//        }
    }
}