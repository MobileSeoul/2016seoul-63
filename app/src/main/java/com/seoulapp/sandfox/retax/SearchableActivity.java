package com.seoulapp.sandfox.retax;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.seoulapp.sandfox.retax.model.Market;
import com.seoulapp.sandfox.retax.model.User;

import java.util.Calendar;
import java.util.UUID;

/**
 * 검색을 담당하는 액티비티로써 SearchSuggestion클릭시, 또는 SearchActivity의 아이템 클릭시
 * DetailsActivity로 보내는 역할을 한다.
 */
public class SearchableActivity extends ListActivity {
    private final static String LOG_TAG = SearchableActivity.class.getSimpleName();
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ReTax.db;
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent(){
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            Uri detailUri = intent.getData();
            String id = detailUri.getLastPathSegment();
            Log.i(LOG_TAG, "id to send with the intent >> " + id);

            Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
            detailsIntent.putExtra("ID", id);
            detailsIntent.putExtra("tableName", Market.tblMarket);

            FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/clicked").child(UUID.randomUUID().toString()).setValue(
                    new User(Constants.SEARCH_SUGGEST, id, Constants.IMMEDIATE_REFUND, Calendar.getInstance().getTime())
            );

            startActivity(detailsIntent);
            finish();
        }
    }


    private void doMySearch(String query){
        Cursor cursor = db.rawQuery("SELECT _id,store,district FROM market WHERE store LIKE ? OR district LIKE ?", new String[]{"%"+query+"%","%"+query+"%"});
        startManagingCursor(cursor);
        SimpleCursorAdapter simpleCursorAdapter = (SimpleCursorAdapter) getListAdapter(); // OR Use Adapter below
        if(simpleCursorAdapter == null){
            simpleCursorAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.activity_search_items,
                    cursor,
                    new String[]{"store", "district","_id"},
                    new int[]{R.id.search_store, R.id.search_district, R.id.search_index});

            setListAdapter(simpleCursorAdapter);
        }else{
            (simpleCursorAdapter).changeCursor(cursor);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView store = (TextView) v.findViewById(R.id.search_store);
        TextView district = (TextView) v.findViewById(R.id.search_district);
        TextView index = (TextView) v.findViewById(R.id.search_index);

        FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/clicked").child(UUID.randomUUID().toString()).setValue(
                new User(Constants.SEARCH_ACTIVITY, store.getText().toString(), district.getText().toString(), Constants.IMMEDIATE_REFUND, Calendar.getInstance().getTime())
        );
        Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        detailsIntent.putExtra("ID", index.getText());
        detailsIntent.putExtra("tableName", Market.tblMarket);
        startActivity(detailsIntent);
        finish();

    }
}
