package com.seoulapp.sandfox.retax.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.seoulapp.sandfox.retax.R;
import com.seoulapp.sandfox.retax.model.Refund;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by user on 2016-10-12.
 */

public class RefundLoader {
    private final static String LOG_TAG = RefundLoader.class.getSimpleName();
    private SQLiteDatabase db;
    private Context mContext;
    private boolean refundLoaded = false;

    public RefundLoader(SQLiteDatabase db, Context context) {
        this.db = db;
        this.mContext = context;
    }

    public void initRefundData() throws IOException{
        Log.i(LOG_TAG, "initRefundData() called.");

        if(refundLoaded) return;

        Log.d(LOG_TAG, "Loading refund data..");

        InputStream inputStream = mContext.getResources().openRawResource(R.raw.refunddata);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            String line; int count=0;
            while((line = reader.readLine()) != null){
                String[] strings = TextUtils.split(line, "\\$");
                addRefund(strings);
                count++;
            }
            Log.i(LOG_TAG, "count : " + count);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }finally {
            if(reader != null){
                reader.close();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        refundLoaded = true;
    }
    private void addRefund(String[] strings){

        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("insert into " + Refund.tblRefund + "("+
                Refund.COL_ADDRESS+", " +
                Refund.COL_DISTRICT+", " +
                Refund.COL_CITY+", "+
                Refund.COL_STORE+", "+
                Refund.COL_LAT+", "+
                Refund.COL_LNG+", "+
                Refund.COL_SORT+", "+
                Refund.COL_COMPANY+") values ('" );

        queryBuffer.append(strings[0].replace(",", " ") + "', '"); // address

        String[] addrDetail = strings[0].split(","); //괄호 안 주소.

        int arrSize = addrDetail.length;
        queryBuffer.append(addrDetail[arrSize-2] + "', '"); // District
        queryBuffer.append(addrDetail[arrSize-1] + "', '"); // CITY
        queryBuffer.append(strings[1].replace(","," ") + "', "); //STORE

        String[] latLng = strings[2].split(",");
        queryBuffer.append(latLng[0] + ", "); // LAT
        queryBuffer.append(latLng[1] + ", "); // LNG

        String[] types = strings[3].split(",");
        queryBuffer.append(types[0] +", "); //SORT
        queryBuffer.append(types[1] +");"); //COMPANY

        Log.i(LOG_TAG, queryBuffer.toString());
        db.execSQL(queryBuffer.toString());
    }

}
