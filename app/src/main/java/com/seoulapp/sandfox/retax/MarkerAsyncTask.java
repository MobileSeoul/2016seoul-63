package com.seoulapp.sandfox.retax;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.seoulapp.sandfox.retax.model.ClusterMapItem;
import com.seoulapp.sandfox.retax.model.Market;
import com.seoulapp.sandfox.retax.model.Refund;

import java.util.ArrayList;
import java.util.List;

import static com.seoulapp.sandfox.retax.ReTax.db;

/**
 * database에서 맵에 보여줄 마커(클러스터아이템)을 인스턴스시켜 배열에 저장하는
 * AsyncTask 클래스.
 *
 */

public class MarkerAsyncTask extends AsyncTask<Void, Void, Void> {
    private final static String LOG_TAG = MarkerAsyncTask.class.getSimpleName();
    static List<ClusterMapItem> marketClusterMapItems = new ArrayList<>();
    static List<ClusterMapItem> refundClusterMapItems = new ArrayList<>();

    public MarkerAsyncTask() {
        super();
    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.i(LOG_TAG, "doInBackground executed");
        try{
            //면세점 표시
            Cursor cursor = db.rawQuery(
                    "SELECT "+ Market.COL_LAT+","+
                            Market.COL_LNG+","+
                            Market.COL_STORE+","+
                            Market.COL_DISTRICT+","+
                            Market.tblMarket+"."+Market.COL_ID+
                            " FROM "+Market.tblMarket, null);


            while (cursor.moveToNext()){
                marketClusterMapItems.add(new ClusterMapItem(cursor.getDouble(0),
                        cursor.getDouble(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)));
            }

            cursor.close();

            //환급소 커서
            Cursor cursor2 = db.rawQuery(
                    "SELECT "+ Refund.COL_LAT+","+
                            Refund.COL_LNG+","+
                            Refund.COL_STORE+","+
                            Refund.COL_DISTRICT+","+
                            Refund.COL_SORT+","+
                            Refund.COL_COMPANY+",+"+
                            Refund.tblRefund+"."+Refund.COL_ID+
                            " FROM "+ Refund.tblRefund, null);

            while (cursor2.moveToNext()){
                refundClusterMapItems.add(new ClusterMapItem(cursor2.getDouble(0),
                        cursor2.getDouble(1),
                        cursor2.getString(2),
                        cursor2.getString(3),
                        cursor2.getInt(4),
                        cursor2.getInt(5),
                        cursor2.getInt(6)));
            }

            cursor2.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i(LOG_TAG,"market-size : " + marketClusterMapItems.size());
        Log.i(LOG_TAG,"refund-size : " + refundClusterMapItems.size());

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    //        //1: global blue 2: global taxfree 3: KT Tourist Reward 4: Easy Tax Refund 5: Cube Refund

}