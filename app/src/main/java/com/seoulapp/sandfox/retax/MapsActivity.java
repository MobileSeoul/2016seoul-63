package com.seoulapp.sandfox.retax;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.seoulapp.sandfox.retax.model.ClusterMapItem;
import com.seoulapp.sandfox.retax.model.Market;
import com.seoulapp.sandfox.retax.model.Refund;
import com.seoulapp.sandfox.retax.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.seoulapp.sandfox.retax.MarkerAsyncTask.marketClusterMapItems;
import static com.seoulapp.sandfox.retax.MarkerAsyncTask.refundClusterMapItems;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterMapItem>, ClusterManager.OnClusterItemClickListener<ClusterMapItem>, GoogleMap.OnInfoWindowClickListener{
    private final static String LOG_TAG = MapsActivity.class.getSimpleName();

    private static boolean DATA_INITIATED = false; // To run asynctask only once.

    private GoogleMap mMap;

    private boolean FLAG_COLLAPSE = true;
    private boolean FLAG_INFO = false;
    private boolean FLAG_DUTYFREE = false;
    private boolean FLAG_AFTER = false;
    private boolean FLAG_MY_LOCATION = false;

    private ClusterManager<ClusterMapItem> clusterManager;

    private FloatingActionButton menu, delayed_option, immediate_option, mylocation, info_option;
    private TextView text1,text2;

    private Button legendButton;
    private RelativeLayout legends;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * 마지막 위치 저장.
         */
        ReTax.sharedPreferences.edit()
                .putString("latlng", mMap.getCameraPosition().target.latitude+"-"+mMap.getCameraPosition().target.longitude)
                .putFloat("zmlvl", mMap.getCameraPosition().zoom)
                .putBoolean("marketOn", FLAG_DUTYFREE)
                .putBoolean("refundOn", FLAG_AFTER).commit();

        Log.i(LOG_TAG, "onPause");

        DATA_INITIATED = true;
        Log.i(LOG_TAG, "DATA_INITIATED = TRUE");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //checking the internet connection
        checkNetworkStatus();


        legends = (RelativeLayout) findViewById(R.id.legends);


        //menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        info_option = (FloatingActionButton) findViewById(R.id.fab_option0);
        delayed_option = (FloatingActionButton) findViewById(R.id.fab_option1);
        immediate_option = (FloatingActionButton) findViewById(R.id.fab_option2);
        mylocation = (FloatingActionButton) findViewById(R.id.fab_option3);
        text1 = (TextView) findViewById(R.id.option1_txt);
        text2 = (TextView) findViewById(R.id.option2_txt);

        menu = (FloatingActionButton) findViewById(R.id.fab_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            menuCollapse(FLAG_COLLAPSE);
            }
        });

/**
 * Floating Button onClickListeners
 */
        info_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickColorChange(info_option, FLAG_INFO);

                if(FLAG_INFO){
                    FLAG_INFO = false;
                    legends.setVisibility(View.GONE);
                }else{
                    FLAG_INFO = true;
                    legends.setVisibility(View.VISIBLE);
                }
            }
        });

        immediate_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickColorChange(immediate_option, FLAG_DUTYFREE);
                FLAG_DUTYFREE = !FLAG_DUTYFREE;
                Log.e(LOG_TAG, "im : "+FLAG_DUTYFREE +"/ de: "+FLAG_AFTER);
                addMarkerOnMap();
            }
        });

        delayed_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickColorChange(delayed_option, FLAG_AFTER);
                FLAG_AFTER = !FLAG_AFTER;
                Log.e(LOG_TAG, "im : "+FLAG_DUTYFREE +"/ de: "+FLAG_AFTER);
                addMarkerOnMap();
            }
        });
        /**
         * TODO
         * 백화점 사진 넣고 -
         * DetailActivity 잘 나오는지 확인.
         * 길찾기 intent 추가
         *
         * gps 내 위치 찍는 것.
         *
         *firbase용 fragment 만들고 설정.
         */

        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //위치 관리자 객체 참조
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                GPSListener gpsListener = new GPSListener(mMap);
                long minTime = 10000;
                float minDistance = 0;
                try{
                    //GPS 기반 위치 요청
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,gpsListener);
                    //네트워크 기반 위치 요청
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime,minDistance,gpsListener);
                } catch (SecurityException ex){
                    ex.printStackTrace();
                }
                checkDangerousPermissions();
            }
        });

        if(!DATA_INITIATED){
            MarkerAsyncTask asyncTask = new MarkerAsyncTask();
            asyncTask.execute();
        }
        Log.i(LOG_TAG, "onCreate() : marketClusterMapItems = "+ marketClusterMapItems.size());
    }

    private void menuCollapse(boolean collapse){
        if (collapse){
            menu.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear_white_24px));
        }else{
            menu.setImageDrawable(getResources().getDrawable(R.drawable.ic_pin_drop_white_24px));
        }

        FLAG_COLLAPSE = !collapse;
        setFabMenuAnimation(info_option);
        setFabMenuAnimation(delayed_option);
        setFabMenuAnimation(immediate_option);
        setFabMenuAnimation(mylocation);
    }
    private void setFabMenuAnimation(FloatingActionButton fab){
        if(fab.getVisibility() == View.VISIBLE){
            fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_disappear));
            text1.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_disappear));
            text2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_disappear));
            fab.setVisibility(View.GONE);
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
        }else{
            fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_appear));
            text1.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_appear));
            text2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_appear));
            fab.setVisibility(View.VISIBLE);
            text1.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
        }
    }

    private void onClickColorChange(FloatingActionButton fab, boolean flag){
        if (flag) {
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.white));
            fab.setColorFilter(Color.BLACK);
        }else{
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.sub));
            fab.setColorFilter(Color.WHITE);
        }
    }

    private Cluster<ClusterMapItem> clickedCluster;
    private ClusterMapItem clickedClusterMapItem;

    @Override
    public boolean onClusterClick(Cluster cluster) {
        clickedCluster = cluster;
        showDialogWithClusterItems();

        return true;
    }
    @Override
    public boolean onClusterItemClick(ClusterMapItem clusterMapItem) {
        clickedClusterMapItem = clusterMapItem;
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(false);

        clusterManager = new ClusterManager<ClusterMapItem>(getApplicationContext(), mMap);
        clusterManager.setRenderer(new itemRenderer());

        mMap.setOnCameraIdleListener(clusterManager);

        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(this);

        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowForMarkers());

        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);

        initMapSettings();
    }

    private void initMapSettings(){
        String[] pref = ReTax.sharedPreferences.getString("latlng", "37.556308-126.986111").split("-");
        float zoom = ReTax.sharedPreferences.getFloat("zmlvl", 10);
        FLAG_DUTYFREE = ReTax.sharedPreferences.getBoolean("marketOn", false);
        FLAG_AFTER = ReTax.sharedPreferences.getBoolean("refundOn", false);

        LatLng latLng = new LatLng(Double.parseDouble(pref[0]), Double.parseDouble(pref[1]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        onClickColorChange(immediate_option, !FLAG_DUTYFREE); //True일 때, 색이 사라짐.
        onClickColorChange(delayed_option, !FLAG_AFTER);

        addMarkerOnMap();
    }

    private class itemRenderer extends DefaultClusterRenderer<ClusterMapItem>{
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final int mDimension;

        public itemRenderer() {
            super(getApplicationContext(), mMap, clusterManager);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_item_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_item_image_padding);
            mImageView.setPadding(padding,padding,padding,padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterMapItem item, MarkerOptions markerOptions) {
            //Draw a single item
            //Set info window
            if(item.getmSort() != null){
                mImageView.setImageResource(item.getmLogoPic());
            }else{
                mImageView.setImageResource(item.getmThumb());
            }

            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }


        @Override
        protected boolean shouldRenderAsCluster(Cluster<ClusterMapItem> cluster) {
            return cluster.getSize() > 1;
        }
    }

    private void addMarkerOnMap(){
        //colorWhenPressed();
        /**
         * Flag 확인 후, 마커 표시
         */
        clusterManager.clearItems();

        if (FLAG_DUTYFREE && FLAG_AFTER) {
            Log.e(LOG_TAG, "both called");
            List<ClusterMapItem> both = new ArrayList<>(marketClusterMapItems);
            both.addAll(refundClusterMapItems);
            clusterManager.addItems(both);
        } else if (!FLAG_AFTER && FLAG_DUTYFREE) {
            clusterManager.addItems(marketClusterMapItems);
            Log.e(LOG_TAG, "im called");
        } else if (!FLAG_DUTYFREE && FLAG_AFTER) {
            clusterManager.addItems(refundClusterMapItems);
            Log.e(LOG_TAG, "de called");
        } else {
            Log.e(LOG_TAG, "non called");
        }

        clusterManager.cluster();

    }


    private class CustomInfoWindowForMarkers implements GoogleMap.InfoWindowAdapter{

        private final View contentView;
        public CustomInfoWindowForMarkers() {
            contentView = getLayoutInflater().inflate(R.layout.info_layout, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
        @Override
        public View getInfoContents(Marker marker) {
            TextView tv1 = (TextView) contentView.findViewById(R.id.text1);
            TextView tv2 = (TextView) contentView.findViewById(R.id.text2);
            ImageView store = (ImageView) contentView.findViewById(R.id.info_image);
            ImageView sort = (ImageView) contentView.findViewById(R.id.info_sort);
            ImageView type = (ImageView) contentView.findViewById(R.id.info_type);

            if(clickedClusterMapItem != null){
                if(clickedClusterMapItem.getmSort() != null){
                    type.setImageDrawable(getResources().getDrawable(
                        clickedClusterMapItem.getmLogoPic()
                    ));
                    type.setVisibility(View.VISIBLE);

                    sort.setImageDrawable(getResources().getDrawable(
                            clickedClusterMapItem.getmSortIcon()
                    ));
                    sort.setVisibility(View.VISIBLE);

                }else{
                    sort.setVisibility(View.GONE);
                    type.setVisibility(View.GONE);
                }
                tv1.setText(clickedClusterMapItem.getmStore());
                tv2.setText(clickedClusterMapItem.getmDistrict());
                store.setImageDrawable(getResources().getDrawable(clickedClusterMapItem.getmThumb()));
            }
            return contentView;
        }
    }


    private ArrayList<ClusterMapItem> items;
    private void showDialogWithClusterItems(){
        items = (ArrayList) clickedCluster.getItems();
        final View innerView = getLayoutInflater().inflate(R.layout.cluster_dialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(clickedCluster.getSize()+" Stores");
        alertBuilder.setPositiveButton("Zoom-in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                zoomToClusterOnClicked();
            }
        });
        alertBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertBuilder.setView(innerView);
        alertBuilder.setAdapter(new DialogListAdapter(getApplicationContext(), items), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClusterMapItem clickedItem = items.get(i);
                Log.d(LOG_TAG, clickedItem.getmIndex() +", "+clickedItem.getmStore());
                //DetailsActivity로
                Intent dialogToDetail = new Intent(getApplicationContext(), DetailsActivity.class);
                dialogToDetail.putExtra("ID", clickedItem.getmIndex());
                dialogToDetail.putExtra("tableName", clickedItem.getmSort() == null? Market.tblMarket : Refund.tblRefund);

                /*FIREBASE 기록*/
                FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/clicked").child(UUID.randomUUID().toString()).setValue(
                        new User(Constants.CLUSTER_DIALOG, clickedItem.getmStore(), clickedItem.getmDistrict(),
                                clickedItem.getmSort() == null? Constants.IMMEDIATE_REFUND : Constants.DELAYED_REFUND, Calendar.getInstance().getTime())
                );

                startActivity(dialogToDetail);
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
    private void zoomToClusterOnClicked(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(ClusterMapItem i : clickedCluster.getItems()){
            builder.include(i.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i(LOG_TAG, "onInfoWindowClicked");

        Intent markerToDetail = new Intent(this, DetailsActivity.class);
        markerToDetail.putExtra("ID", clickedClusterMapItem.getmIndex());
        markerToDetail.putExtra("tableName", clickedClusterMapItem.getmSort() == null? Market.tblMarket : Refund.tblRefund);

        /*FIREBASE 기록*/
        FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/clicked").child(UUID.randomUUID().toString()).setValue(
                new User(Constants.INFO_WINDOW, clickedClusterMapItem.getmStore(), clickedClusterMapItem.getmDistrict(),
                        clickedClusterMapItem.getmSort() == null? Constants.IMMEDIATE_REFUND : Constants.DELAYED_REFUND, Calendar.getInstance().getTime())
        );
        startActivity(markerToDetail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        /*
        검색 시, 아래 창 보여지는 것 !
        http://ramannanda.blogspot.kr/2014/10/android-searchview-integration-with.html */
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        ComponentName componentName = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG, "query = " + query);
                FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/search_keys").child(UUID.randomUUID().toString()).setValue(
                        new User(Calendar.getInstance().getTime(), query)
                );

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(LOG_TAG, "newText = " + newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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



    /**
     * Check its network - loading map
     */
    private void checkNetworkStatus(){
        if(!isNetworkConnected()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Network Not Available");
            builder.setPositiveButton("RETURN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();
        }
    }
    private boolean isNetworkConnected(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null;
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "Turn on GPS for your location", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }
}
