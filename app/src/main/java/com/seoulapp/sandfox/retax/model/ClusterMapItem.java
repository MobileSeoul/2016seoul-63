package com.seoulapp.sandfox.retax.model;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.seoulapp.sandfox.retax.Constants;
import com.seoulapp.sandfox.retax.R;

import static com.seoulapp.sandfox.retax.DetailsActivity.logoResource;
import static com.seoulapp.sandfox.retax.DetailsActivity.sortResource;
import static com.seoulapp.sandfox.retax.DetailsActivity.thumbResource;

/**
 * 맵에 뿌려지는 마커(클러스터아이템) 관련 클래스
 * 위치, 매장명, 매장지역, 매장썸네일, 환급기업 로고, 환급방식아이콘 등을 설정
 */

public class ClusterMapItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mStore;
    private final String mDistrict;
    private final int mIndex;

    private String mSort;
    private String mCompany;
    private int mLogoPic = R.drawable.default_pin;
    private int mSortIcon;

    private int mThumb;


    public ClusterMapItem(double lat, double lng, String store, String district, int id) {
        this.mPosition = new LatLng(lat, lng);
        this.mStore = store;
        this.mDistrict = district;
        this.mIndex = id;
        this.mThumb = thumbResource(Constants.THUMBNAIL, store);
    }

    public ClusterMapItem(double lat, double lng, String store, String district, int sort, int company, int id) {
        this.mPosition = new LatLng(lat, lng);
        this.mStore = store;
        this.mDistrict = district;
        this.mIndex = id;
        this.mSort = sortAs(sort);

        this.mLogoPic = logoResource(company); //global? kt?
        this.mSortIcon = sortResource(sort); //kiosk/window
        this.mThumb = thumbResource(Constants.THUMBNAIL, store);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getmStore() {
        return mStore;
    }

    public String getmDistrict() {
        return mDistrict;
    }

    public int getmIndex() {
        return mIndex;
    }

    public String getmSort() {
        return mSort;
    }

    public int getmLogoPic() {
        return mLogoPic;
    }

    public int getmSortIcon() {
        return mSortIcon;
    }

    public int getmThumb() {
        return mThumb;
    }

    private String sortAs(int type) {
        if (type == 101) {
            return "Service Window";
        } else {
            return "Kiosk Machine";
        }
    }



}

