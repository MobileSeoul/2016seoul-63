package com.seoulapp.sandfox.retax;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulapp.sandfox.retax.model.Market;
import com.seoulapp.sandfox.retax.model.Refund;

import java.util.HashMap;

/**
 * 환급매장/환급소에 대한 상세정보를 보여주는 클래스
 * Intent에서 인덱스 값으로 데이터베이스에서 값을 가져와 보여줄 수 있다.
 * 현재로썬 상세 내용은 부족.
 *
 */
public class DetailsActivity extends AppCompatActivity {
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView store = (TextView) findViewById(R.id.d_store);
        TextView addr = (TextView) findViewById(R.id.d_address);
        ImageView storeImage = (ImageView) findViewById(R.id.d_image);
        ImageView sort = (ImageView) findViewById(R.id.d_sort);
        ImageView company = (ImageView) findViewById(R.id.d_company);

        Intent intent = getIntent();
        String index = intent.getExtras().get("ID").toString();
        String tableName = intent.getStringExtra("tableName");

        SQLiteDatabase db = ReTax.db;

        if(tableName.equals(Market.tblMarket)){
            Cursor cursor = db.rawQuery("select "+
                    Market.COL_ADDRESS +"," + Market.COL_STORE +"," +Market.COL_LAT +","+ Market.COL_LNG
                    +" from " + tableName + " where _id = ?", new String[]{index});
            if(cursor.moveToNext()){
                addr.setText(cursor.getString(0));
                store.setText(cursor.getString(1));
                storeImage.setImageDrawable(
                        getResources().getDrawable(thumbResource(Constants.NORMAL_IMG, cursor.getString(1))));
                lat = cursor.getDouble(2);
                lng = cursor.getDouble(3);
            }
        }else{
            Cursor cursor = db.rawQuery("select "+
                    Refund.COL_ADDRESS +"," + Refund.COL_STORE +","+Refund.COL_SORT+","+Refund.COL_COMPANY
                    +"," +Market.COL_LAT +","+ Market.COL_LNG
                    +" from " + tableName + " where _id = ?", new String[]{index});
            if(cursor.moveToNext()){
                addr.setText(cursor.getString(0));
                store.setText(cursor.getString(1));
                storeImage.setImageDrawable(
                        getResources().getDrawable(thumbResource(Constants.NORMAL_IMG, cursor.getString(1))));
                sort.setImageDrawable(getResources().getDrawable(sortResource(cursor.getInt(2))));
                company.setImageDrawable(getResources().getDrawable(logoResource(cursor.getInt(3))));
                lat = cursor.getDouble(4);
                lng = cursor.getDouble(5);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
//        //1: global blue 2: global taxfree 3: KT Tourist Reward 4: Easy Tax Refund 5: Cube Refund

    public void onClickDirectionBtn(View v){
        Uri gmIntentUri = Uri.parse("google.navigation:q="+ lat +","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if(mapIntent.resolveActivity(getPackageManager()) != null){
            startActivity(mapIntent);
        }
    }

    public static int logoResource(int company) {
        switch (company) {
            case 1:
                return R.drawable.blue;
            case 2:
                return R.drawable.global;
            case 3:
                return R.drawable.kt;
            case 4:
                return R.drawable.easy;
            case 5:
                return R.drawable.cube;
            default:
                return R.drawable.default_pin;
        }
    }

    public static int thumbResource(int type, String store) {
        String name = store.toLowerCase();
        String[] keywords = {"doota", "hyundai", "lotte", "olive", "shinsegae", "e-mart", "innisfree", "nature republic", "the face shop"};
        HashMap<String, Integer> imageMap;

        if(type == Constants.THUMBNAIL){
            imageMap = new HashMap<>();
            imageMap.put(keywords[0],R.drawable.t_doota);
            imageMap.put(keywords[1],R.drawable.t_hdai);
            imageMap.put(keywords[2],R.drawable.t_lotte);
            imageMap.put(keywords[3],R.drawable.t_oy);
            imageMap.put(keywords[4],R.drawable.t_ssg);
            imageMap.put(keywords[5],R.drawable.t_emart);
            imageMap.put(keywords[6],R.drawable.t_innis);
            imageMap.put(keywords[7],R.drawable.t_nr);
            imageMap.put(keywords[8],R.drawable.t_tfs);

            for(String key: keywords){
                if (name.contains(key)){
                    return imageMap.get(key);
                }
            }
            return R.drawable.default_pin;

        }else{
            imageMap = new HashMap<>();
            imageMap.put(keywords[0],R.drawable.det_doota);
            imageMap.put(keywords[1],R.drawable.det_hdai);
            imageMap.put(keywords[2],R.drawable.det_lotte);
            imageMap.put(keywords[3],R.drawable.det_oy);
            imageMap.put(keywords[4],R.drawable.det_ssg);
            imageMap.put(keywords[5],R.drawable.det_emart);
            imageMap.put(keywords[6],R.drawable.det_innis);
            imageMap.put(keywords[7],R.drawable.det_nr);
            imageMap.put(keywords[8],R.drawable.det_tfs);

            for(String key: keywords){
                if (name.contains(key)){
                    return imageMap.get(key);
                }
            }
            return R.drawable.det_default;
        }
    }

    public static int sortResource(int sort) {
        switch (sort) {
            case 101:
                return R.drawable.window;
            case 102:
                return R.drawable.kiosk;
            default:
                return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
