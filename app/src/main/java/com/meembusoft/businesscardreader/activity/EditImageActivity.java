package com.meembusoft.businesscardreader.activity;

import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.meembusoft.businesscardreader.R;
import com.meembusoft.businesscardreader.compresshelper.CompressHelper;
import com.meembusoft.businesscardreader.compresshelper.FileUtil;
import com.meembusoft.businesscardreader.util.Logger;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import team.clevel.documentscanner.libraries.NativeClass;
import team.clevel.documentscanner.libraries.PolygonView;

import java.io.File;
import java.util.*;

import static com.meembusoft.businesscardreader.util.AllConstants.INTENT_KEY_IMAGE_URI;

public class EditImageActivity extends AppCompatActivity {

    // Toolbar
    private LinearLayout llLeftMenu, llRightMenu;
    private ImageView rightMenu;
    private TextView toolbarTitle;

    // File compression
    private String TAG = EditImageActivity.class.getSimpleName();
    private Uri mPhotoUri;
    private File fileOriginal, fileCompressed;
    private Bitmap bitmapOriginal, bitmapCompressed, bitmapCompressedTemp;

    // Document scanner
    private PolygonView polygonView;
    private FrameLayout flProcessingImageHolder;
    private ProgressBar progressBar;
    private ImageView ivCrop, ivRecognize, ivRotate, ivInvert, ivAlign, ivProcessingImage;
    private LinearLayout llCrop, llRecognize, llRotate, llInvert, llAlign;
    private TextView tvCrop, tvRecognize, tvRotate, tvInvert, tvAlign;
    private PROCESSING_TYPE mProcessingType;
    private NativeClass nativeClass;
    private boolean isInverted = false;

    public enum PROCESSING_TYPE {CROP, INVERT, ALIGN, ROTATE, RECOGNIZE}

    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        initUI();
        initEditImageScreen();
        initActions();
    }

    private void initUI() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Process Image");
        llRightMenu = findViewById(R.id.ll_right_menu);
        llLeftMenu = findViewById(R.id.ll_left_menu);
        rightMenu = findViewById(R.id.right_menu);
        rightMenu.setBackgroundResource(R.drawable.ic_round_tick);

        ivProcessingImage = findViewById(R.id.iv_processing_image);
        flProcessingImageHolder = findViewById(R.id.fl_processing_image_holder);
        polygonView = findViewById(R.id.polygonView);
        progressBar = findViewById(R.id.progressBar);
        ivCrop = findViewById(R.id.iv_crop);
        ivRecognize = findViewById(R.id.iv_recognize);
        ivRotate = findViewById(R.id.iv_rotate);
        ivInvert = findViewById(R.id.iv_invert);
        ivAlign = findViewById(R.id.iv_align);

        tvCrop = findViewById(R.id.tv_crop);
        tvRecognize = findViewById(R.id.tv_recognize);
        tvRotate = findViewById(R.id.tv_rotate);
        tvInvert = findViewById(R.id.tv_invert);
        tvAlign = findViewById(R.id.tv_align);

        llCrop = findViewById(R.id.ll_crop);
        llRecognize = findViewById(R.id.ll_recognize);
        llRotate = findViewById(R.id.ll_rotate);
        llInvert = findViewById(R.id.ll_invert);
        llAlign = findViewById(R.id.ll_align);
    }

    private void initActions() {
        llLeftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llRightMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set progress bar
                setProgressBar(true);

                Bitmap tempBitmap = getCroppedImage();
                if (tempBitmap != null) {
                    ivProcessingImage.setImageBitmap(null);
                    ivProcessingImage.setImageBitmap(tempBitmap);
                    llRightMenu.setVisibility(View.INVISIBLE);
                    rightMenu.setVisibility(View.INVISIBLE);
                    polygonView.setVisibility(View.GONE);
                }

                // Set progress bar
                setProgressBar(false);
            }
        });
        llCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBottomBar(PROCESSING_TYPE.CROP);
            }
        });
        llRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBottomBar(PROCESSING_TYPE.RECOGNIZE);
            }
        });
        llRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBottomBar(PROCESSING_TYPE.ROTATE);
            }
        });
        llInvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBottomBar(PROCESSING_TYPE.INVERT);
            }
        });
        llAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBottomBar(PROCESSING_TYPE.ALIGN);
            }
        });
    }

    private void setSelectedBottomBar(PROCESSING_TYPE processingType) {
        mProcessingType = processingType;
        // Set progress bar
        setProgressBar(true);

        switch (processingType) {
            case CROP:
                llRightMenu.setVisibility(View.VISIBLE);
                rightMenu.setVisibility(View.VISIBLE);
                ivCrop.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRecognize.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRotate.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivInvert.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivAlign.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

                tvCrop.setTextColor(getResources().getColor(R.color.colorBlue));
                tvRecognize.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRotate.setTextColor(getResources().getColor(R.color.colorBlack));
                tvInvert.setTextColor(getResources().getColor(R.color.colorBlack));
                tvAlign.setTextColor(getResources().getColor(R.color.colorBlack));

                initImageCropperView();
                break;
            case INVERT:
                llRightMenu.setVisibility(View.INVISIBLE);
                rightMenu.setVisibility(View.INVISIBLE);
                ivCrop.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRecognize.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRotate.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivInvert.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                ivAlign.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

                tvCrop.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRecognize.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRotate.setTextColor(getResources().getColor(R.color.colorBlack));
                tvInvert.setTextColor(getResources().getColor(R.color.colorBlue));
                tvAlign.setTextColor(getResources().getColor(R.color.colorBlack));

                invertColor();
                break;
            case RECOGNIZE:
                llRightMenu.setVisibility(View.INVISIBLE);
                rightMenu.setVisibility(View.INVISIBLE);
                ivCrop.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRecognize.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRotate.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivInvert.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivAlign.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

                tvCrop.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRecognize.setTextColor(getResources().getColor(R.color.colorBlue));
                tvRotate.setTextColor(getResources().getColor(R.color.colorBlack));
                tvInvert.setTextColor(getResources().getColor(R.color.colorBlack));
                tvAlign.setTextColor(getResources().getColor(R.color.colorBlack));

                // Navigate to recognize activity
                Intent intentRecognize = new Intent(EditImageActivity.this, RecognizeActivity.class);
                intentRecognize.putExtra(INTENT_KEY_IMAGE_URI, mPhotoUri);
                startActivity(intentRecognize);
                break;
            case ROTATE:
                llRightMenu.setVisibility(View.INVISIBLE);
                rightMenu.setVisibility(View.INVISIBLE);
                ivCrop.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRecognize.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRotate.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                ivInvert.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivAlign.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

                tvCrop.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRecognize.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRotate.setTextColor(getResources().getColor(R.color.colorBlue));
                tvInvert.setTextColor(getResources().getColor(R.color.colorBlack));
                tvAlign.setTextColor(getResources().getColor(R.color.colorBlack));
                break;
            case ALIGN:
                llRightMenu.setVisibility(View.INVISIBLE);
                rightMenu.setVisibility(View.INVISIBLE);
                ivCrop.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRecognize.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRotate.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivInvert.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                ivAlign.setColorFilter(ContextCompat.getColor(EditImageActivity.this, R.color.colorBlue), android.graphics.PorterDuff.Mode.SRC_IN);

                tvCrop.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRecognize.setTextColor(getResources().getColor(R.color.colorBlack));
                tvRotate.setTextColor(getResources().getColor(R.color.colorBlack));
                tvInvert.setTextColor(getResources().getColor(R.color.colorBlack));
                tvAlign.setTextColor(getResources().getColor(R.color.colorBlue));
                break;
        }

        // Set progress bar
        setProgressBar(false);
    }

    private void setProgressBar(boolean isShow) {
        RelativeLayout rlContainer = findViewById(R.id.rlContainer);
        setViewInteract(rlContainer, !isShow);
        if (isShow) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setViewInteract(View view, boolean canDo) {
        view.setEnabled(canDo);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewInteract(((ViewGroup) view).getChildAt(i), canDo);
            }
        }
    }

    /*********************
     * Image Compression *
     *********************/
    private void initEditImageScreen() {
        // Set progress bar
        setProgressBar(true);
        nativeClass = new NativeClass();
        try {
            // Pick intent data
            Intent intent = getIntent();
            Object data = intent.getParcelableExtra(INTENT_KEY_IMAGE_URI);
            if (data != null && data instanceof Uri) {
                mPhotoUri = (Uri) data;
                Logger.d(TAG, "photoUri: " + mPhotoUri.toString());

                // Process original file
                fileOriginal = FileUtil.getTempFile(EditImageActivity.this, mPhotoUri);
                if (bitmapOriginal != null) {
                    bitmapOriginal.recycle();
                    bitmapOriginal = null;
                }
                bitmapOriginal = BitmapFactory.decodeFile(fileOriginal.getAbsolutePath());

                // Compress original file
                fileCompressed = CompressHelper.getDefault(getApplicationContext()).compressToFile(fileOriginal);
                if (bitmapCompressed != null) {
                    bitmapCompressed.recycle();
                    bitmapCompressed = null;
                }
                bitmapCompressed = BitmapFactory.decodeFile(fileCompressed.getAbsolutePath());
                bitmapCompressedTemp = bitmapCompressed.copy(bitmapCompressed.getConfig(), true);

                // For the first time set crop
                setSelectedBottomBar(PROCESSING_TYPE.CROP);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**************
     * Image Crop *
     **************/
    private void initImageCropperView() {
        isInverted = false;

        flProcessingImageHolder.post(new Runnable() {
            @Override
            public void run() {
                Bitmap scaledBitmap = scaledBitmap(bitmapCompressed, flProcessingImageHolder.getWidth(), flProcessingImageHolder.getHeight());
                ivProcessingImage.setImageBitmap(scaledBitmap);

                Bitmap tempBitmap = ((BitmapDrawable) ivProcessingImage.getDrawable()).getBitmap();

                Map<Integer, PointF> pointFs = null;
                try {
                    pointFs = getEdgePoints(tempBitmap);
                    polygonView.setPoints(pointFs);
                    polygonView.setVisibility(View.VISIBLE);

                    int padding = (int) getResources().getDimension(R.dimen.dp_16);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
                    layoutParams.gravity = Gravity.CENTER;

                    polygonView.setLayoutParams(layoutParams);
                    polygonView.setPointColor(getResources().getColor(R.color.colorGreenDark));
                } catch (Exception e) {
                    Logger.d(TAG, "initImageCropperView exception: " + e.getMessage());
                }

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    protected Bitmap getCroppedImage() {
        try {
            Map<Integer, PointF> points = polygonView.getPoints();

            float xRatio = (float) bitmapCompressed.getWidth() / ivProcessingImage.getWidth();
            float yRatio = (float) bitmapCompressed.getHeight() / ivProcessingImage.getHeight();

            float x1 = (points.get(0).x) * xRatio;
            float x2 = (points.get(1).x) * xRatio;
            float x3 = (points.get(2).x) * xRatio;
            float x4 = (points.get(3).x) * xRatio;
            float y1 = (points.get(0).y) * yRatio;
            float y2 = (points.get(1).y) * yRatio;
            float y3 = (points.get(2).y) * yRatio;
            float y4 = (points.get(3).y) * yRatio;
            return nativeClass.getScannedBitmap(bitmapCompressed, x1, y1, x2, y2, x3, y3, x4, y4);
        } catch (Exception e) {
            Logger.d(TAG, "getCroppedImage>>Exception: " + e.getMessage());
            return null;
        }
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) throws Exception {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
        if (point2f == null)
            point2f = new MatOfPoint2f();
        List<Point> points = Arrays.asList(point2f.toArray());
        List<PointF> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
        }

        return result;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    /****************
     * Image invert *
     ****************/
    private void invertColor() {
        // Do inversion
        if (!isInverted) {
            Bitmap bmpMonochrome = Bitmap.createBitmap(bitmapCompressed.getWidth(), bitmapCompressed.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpMonochrome);
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(ma));
            canvas.drawBitmap(bitmapCompressed, 0, 0, paint);
            bitmapCompressed = bmpMonochrome.copy(bmpMonochrome.getConfig(), true);
        } else {
            bitmapCompressed = bitmapCompressedTemp.copy(bitmapCompressedTemp.getConfig(), true);
        }
        isInverted = !isInverted;

        // Set new inverted image
        Bitmap scaledBitmap = scaledBitmap(bitmapCompressed, flProcessingImageHolder.getWidth(), flProcessingImageHolder.getHeight());
        ivProcessingImage.setImageBitmap(scaledBitmap);
    }
}