package com.seoulapp.sandfox.retax;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.FirebaseDatabase;
import com.seoulapp.sandfox.retax.model.DbHelper;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/**
 * 어플리케이션 클래스.
 * created and deleted when thes application starts and ends
 *
 * 환경설정, 데이터베이스 생성, 실행 시작/끝 기록 등의 작업을 수행
 *
 */

public class ReTax extends Application {
    //DB Variables
    public static DbHelper dbHelper;
    public static SQLiteDatabase db;
    public static SharedPreferences sharedPreferences;

    private final static String SP_NAME = "retaxspfile0";

    public final static String INITIALIZED = "initialized";
    public final static String IN_PROGRESS = "in_progress";

    public static boolean IS_FIRST_TIME;
    public static String USER_UNIQUE_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean("first_launch", false)){
            IS_FIRST_TIME = false;
        }else{
            IS_FIRST_TIME = true;
            sharedPreferences.edit().putBoolean("first_launch", true).commit();
            generateUserID();
            FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/log").child("app_created_time").setValue(Calendar.getInstance().getTime());
        }
        USER_UNIQUE_ID = sharedPreferences.getString("userId", null);
        FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/log").child("login_time").child(UUID.randomUUID().toString()).setValue(Calendar.getInstance().getTime());
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        setProgress(false);
    }

    public static boolean isInitialized() {
        return sharedPreferences.getBoolean(INITIALIZED, false);
    }
    public static void setInitialized(){
        sharedPreferences.edit().putBoolean(INITIALIZED, true).commit();
    }

    public static boolean isProgress() {
        return sharedPreferences.getBoolean(IN_PROGRESS, false);
    }
    public static void setProgress(boolean progress){
        sharedPreferences.edit().putBoolean(IN_PROGRESS, progress).commit();
    }
    private void generateUserID(){
        Locale user_locale = getResources().getConfiguration().locale;
        sharedPreferences.edit().putString("userId", "user"+user_locale+ UUID.randomUUID()).commit();
        USER_UNIQUE_ID = sharedPreferences.getString("userId", null);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/log").child("logout_time").child(UUID.randomUUID().toString()).setValue(Calendar.getInstance().getTime());
    }
}
