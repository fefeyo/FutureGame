package com.archetypenova.futuregame.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by USER on 2015/09/05.
 */
public class GeofenceService extends IntentService{

    private Handler mHanlder;

    private Runnable checkData = new Runnable() {
        @Override
        public void run() {
            getUserData();
            getFlagData();
            mHanlder.postDelayed(this, 5000);
        }
    };

    public GeofenceService(String name) {
        super(name);
    }

    public GeofenceService(){
        super("AreaHack");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
                        try{
                            final String result = new String(responseBody, "UTF-8");
                            final JSONObject json = new JSONObject(result);
                        }catch (UnsupportedEncodingException|JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

    private void getFlagData(){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "",
                null,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            final String result = new String(responseBody, "UTF-8");
                            final JSONObject json = new JSONObject(result);
                        }catch (UnsupportedEncodingException|JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

}
