package com.zularizal.homedashboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class Home extends AppCompatActivity implements View.OnClickListener{
    private CardView find,about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        find = (CardView) findViewById(R.id.quotes);
        about = (CardView) findViewById(R.id.me);

        //add click listerner to the card
        find.setOnClickListener(this);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i ;

        switch (v.getId()) {
            case R.id.quotes : i = new Intent(this, MainActivity.class); startActivity(i); break;
            case R.id.me : i = new Intent(this, MainActivity.class); startActivity(i); break;

            default:break;
        }

    }
}
