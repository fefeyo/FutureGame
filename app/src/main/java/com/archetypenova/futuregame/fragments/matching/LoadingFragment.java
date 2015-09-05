package com.archetypenova.futuregame.fragments.matching;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.archetypenova.futuregame.GameScreenActivity;
import com.archetypenova.futuregame.R;
import com.beardedhen.androidbootstrap.BootstrapButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragment extends Fragment implements View.OnClickListener{

    private ListView mListView;
    private BootstrapButton match_start;
    private boolean judge;

    public LoadingFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_loading, container, false);

        mListView = (ListView)v.findViewById(R.id.user_list);
        match_start = (BootstrapButton)v.findViewById(R.id.match_start);
        match_start.setOnClickListener(this);
//        match_start.setEnabled(false);

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

    private class UserListAdapter extends ArrayAdapter {

        public UserListAdapter(Context context, int resource) {
            super(context, resource);
        }
    }

}
