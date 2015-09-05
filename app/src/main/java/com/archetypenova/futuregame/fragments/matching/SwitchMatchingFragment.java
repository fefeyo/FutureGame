package com.archetypenova.futuregame.fragments.matching;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.archetypenova.futuregame.MatchingActivity;
import com.archetypenova.futuregame.R;
import com.beardedhen.androidbootstrap.BootstrapButton;
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
public class SwitchMatchingFragment extends Fragment implements View.OnClickListener {


    public SwitchMatchingFragment() {
    }

    private BootstrapButton invitePlay;
    private BootstrapButton makeRoom;

    private void assignViews(final View v) {
        invitePlay = (BootstrapButton) v.findViewById(R.id.invite_room);
        invitePlay.setOnClickListener(this);
        makeRoom = (BootstrapButton) v.findViewById(R.id.make_room);
        makeRoom.setOnClickListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_switch_matching, container, false);
        assignViews(v);

        createUser();

        return v;
    }


    @Override
    public void onClick(View v) {
        final MatchingActivity activity = (MatchingActivity) getActivity();
        switch (v.getId()) {
            case R.id.make_room:
                activity.changeFragment(new HostFragment());
                break;
            case R.id.invite_room:
                activity.changeFragment(new GuestFragment());
                break;
        }
    }

    //　名前を元にユーザー情報作成
    private void createUser() {
        final SharedPreferences mPreferences = getActivity().getSharedPreferences("area_hack", Context.MODE_PRIVATE);
        if (null != mPreferences.getString("name", null)) {
            final RequestParams params = new RequestParams();
            params.put("user_name", mPreferences.getString("name", null));
            final AsyncHttpClient client = new AsyncHttpClient();
            client.get(
                    getActivity().getApplicationContext(),
                    "http://54.65.82.21/createUser.php",
                    params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                final SharedPreferences.Editor editor = mPreferences.edit();
                                final String result = new String(responseBody, "UTF-8");
                                Log.i("result", result);
                                final JSONObject json = new JSONObject(result);
                                editor.putString("id", json.getString("user_id"));
                                editor.apply();
                                Log.i("user_id", json.getString("user_id"));
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity().getApplicationContext(), "通信に失敗しました", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}
