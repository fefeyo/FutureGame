package com.archetypenova.futuregame.fragments.matching;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.archetypenova.futuregame.GameScreenActivity;
import com.archetypenova.futuregame.MatchingActivity;
import com.archetypenova.futuregame.R;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogRecord;

/**
 * A simple {@link Fragment} subclass.
 */
public class HostFragment extends Fragment implements View.OnClickListener{

    private ListView mListView;
    private BootstrapButton match_start;

    private SharedPreferences mPrefrerences;

    private static ArrayAdapter<String> adapter;
    private static String userId;      //使うかは不明
    private static String userName;
    private static String[] names;
    private static int[] colors;
    private static boolean judge;
    private static boolean start;

    private Handler mHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkUser();
            setAdapter();
            if(start){
                mHandler.removeCallbacks(runnable);
                Intent i = new Intent(getActivity(),GameScreenActivity.class);
                startActivity(i);
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    public HostFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_loading, container, false);

        mListView = (ListView)v.findViewById(R.id.user_list);
        match_start = (BootstrapButton)v.findViewById(R.id.match_start);
        match_start.setOnClickListener(this);
//        match_start.setEnabled(false);
        mPrefrerences = getActivity().getSharedPreferences("area_hack", Context.MODE_PRIVATE);

        createRoom();
        checkUser();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.match_start:
                clickEnable();
                break;
        }
    }

    public void clickEnable(){
        final RequestParams params = new RequestParams();
        params.put("room_id",MatchingActivity.roomId);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getActivity().getApplicationContext(),
                "http://54.65.82.21/startGame.php",         //TODO URLの入力（StartAPI)
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody,"UTF-8");
                            JSONObject json = new JSONObject(result);
                            start = json.getBoolean("judge");       //TODO カラム名の入力(StartAPI)
                            Log.d("startttttttttttt",""+start);
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

    private void setAdapter(){

        adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                names
        );
        mListView.setAdapter(adapter);
    }

    //　ルームを作成
    private void createRoom(){
        final RequestParams params = new RequestParams();
        params.put("user_id", mPrefrerences.getString("id", null));
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getActivity().getApplicationContext(),
                "http://54.65.82.21/createRoom.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody,"UTF-8");
                            JSONObject json = new JSONObject(result);
                            MatchingActivity.roomId = json.getString("room_id");
                            Log.d("",""+MatchingActivity.roomId);
                        }catch (UnsupportedEncodingException | JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

//    //マッチング待機中
//    private void matchingUser(){
//        final RequestParams params = new RequestParams();
//        params.put("user_name",mPrefrerences.getString("name",null));
//        params.put("room_id",roomId);
//        final AsyncHttpClient client = new AsyncHttpClient();
//        client.get(
//                getActivity().getApplicationContext(),
//                "あああああああああああああああああああああああ",
//                params,
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        try {
//                            final String result = new String(responseBody,"UTF-8");
//                            JSONArray jsonArray = new JSONArray(result);
//                            for (int i=0; i>jsonArray.length(); i++ ){
//                                JSONObject json = jsonArray.getJSONObject(i);
//                                names[i] = json.getString("user_name");
//                                colors[i] = json.getInt("color");
//                            }
//                        }catch (UnsupportedEncodingException|JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                    }
//                }
//        );
//    }

    //　プレイヤーの参加状況確認用
    private void checkUser(){
        final RequestParams params = new RequestParams();
        params.put("room_id", MatchingActivity.roomId);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getActivity().getApplicationContext(),
                "http://54.65.82.21/checkRoom.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            final String result = new String(responseBody, "UTF-8");
                            JSONObject json = new JSONObject(result);
                            judge = json.getBoolean("judge");
                            if (judge) {
                                match_start.setEnabled(true);
                            }
                            JSONArray jsonArray = json.getJSONArray("user_info");
                            for (int i=0;i>jsonArray.length();i++){
                                JSONObject userInfo = jsonArray.getJSONObject(i);
                                names[i]=userInfo.getString("user_name");
                                colors[i]=userInfo.getInt("color"); //TODO getViewのpositionで色分け判定
                                String id = mPrefrerences.getString("id", null);
                                if (id.equals(userInfo.getString("user_id"))){
                                    MatchingActivity.color=userInfo.getInt("color");
                                }
                            }
                        }catch (UnsupportedEncodingException|JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Log.d("namename",""+names[0]);
                        if (mHandler==null){
                            mHandler = new Handler();
                            mHandler.post(runnable);
                        }
                    }
                }
        );
    }

}
