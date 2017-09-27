package com.seoulapp.sandfox.retax.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 2016-10-12.
 */

public class Refund {
    public final static String tblRefund = "refund";
    public final static String COL_ID = "_id";
    public final static String COL_ADDRESS = "address";
    public final static String COL_DISTRICT = "district";
    public final static String COL_CITY = "city";
    public final static String COL_STORE = "store";
    public final static String COL_SORT = "sort";
    public final static String COL_COMPANY = "company";
    public final static String COL_LAT = "lat";
    public final static String COL_LNG = "lng";

    private String address;
    private String store;
    private String district;
    private String city;
    private String sort;
    private String company;
    private LatLng latLng;

    static String getSql(){
        return "create table if not exists "+
                tblRefund +" ( "+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ADDRESS+" TEXT, "+
                COL_DISTRICT+" TEXT, " +
                COL_CITY+" TEXT, " +
                COL_STORE+" TEXT, " +
                COL_LAT+" REAL, " +
                COL_LNG+" REAL, " +
                COL_SORT+" INTEGER, " +
                COL_COMPANY+" INTEGER);";
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getStore() {
        return store;
    }
    public void setStore(String store) {
        this.store = store;
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

    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public LatLng getLatLng() {
        return latLng;
    }
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}
