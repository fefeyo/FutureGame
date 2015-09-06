package com.archetypenova.futuregame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private static int redScore;
    private static int blueScore;
    ImageView red,blue;
    TextView resultText;
    Button returnButton;
    ProgressBar gauge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        red=(ImageView)findViewById(R.id.red);
        blue=(ImageView)findViewById(R.id.blue);
        resultText=(TextView)findViewById(R.id.resultText);
        returnButton=(Button)findViewById(R.id.returnButton);
        gauge=(ProgressBar)findViewById(R.id.gauge);
        gauge.setMax(5);

        returnButton.setOnClickListener(this);

        getResult();

        gauge.setProgress(blueScore);
        gauge.setSecondaryProgress(redScore);
        imageSet();

    }

    private void getResult(){
        final RequestParams params = new RequestParams();
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                getApplicationContext(),
                "http://54.65.82.21/checkFlag.php",
                params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            final String result = new String(responseBody, "UTF-8");
                            JSONObject json = new JSONObject(result);
                            JSONArray jsonArray = json.getJSONArray("flag_info");
                            for (int i = 0; i > jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int tmp = jsonObject.getInt("joukyou");
                                if (tmp > 0) {
                                    redScore++;
                                } else if (tmp < 0) {
                                    blueScore++;
                                }
                            }
                        } catch (UnsupportedEncodingException | JSONException e) {

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }
        );
    }

    private void imageSet(){
        if(redScore>blueScore){
            resultText.setText("赤チームの勝ちです！");
            red.setImageResource(R.drawable.win_red);
            blue.setImageResource(R.drawable.lose_bule);
        }else{
            resultText.setText("青チームの勝ちです！");
            red.setImageResource(R.drawable.lose_red);
            blue.setImageResource(R.drawable.win_bule);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(),MatchingActivity.class);
        startActivity(i);
    }
}