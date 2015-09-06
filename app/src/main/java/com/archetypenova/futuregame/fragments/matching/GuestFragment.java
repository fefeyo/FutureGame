package com.archetypenova.futuregame.fragments.matching;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.archetypenova.futuregame.GameScreenActivity;
import com.archetypenova.futuregame.MatchingActivity;
import com.archetypenova.futuregame.R;
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
public class GuestFragment extends Fragment {

    private ListView mListView;
    private TextView text;
    private SharedPreferences mPreferences;

    private static String[] names;
    private static int[] colors;
    private static ArrayAdapter<String> adapter;

    private static boolean judge;
    private static boolean start;

    private static Handler mHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            matchingUser();
            adapterSet();
            getStartBoolean();
            if(start){
                mHandler.removeCallbacks(runnable);
                Intent i = new Intent(getActivity(),GameScreenActivity.class);
                startActivity(i);
            }
            mHandler.postDelayed(this,1000);
        }
    };


    public GuestFragment() {
        // Required empty public constructor
    }

    private void getStartBoolean(){
        RequestParams params = new RequestParams();
        params.put("room_id",MatchingActivity.roomId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getActivity().getApplicationContext(),
                "http://54.65.82.21/checkStart.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody, "UTF-8");
                            JSONObject json = new JSONObject(result);
                            start = json.getBoolean("judge");
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_guest, container, false);
        mListView = (ListView)v.findViewById(R.id.invite_user_list);
        text = (TextView)v.findViewById(R.id.text);
        mPreferences = getActivity().getSharedPreferences("area_hack", Context.MODE_PRIVATE);


        checkRoomId();

        enterRoom();

        return v;
    }

    private void adapterSet(){
        adapter = new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                names
        );
        mListView.setAdapter(adapter);
    }

    private void checkRoomId(){
        final EditText edit = new EditText(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("招待コードを入力してください");
        builder.setView(edit);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MatchingActivity.roomId = edit.getText().toString();
                text.setText(MatchingActivity.roomId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
    }

    private void enterRoom(){
        final RequestParams params = new RequestParams();
        params.put("user_id", mPreferences.getString("id", null));
        params.put("room_id", MatchingActivity.roomId);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getActivity().getApplicationContext(),
                "http://54.65.82.21/enterRoom.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody, "UTF-8");
                            final JSONObject json = new JSONObject(result);
                            judge = json.getBoolean("judge");

                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

    //マッチング待機中
    private void matchingUser(){
        final RequestParams params = new RequestParams();
        params.put("room_id",MatchingActivity.roomId);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getActivity().getApplicationContext(),
                "http://54.65.82.21/checkRoom.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody,"UTF-8");
                            JSONObject json = new JSONObject(result);
                            JSONArray jsonArray = json.getJSONArray("user_info");
                            for (int i=0;i>jsonArray.length();i++){
                                JSONObject userInfo = jsonArray.getJSONObject(i);
                                names[i] = userInfo.getString("user_name");
                                colors[i] = userInfo.getInt("color");
                                String id = mPreferences.getString("id",null);
                                if (id.equals(userInfo.getString("user_id"))){
                                    MatchingActivity.color=userInfo.getInt("color");
                                }
                            }
                        }catch(UnsupportedEncodingException|JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mHandler==null){
                            mHandler = new Handler();
                            mHandler.post(runnable);
                        }
                    }
                }
        );

    }
}
