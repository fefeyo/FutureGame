package com.archetypenova.futuregame.fragments.matching;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.archetypenova.futuregame.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuestFragment extends Fragment {

    private ListView mListView;
    private SharedPreferences mPreferences;

    private static String room_id;


    public GuestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_guest, container, false);
        mListView = (ListView)v.findViewById(R.id.invite_user_list);
        mPreferences = getActivity().getSharedPreferences("area_hack", Context.MODE_PRIVATE);

        return v;
    }

    private void enterRoom(){
        final RequestParams params = new RequestParams();
        params.put("user_id", mPreferences.getString("id", null));
        params.put("room_id", room_id);
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

    private void checkRoomId(){
        final EditText edit = new EditText(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("招待コードを入力してください");
        builder.setView(edit);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                room_id = edit.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
    }


}
