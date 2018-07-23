package com.zularizal.homedashboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
Entire project adapted from https://abhiandroid.com/database/sqlite

 */
public class MainActivity extends AppCompatActivity {
    DataBaseHelper dbhelper;
    Quote[] mQuotes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the database
        SQLiteDatabase db = null;
        dbhelper = new DataBaseHelper(this, "sentencetbm.db", 1);
        try {
            dbhelper.createDataBase();

            // force any upgrade.
            db = dbhelper.getWritableDatabase();
        } catch (Error ioe) {
            ioe.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }

        Button find = (Button) findViewById(R.id.btnFind);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.txtQuote);
                findQuote(et.getText().toString());
            }
        });
        ((RadioButton) findViewById(R.id.rbExact)).setChecked(true);
        mQuotes = AllQuotes();
    }

    public void findQuote(String s) {
        RadioButton rbExact = (RadioButton) findViewById(R.id.rbExact);
        RadioButton rbAll = (RadioButton) findViewById(R.id.rbAll);
        RadioButton rbAllInOrder = (RadioButton) findViewById(R.id.rbAllInOrder);

        Quote[] matches = null;

        // remove any highlighting.
        for (Quote q: mQuotes)
            q.clearHits();

        if (rbExact.isChecked())
            matches = Matches(s);
        else if (rbAll.isChecked())
            matches = MatchesMultiword(s);
        else if (rbAllInOrder.isChecked())
            matches = MatchesMultiwordInOrder(s);

        if (matches == null)    // shouldn't happen.
            return;

        TableLayout tl = (TableLayout) findViewById(R.id.tblResults);
        tl.removeAllViews();
        LayoutInflater l = getLayoutInflater();
        for (Quote q : matches) {
            if (q == null)
                continue;

            TableRow tr = (TableRow) l.inflate(R.layout.resultrow, tl, false);
            TextView tvSpeaker = (TextView) tr.findViewById(R.id.txtSpeaker);
            TextView tvQuotation = (TextView) tr.findViewById(R.id.txtQuotation);
            tvSpeaker.setText(q.Speaker);
            tvQuotation.setText(q.highlightedText());
            tl.addView(tr, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            final Quote quoteToView = q;

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getBaseContext(), QuoteDetailActivity.class);
                    i.putExtra(QuoteDetailActivity.INTENT_QUOTE, quoteToView);
                    startActivity(i);
                }
            });
        }
    }

    Quote[] Matches(String s) {
        if (s == null || s.length() == 0)
            return new Quote[0];

        TBM bm = TBM.compile(s);
        ArrayList<Quote> resultmatches = new ArrayList<>();

        for (Quote q : mQuotes) {
            if (q == null)
                continue;
            List<Integer> result =  bm.findAll(q.Quotation);
            if (result.size() > 0) {
                for (int i : result)
                    q.addHit(new WordRange(i, i + s.length()));
                resultmatches.add(q);
            }
        }

        return resultmatches.toArray(new Quote[0]);
    }

    Quote[] MatchesMultiword(String s) {
        if (s == null || s.length() == 0)
            return new Quote[0];

        String[] words = s.split("\\s+");
        TBM matchers[] = new TBM[words.length];

        for (int i = 0; i < words.length; i++)
            matchers[i] = TBM.compile(words[i]);

        ArrayList<Quote> resultmatches = new ArrayList<>();

        for (Quote q : mQuotes) {
            if (q == null)
                continue;

            Boolean fIsMatch = true;
            for (TBM bm : matchers) {
                List<Integer> result =  bm.findAll(q.Quotation);
                if (result.size() == 0)
                    fIsMatch = false;
                else {
                    for (int i : result) {
                        q.addHit(new WordRange(i, i + bm.matchLength));
                    }
                }
            }

            if (fIsMatch)
                resultmatches.add(q);
        }

        return resultmatches.toArray(new Quote[0]);
    }

    Quote[] MatchesMultiwordInOrder(String s) {
        if (s == null || s.length() == 0)
            return new Quote[0];

        String[] words = s.split("\\s+");
        TBM matchers[] = new TBM[words.length];

        for (int i = 0; i < words.length; i++)
            matchers[i] = TBM.compile(words[i]);

        ArrayList<Quote> resultmatches = new ArrayList<>();

        for (Quote q : mQuotes) {
            if (q == null)
                continue;
            String searchString = q.Quotation;

            Boolean fIsMatch = true;
            for (int i = 0; i < matchers.length; i++) {
                List<Integer> lst = matchers[i].findAll(searchString);

                if (lst.size() == 0) {
                    fIsMatch = false;
                    break;
                }

                int matchPos = lst.get(0);

                int adjustedMatchPos = matchPos + (q.Quotation.length() - searchString.length());
                q.addHit(new WordRange(adjustedMatchPos, adjustedMatchPos + matchers[i].matchLength));

                searchString = searchString.substring(matchPos + words[i].length());
            }

            if (fIsMatch)
                resultmatches.add(q);
        }

        return resultmatches.toArray(new Quote[0]);
    }

    Quote[] AllQuotes() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor c = null;
        int iRow = 0;

        Quote[] results = null;

        try {
            c = db.query("quotes", null, "", null, null, null, null);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm'Z'", Locale.getDefault());

            if (c != null) {
                int colID = c.getColumnIndex("ID");
                int colSpeaker = c.getColumnIndex("speaker");
                int colQuote = c.getColumnIndex("Quote");
                int colPlace = c.getColumnIndex("Place");
                int colDate = c.getColumnIndex("Date");
                results = new Quote[c.getCount()];
                while (c.moveToNext()) {
                    Date d = sdf.parse(c.getString(colDate));
                    Quote q = new Quote(c.getInt(colID), c.getString(colSpeaker), c.getString(colQuote), c.getString(colPlace), d);
                    results[iRow++] = q;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return results;
    }
}
