package com.archetypenova.futuregame;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.archetypenova.futuregame.service.GeofenceService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GameScreenActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private static GoogleMap mMap;
    private GoogleApiClient mApiClient;

    private static LatLng spot1, spot2, spot3, spot4, spot5;
    public static LatLng now;
    private static LatLng[] spots;

    private static CircleOptions co1, co2, co3, co4, co5;
    private static CircleOptions[] cOptions;

    private static GameScreenActivity mActivity;

    private static int red,blue;

    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        mActivity = this;
        red = getResources().getColor(R.color.red);
        blue = getResources().getColor(R.color.blue);

        spot1 = new LatLng(35.665722, 139.740220);
        spot2 = new LatLng(35.665526, 139.739272);
        spot3= new LatLng(35.664913, 139.739032 );
        spot4= new LatLng(35.666189, 139.739612);
        spot5 = new LatLng(35.664135, 139.738844);

        spots = new LatLng[]{spot1, spot2, spot3, spot4, spot5};
        cOptions = new CircleOptions[5];

        onMapReadyIfNeeded();

        buildGoogleApiClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null != mApiClient) mApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != mApiClient) mApiClient.disconnect();
    }

    //　使用API,接続（失敗）コールバックの設定
    protected synchronized void buildGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //　GoogleMapの初期設定
    private void onMapReadyIfNeeded() {
        if (null == mMap) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.game_map)).getMap();
            if (null != mMap) {
                createMapReady();
            }
        }
    }

    //　マップの詳細設定
    private void createMapReady() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        for(int i = 0; i < cOptions.length; i++){
            cOptions[i] = createSpot(Color.GREEN, spots[i]);
            mMap.addCircle(cOptions[i]);
        }

    }

    //　GeoPoint生成
    private Geofence createGeoPoint(final String id, final int type, final double lat, final double lng){
        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(type)
                .setCircularRegion(lat, lng, 35)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(5000)
                .build();
    }

    //　CirclePoint生成
    private CircleOptions createSpot(final int color, final LatLng spot){
        final CircleOptions co = new CircleOptions();
        co.fillColor(color);
        co.center(spot);
        co.radius(25);
        co.strokeWidth(0);

        return co;
    }

    public static void syncSpot(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.clear();
                //TODO:陣の色の変更と陣のアイコンの変更と反映
                for (CircleOptions co : cOptions) {
                    co.fillColor(red);
                    mMap.addCircle(co);
                    resetMarker(co);
                }
            }
        });
    }

    public static void setPlayerMarker(final LatLng[] position) {
        for (int i = 0; i < position.length; i++) {
            final MarkerOptions mo = new MarkerOptions();
            mo.icon(BitmapDescriptorFactory.fromResource(R.mipmap.user_blue));
            mo.position(position[i]);
            mMap.addMarker(mo);
        }
    }

    private static void resetMarker(final CircleOptions co){
        if(red == co.getFillColor()){
            //　チームアイコン追加(赤)
            addMarker(R.mipmap.ika_red, co.getCenter());
        }else{
            //　チームアイコン追加(青)
            addMarker(R.mipmap.ika_blue, co.getCenter());
        }
    }

    private static void addMarker(final int resId, final LatLng latLng){
        final MarkerOptions mo = new MarkerOptions();
        mo.position(latLng);
        mo.icon(BitmapDescriptorFactory.fromResource(resId));
        mMap.addMarker(mo);
    }

    private GeofencingRequest getGeofencingRequest(final int id) {
        final GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(createGeoPoint("s" + id, Geofence.GEOFENCE_TRANSITION_DWELL, spots[id].latitude, spots[id].longitude));

        return builder.build();
    }

    @Override
    public void onBackPressed() {
    }

    //　GoogleApi接続成功
    @Override
    public void onConnected(Bundle bundle) {
        try{
            for(int i = 0; i < 5;i++) {
                final Intent  intent = new Intent(getApplicationContext(), GeofenceService.class);
                intent.putExtra("id", i);
                LocationServices.GeofencingApi.addGeofences(
                        mApiClient,
                        getGeofencingRequest(i),
                        PendingIntent.getService(
                                getApplicationContext(),
                                i,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                );
            }

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mApiClient,
                    mLocationRequest,
                    this
            );

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    //　謎
    @Override
    public void onConnectionSuspended(int i) {}

    //　GoogleApi接続失敗
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("現在地", "緯度"+location.getLatitude()+"経度"+location.getLongitude());
        now = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 18));
    }
}