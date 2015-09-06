package com.archetypenova.futuregame;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.archetypenova.futuregame.fragments.matching.SwitchMatchingFragment;

public class MatchingActivity extends AppCompatActivity {

    public static int color;
    public static String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        changeFragment(new SwitchMatchingFragment());
    }

    public void changeFragment(final Fragment fragment){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.matching_container, fragment);
        ft.commit();
    }

}