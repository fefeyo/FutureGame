package com.archetypenova.futuregame.fragments.matching;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.archetypenova.futuregame.GameScreenActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HostFragment extends Fragment implements View.OnClickListener{

    private ListView mListView;
    private BootstrapButton match_start;

    private SharedPreferences mPrefrerences;

    private static String room_id;

    public HostFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_loading, container, false);

        mListView = (ListView)v.findViewById(R.id.user_list);
        match_start = (BootstrapButton)v.findViewById(R.id.match_start);
        match_start.setOnClickListener(this);
        match_start.setEnabled(false);
        mPrefrerences = getActivity().getSharedPreferences("area_hack", Context.MODE_PRIVATE);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.match_start:
                final Intent i = new Intent(getActivity().getApplicationContext(), GameScreenActivity.class);
                startActivity(i);
                break;
        }
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

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

    //　プレイヤーの参加状況確認用
    private void checkUser(){
        final RequestParams params = new RequestParams();
        params.put("user_id", mPrefrerences.getString("id", null));
        params.put("room_id", room_id);
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
