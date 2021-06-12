//package com.meembusoft.businesscardreader.activity;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.ImageView;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.meembusoft.businesscardreader.R;
//import com.meembusoft.businesscardreader.compresshelper.CompressHelper;
//import com.meembusoft.businesscardreader.compresshelper.FileUtil;
//import com.meembusoft.businesscardreader.util.Logger;
//import team.clevel.documentscanner.ImageCropActivity;
//import team.clevel.documentscanner.helpers.ScannerConstants;
//
//import java.io.File;
//
//import static com.meembusoft.businesscardreader.util.AllConstants.INTENT_KEY_IMAGE_URI;
//
//public class ImageProcessingActivity extends AppCompatActivity {
//
//    private String TAG = ImageProcessingActivity.class.getSimpleName();
//    private Uri mPhotoUri;
//    private ImageView ivBitmapImage;
//    private File fileOriginal, fileCompressed;
//    private Bitmap bitmapOriginal, bitmapCompressed;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_processing);
//
//        ivBitmapImage = findViewById(R.id.iv_bitmap_image);
//
//        try {
//            // Pick intent data
//            Intent intent = getIntent();
//            Object data = intent.getParcelableExtra(INTENT_KEY_IMAGE_URI);
//            if (data != null && data instanceof Uri) {
//                mPhotoUri = (Uri) data;
//                Logger.d(TAG, "photoUri: " + mPhotoUri.toString());
//
//                // Process original file
//                fileOriginal = FileUtil.getTempFile(ImageProcessingActivity.this, mPhotoUri);
//                if (bitmapOriginal != null) {
//                    bitmapOriginal.recycle();
//                    bitmapOriginal = null;
//                }
//                bitmapOriginal = BitmapFactory.decodeFile(fileOriginal.getAbsolutePath());
//
//                // Compress original file
//                fileCompressed = CompressHelper.getDefault(getApplicationContext()).compressToFile(fileOriginal);
//                if (bitmapCompressed != null) {
//                    bitmapCompressed.recycle();
//                    bitmapCompressed = null;
//                }
//                bitmapCompressed = BitmapFactory.decodeFile(fileCompressed.getAbsolutePath());
//
//                ScannerConstants.selectedImageBitmap = bitmapCompressed;
//                startActivityForResult(new Intent(ImageProcessingActivity.this, ImageCropActivity.class), 1234);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//}