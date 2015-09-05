package com.archetypenova.futuregame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;

public class StartScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences mPreference;
    private SharedPreferences.Editor mEditor;
    private EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView start = (TextView)findViewById(R.id.main_start);
        start.setOnClickListener(this);

        mPreference = getSharedPreferences("area_hack", MODE_PRIVATE);
        nameText = new EditText(this);

    }

    @Override
    public void onClick(View v) {
        final Intent i = new Intent(getApplicationContext(), MatchingActivity.class);
        showNamenputDialog(i);
    }

    private void showNamenputDialog(final Intent i){
        if(null == checkName()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("名前を入力してください");
            builder.setView(nameText);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mEditor = mPreference.edit();
                    mEditor.putString("name", nameText.getText().toString());
                    mEditor.apply();
                    startActivity(i);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }else{
            startActivity(i);
        }
    }

    private String checkName(){
        final SharedPreferences pref = getSharedPreferences("area_hack", MODE_PRIVATE);
        return pref.getString("name", null);
    }
}