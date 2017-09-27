package com.seoulapp.sandfox.retax;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.seoulapp.sandfox.retax.model.DbHelper;

import java.util.HashMap;

/**
 * SearchSuggestion에 보여줄 데이터를 매칭하는 프로바이더
 */

public class DataProvider extends ContentProvider {
    public final static Uri CONTENT_URI_MARKET = Uri.parse("content://com.seoulapp.sandfox.retax.provider/market");
//    public final static Uri CONTENT_URI_REFUND = Uri.parse("content://com.seoulapp.sandfox.retax.provider/refund");
// Search Suggestions from two tables..

    private final static UriMatcher URI_MATCHER = buildUriMatcher();
    private final static int SEARCH = 3;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        DbHelper dbHelper = new DbHelper(getContext());
        db = dbHelper.getReadableDatabase();
        return true;
    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_URI_MARKET.getAuthority(), "market", 1);
        matcher.addURI(CONTENT_URI_MARKET.getAuthority(), "market/#", 2);
        matcher.addURI(CONTENT_URI_MARKET.getAuthority(), SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
        matcher.addURI(CONTENT_URI_MARKET.getAuthority(), SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
        return matcher;
    }


    private static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();
    static {
        PROJECTION_MAP.put("_id", "_id");
        PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "store AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2, "district AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
        PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }
        /**
     * 위에 projection_map에 suggestion table 생성
     * http://www.appsrox.com/android/tutorials/forexwiz/6/
     * 아니면 밑에 따라하기.
     * https://developer.android.com/guide/topics/search/adding-custom-suggestions.html
     */

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables("market");
        switch (URI_MATCHER.match(uri)){
            case SEARCH:
                String query = uri.getLastPathSegment().toLowerCase();
                builder.appendWhere("store LIKE '" + query + "%' OR ");
                builder.appendWhere("district LIKE '" + query + "%'");
                builder.setProjectionMap(PROJECTION_MAP);
                break;

        }
        return builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }
    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
