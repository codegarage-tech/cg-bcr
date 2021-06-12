package com.meembusoft.businesscardreader.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.meembusoft.businesscardreader.util.AppUtils;
import com.meembusoft.businesscardreader.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class BCRApp extends Application {

    private static Context mContext;
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mContext == null) {
            mContext = this;
        }

        //Initialize logger
        new Logger.Builder()
                .isLoggable(AppUtils.isDebug(mContext))
                .build();

        //For using vector drawable
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Initialize font
        initTypeface();

        //Multidex initialization
        MultiDex.install(this);
    }

    private void initTypeface() {
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }

    public static Context getGlobalContext() {
        return mContext;
    }
}