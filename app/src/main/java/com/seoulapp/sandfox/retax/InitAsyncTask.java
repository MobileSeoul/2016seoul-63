package com.seoulapp.sandfox.retax;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.seoulapp.sandfox.retax.data.MarketLoader;
import com.seoulapp.sandfox.retax.data.RefundLoader;

/**
 * 어플을 처음 실행 시, raw data로부터 database에 입력시키는 작업을 하는 AsyncTask
 * SplashActivity에서 execute.
 */

public class InitAsyncTask extends AsyncTask<Void, Void, Integer>{
    private static final String LOG_TAG = InitAsyncTask.class.getSimpleName();
    private Context mContext;
    private SQLiteDatabase db;
    ProgressDialog dialog;

    public InitAsyncTask(Context context) {
        super();
        this.mContext = context;
        this.db = ReTax.db;

        dialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        dialog.setCancelable(false);
        dialog.setMessage("Loading DB..");
        dialog.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        try{
            RefundLoader rLoader = new RefundLoader(db, mContext);
            rLoader.initRefundData();
            MarketLoader mLoader = new MarketLoader(db, mContext);
            mLoader.initMarketData();


        }catch (Exception e){
            return Constants.STATUS_FAILED;
        }

        ReTax.setInitialized();
        return Constants.STATUS_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected void onPostExecute(Integer integer) {
        dialog.dismiss();
        broadcastStatus(integer);
    }

    private void broadcastStatus(Integer status){
        Intent intent = new Intent(Constants.ACTION_INIT);
        intent.putExtra(Constants.EXTRA_STATUS, status);
        mContext.sendBroadcast(intent);
    }
}
