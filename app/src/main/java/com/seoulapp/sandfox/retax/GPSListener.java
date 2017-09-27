package com.seoulapp.sandfox.retax;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * GPS을 이용해 현재 내 위치를 파악하는 리스너
 */

public class GPSListener implements LocationListener{
    private GoogleMap mMap;
    private Marker curMarker;
    public GPSListener(GoogleMap map) {
        this.mMap = map;
    }

    @Override
    public void onLocationChanged(Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        String msg = "Latitude : " + latitude + "\nLongitude : " + longitude;
        Log.i("GPSLocationService", msg);

        //현재 위치의 지도를 보여주기 위해 정의한 메소드 호출
        showCurrentLocation(latitude, longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void showCurrentLocation (Double latitude, Double longitude){
        //현재 위치를 ㅣㅇ요해 lat, long 객체 생성
        LatLng curPoint = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        curMarker = mMap.addMarker(new MarkerOptions().position(curPoint)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_pin)));
    }
}
