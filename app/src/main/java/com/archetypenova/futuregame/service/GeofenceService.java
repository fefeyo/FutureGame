package com.archetypenova.futuregame.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.archetypenova.futuregame.GameScreenActivity;
import com.archetypenova.futuregame.ResultActivity;
import com.archetypenova.futuregame.fragments.matching.SwitchMatchingFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by USER on 2015/09/05.
 */
public class GeofenceService extends IntentService{

    private Handler mHanlder;

    private boolean userSuccessed;

    private LatLng[] playerPosition;

    private Runnable checkData = new Runnable() {
        @Override
        public void run() {
            Log.i("check_data", "check_data");
//            getUserData();
            getFlagData();
            mHanlder.postDelayed(this, 5000);
        }
    };

    private HashMap<Integer, Integer> flagState;

    public GeofenceService(String name) {
        super(name);
    }

    public GeofenceService(){
        super("AreaHack");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        playerPosition = new LatLng[4];
        flagState = new HashMap<>();
        mHanlder = new Handler();
        getFlag(intent.getIntExtra("id", 10000));
        mHanlder.post(checkData);
    }

    private void getUserData(){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "",
                null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody, "UTF-8");
                            final JSONObject json = new JSONObject(result);
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                        userSuccessed = true;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        userSuccessed = false;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        GameScreenActivity.setPlayerMarker(playerPosition);
                    }
                }
        );
    }

    private void getFlag(final int flag_id){
        final RequestParams params = new RequestParams();
        params.put("color", 0);
        params.put("flag_id", flag_id);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "http://54.65.82.21/getFlag.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

    public void getFlagData(){
        Log.i("hoge", "getFlagData()");
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "http://54.65.82.21/checkFlag.php",
                null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            final String result = new String(responseBody, "UTF-8");
                            final JSONObject json = new JSONObject(result);
                            if(json.getBoolean("judge")) moveResult();
                            final JSONArray flags = json.getJSONArray("flag_info");
                            for(int i = 0;i < flags.length(); i++){
                                final JSONObject  flag = flags.getJSONObject(i);
                                flagState.put(flag.getInt("flag_id"), flag.getInt("joukyou"));
                            }
                        }catch (UnsupportedEncodingException|JSONException e){
                            e.printStackTrace();
                        }
                        GameScreenActivity.syncSpot(flagState);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i("hoge", "statuscode ="+statusCode);
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    private void moveResult(){
        mHanlder.removeCallbacks(checkData);
        final Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        startActivity(intent);
    }

    private void zahyo(){
        final RequestParams params = new RequestParams();
        params.put("room_id", "");
        params.put("uesr_id", "");
        params.put("lat", "");
        params.put("lng", "");
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "http://54.65.82.21/zahyo.php",
                null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

}
