package com.archetypenova.futuregame.fragments.matching;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.archetypenova.futuregame.MatchingActivity;
import com.archetypenova.futuregame.R;
import com.beardedhen.androidbootstrap.BootstrapButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwitchMatchingFragment extends Fragment implements View.OnClickListener{


    public SwitchMatchingFragment() {
    }

    private BootstrapButton quickPlay;
    private BootstrapButton invitePlay;

    private void assignViews(final View v) {
        quickPlay = (BootstrapButton) v.findViewById(R.id.quick_play);
        quickPlay.setOnClickListener(this);
        invitePlay = (BootstrapButton) v.findViewById(R.id.invite_play);
        invitePlay.setOnClickListener(this);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_switch_matching, container, false);
        assignViews(v);

        return v;
    }


    @Override
    public void onClick(View v) {
        final MatchingActivity activity = (MatchingActivity)getActivity();
        activity.changeFragment(new LoadingFragment());
    }
}
