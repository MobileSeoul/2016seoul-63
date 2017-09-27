package com.seoulapp.sandfox.retax.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.seoulapp.sandfox.retax.R;
import com.seoulapp.sandfox.retax.model.Market;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by user on 2016-10-12.
 */

public class MarketLoader {
    private final static String LOG_TAG = MarketLoader.class.getSimpleName();
    private SQLiteDatabase db;
    private Context mContext;
    private boolean marketLoaded = false;

    public MarketLoader(SQLiteDatabase db, Context context){
        this.db = db;
        this.mContext = context;

    }

    public void initMarketData() throws IOException{
        Log.i(LOG_TAG, "initMarketData() called.");

        if(marketLoaded) return;

        Log.d(LOG_TAG, "loading market data..");
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.marketdata);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try{
            String line;
            while((line = reader.readLine()) != null){
                String[] strings = TextUtils.split(line, "\\$");
                addRefund(strings);
            }
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

        marketLoaded = true;
    }


    private void addRefund(String[] strings){
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("insert into " + Market.tblMarket + "("+
                Market.COL_ADDRESS+", " +
                Market.COL_DISTRICT+", " +
                Market.COL_CITY+", "+
                Market.COL_STORE+", "+
                Market.COL_LAT+", "+
                Market.COL_LNG+") values ('" );

        queryBuffer.append(strings[0].replace(", ", " ") + "', '"); // address

        String[] addrDetail = strings[0].split(", "); //괄호 안 주소.

        int arrSize = addrDetail.length;
        queryBuffer.append(addrDetail[arrSize-2] + "', '"); // District
        queryBuffer.append(addrDetail[arrSize-1] + "', '"); // CITY
        queryBuffer.append(strings[1] + "', "); //STORE

        String[] latLng = strings[2].split(",");
        queryBuffer.append(latLng[0] + ", "); // LAT
        queryBuffer.append(latLng[1] + ");"); // LNG

        Log.i(LOG_TAG, queryBuffer.toString());
        db.execSQL(queryBuffer.toString());
    }

}
