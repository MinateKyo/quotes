package com.zularizal.homedashboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class QuoteDetailActivity extends AppCompatActivity {

    public final static String INTENT_QUOTE = "com.zularizal.homedashboard.Quote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Quote q = getIntent().getParcelableExtra(INTENT_QUOTE);
        if (q != null) {
            ((TextView) findViewById(R.id.lblSpeaker)).setText(q.Speaker);
            ((TextView) findViewById(R.id.lblPlace)).setText(q.Place);
            ((TextView) findViewById(R.id.lblDate)).setText(q.Timestamp.toString());
            ((TextView) findViewById(R.id.lblQuote)).setText(q.Quotation);
        }
    }

}
