package com.archetypenova.futuregame.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.archetypenova.futuregame.GameScreenActivity;
import com.archetypenova.futuregame.ResultActivity;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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
            getUserData();
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
        Log.i("id", intent.getIntExtra("id", 1000)+"");
        Toast.makeText(getApplicationContext(), intent.getIntExtra("id", 100000)+"", Toast.LENGTH_SHORT).show();
        playerPosition = new LatLng[4];
        flagState = new HashMap<>();
        mHanlder = new Handler();
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

    private void getFlagData(){
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
                                flags.put(flag.getInt("flag_id"), flag.getInt("joukyou"));
                            }
                        }catch (UnsupportedEncodingException|JSONException e){
                            e.printStackTrace();
                        }
                        GameScreenActivity.syncSpot(flagState);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
                }
        );
    }

    private void moveResult(){
        mHanlder.removeCallbacks(checkData);
        final Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        startActivity(intent);
    }

}
