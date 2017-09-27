package com.seoulapp.sandfox.retax.model;

import com.google.android.gms.maps.model.LatLng;

public class Market {
    public final static String tblMarket = "market";
    public final static String COL_ID = "_id";
    public final static String COL_ADDRESS = "address";
    public final static String COL_DISTRICT = "district";
    public final static String COL_CITY = "city";
    public final static String COL_STORE = "store";
    public final static String COL_LAT = "lat";
    public final static String COL_LNG = "lng";

    private String address;
    private String district;
    private String city;
    private String store;
    private LatLng latlng;

    static String getSql(){
        return "create table if not exists " +
                tblMarket+" ( "+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ADDRESS+" TEXT, " +
                COL_DISTRICT+" TEXT, " +
                COL_CITY+" TEXT, " +
                COL_STORE+" TEXT," +
                COL_LAT+" REAL, " +
                COL_LNG+" REAL );";
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getStore() {
        return store;
    }
    public void setStore(String store) {
        this.store = store;
    }

    public LatLng getLatlng() {
        return latlng;
    }
    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }


}
