package com.meembusoft.businesscardreader.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.meembusoft.businesscardreader.R;
import com.meembusoft.businesscardreader.util.AppUtils;
import com.meembusoft.businesscardreader.util.Logger;

import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import static android.view.View.VISIBLE;
import static com.meembusoft.businesscardreader.util.AllConstants.INTENT_KEY_IMAGE_URI;

public class RecognizeActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener {

    private ProgressDialog mProgressDialog;
    private DetectTask mDetectTask;

    // Toolbar
    private LinearLayout llLeftMenu, llRightMenu;
    private ImageView rightMenu;
    private TextView toolbarTitle;

    private Uri mPhotoUri;
    private ImageView ivSelectedImage;
    private TextView tvName, tvPhone, tvEmail, tvAddress;
    private EditText edtName, edtPhone, edtEmail, edtAddress;
    private String TAG = RecognizeActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        // Pick intent data
        Intent intent = getIntent();
        Object data = intent.getParcelableExtra(INTENT_KEY_IMAGE_URI);
        if (data != null && data instanceof Uri) {
            mPhotoUri = (Uri) data;
            Logger.d(TAG, "photoUri: " + mPhotoUri.toString());
        }

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Recognize Contact");
        llRightMenu = findViewById(R.id.ll_right_menu);
        llLeftMenu = findViewById(R.id.ll_left_menu);
        rightMenu = findViewById(R.id.right_menu);

        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvAddress = findViewById(R.id.tv_address);

        edtName = findViewById(R.id.edt_name);
        edtPhone = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtAddress = findViewById(R.id.edt_address);

        ivSelectedImage = findViewById(R.id.iv_selected_image);
        AppUtils.loadImage(RecognizeActivity.this, ivSelectedImage, mPhotoUri, false, false, true);

        llLeftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvAddress.setOnLongClickListener(longClickListener);
        tvName.setOnLongClickListener(longClickListener);
        tvPhone.setOnLongClickListener(longClickListener);
        tvEmail.setOnLongClickListener(longClickListener);

        edtName.setOnLongClickListener(longClickListener);
        edtPhone.setOnLongClickListener(longClickListener);
        edtEmail.setOnLongClickListener(longClickListener);
        edtAddress.setOnLongClickListener(longClickListener);

        tvAddress.setOnDragListener(this);
        tvName.setOnDragListener(this);
        tvPhone.setOnDragListener(this);
        tvEmail.setOnDragListener(this);

        edtName.setOnDragListener(this);
        edtPhone.setOnDragListener(this);
        edtEmail.setOnDragListener(this);
        edtAddress.setOnDragListener(this);

//        mDetectTask = new DetectTask(RecognizeActivity.this, "Sk. Masud Rana, NextPage Technology Limited");
//        mDetectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissProgressDialog();
        if (mDetectTask != null && mDetectTask.getStatus() == AsyncTask.Status.RUNNING) {
            mDetectTask.cancel(true);
        }
    }

    /*****************
     * Drag and drop *
     *****************/
    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            AppUtils.doVibrate(RecognizeActivity.this, 3000);
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.VISIBLE);
            return true;
        }
    };

    @Override
    public boolean onDrag(View v, DragEvent event) {

        if (event.getAction() == DragEvent.ACTION_DROP) {
            //handle the dragged view being dropped over a target view

            if (v.equals(findViewById(R.id.edt_address)) || v.equals(findViewById(R.id.edt_email))
                    || v.equals(findViewById(R.id.edt_name)) || v.equals(findViewById(R.id.edt_phone))) {
                TextView dropped1 = (TextView) event.getLocalState();
                EditText dropTarget1 = (EditText) v;

                Logger.d("as", "onDrag: E" + dropped1.getText() + " " + dropTarget1.getText());
                String text = dropTarget1.getText().toString();
                //stop displaying the view where it was before it was dragged
                dropped1.setVisibility(VISIBLE);
                //dropped.setText(dropTarget.getText());
                //if an item has already been dropped here, there will be different string


                //if there is already an item here, set it back visible in its original place
                //  if(text.equals(text1.getText().toString())) text1.setVisibility(View.VISIBLE);
                //  else if(text.equals(text2.getText().toString())) text2.setVisibility(View.VISIBLE);
                //  else if(text.equals(text3.getText().toString())) text3.setVisibility(View.VISIBLE);


                //update the text and color in the target view to reflect the data being dropped
                dropTarget1.setText(dropped1.getText());
                dropped1.setText(text);
            } else {
                TextView dropped = (TextView) event.getLocalState();
                TextView dropTarget = (TextView) v;

                Logger.d("as", "onDrag: " + dropped.getText() + " " + dropTarget.getText());
                String text = dropTarget.getText().toString();
                //stop displaying the view where it was before it was dragged
                dropped.setVisibility(VISIBLE);
                //dropped.setText(dropTarget.getText());
                //if an item has already been dropped here, there will be different string


                //if there is already an item here, set it back visible in its original place
                //  if(text.equals(text1.getText().toString())) text1.setVisibility(View.VISIBLE);
                //  else if(text.equals(text2.getText().toString())) text2.setVisibility(View.VISIBLE);
                //  else if(text.equals(text3.getText().toString())) text3.setVisibility(View.VISIBLE);


                //update the text and color in the target view to reflect the data being dropped
                dropTarget.setText(dropped.getText());
                dropped.setText(text);
                // dropTarget.setBackgroundColor(Color.BLUE);
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);
            return true;
        } else return false;
    }

    /******************
     * Text detection *
     ******************/
    private class DetectTask extends AsyncTask<String, Integer, String> {

        private Context mContext;
        private String mTestData = "";

        private DetectTask(Context context, String testData) {
            mContext = context;
            mTestData = testData;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog progressDialog = showProgressDialog();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Logger.d(TAG, TAG + ">> Background task is cancelled");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                InputStream is;
                TokenizerModel tm;

                is = getAssets().open("en-token.bin");
                tm = new TokenizerModel(is);
                Tokenizer tokenizer = new TokenizerME(tm);
                String[] Tokens = tokenizer.tokenize(mTestData);

                String names = namefind(Tokens);
//                String org = orgfind(Tokens);

                Logger.d(TAG, TAG + ">> name: " + names);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                dismissProgressDialog();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public String namefind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
//            is = new FileInputStream("/home/rahul/opennlp/model/en-ner-person.bin");
                is = getAssets().open("en-ner-person.bin");
                tnf = new TokenNameFinderModel(is);
                nf = new NameFinderME(tnf);

                Span sp[] = nf.find(cnt);

                String a[] = Span.spansToStrings(sp, cnt);
                StringBuilder fd = new StringBuilder();
                int l = a.length;

                for (int j = 0; j < l; j++) {
                    fd = fd.append(a[j] + "\n");

                }
                sd = fd.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return sd;
        }

        public String orgfind(String cnt[]) {
            InputStream is;
            TokenNameFinderModel tnf;
            NameFinderME nf;
            String sd = "";
            try {
//            is = new FileInputStream("/home/rahul/opennlp/model/en-ner-organization.bin");
                is = getAssets().open("en-ner-organization.bin");
                tnf = new TokenNameFinderModel(is);
                nf = new NameFinderME(tnf);
                Span sp[] = nf.find(cnt);
                String a[] = Span.spansToStrings(sp, cnt);
                StringBuilder fd = new StringBuilder();
                int l = a.length;

                for (int j = 0; j < l; j++) {
                    fd = fd.append(a[j] + "\n");

                }

                sd = fd.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return sd;
        }
    }

    /***************************
     * Progress dialog methods *
     ***************************/
    public ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(RecognizeActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                }
            });
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        return mProgressDialog;
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}