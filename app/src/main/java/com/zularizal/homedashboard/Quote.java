package com.zularizal.homedashboard;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.TextAppearanceSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Quote implements Parcelable {


    private int ID;
    public String Speaker;
    public String Quotation;
    public String Place;
    public Date Timestamp;
    private ArrayList<WordRange> hits;
    int HitForeColor = Color.RED;
    int HitBackColor = Color.YELLOW;

    Quote(int id, String speaker, String quotation, String place, Date timestamp) {
        ID = id;
        Speaker = speaker;
        Quotation = quotation;
        Place = place;
        Timestamp = timestamp;
        hits = new ArrayList<>();
    }

    private Quote(Parcel in) {
        ID = in.readInt();
        Speaker = in.readString();
        Quotation = in.readString();
        Place = in.readString();
        Timestamp = (Date) in.readSerializable();
        int wordrangeCount = in.readInt();
        WordRange[] words = new WordRange[wordrangeCount];
        in.readTypedArray(words, WordRange.CREATOR);
        hits = new ArrayList<>();
        hits.addAll(Arrays.asList(words));
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Speaker);
        parcel.writeString(Quotation);
        parcel.writeString(Place);
        parcel.writeSerializable(Timestamp);
        parcel.writeInt(hits.size());
        parcel.writeTypedArray(hits.toArray(new WordRange[0]), i);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Quote> CREATOR
            = new Parcelable.Creator<Quote>() {
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    public void clearHits() {
        hits = new ArrayList<>();
    }

    public void addHit(WordRange wr) {
        hits.add(wr);
    }

    public Spannable highlightedText() {
        if (Quotation == null)
            return new SpannableString("");

        Spannable s = new SpannableString(Quotation);

        for (WordRange wr : hits) {
            ColorStateList foreColor = new ColorStateList(new int[][] { new int[] {}}, new int[] {HitForeColor});
            TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, foreColor, null);
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(HitBackColor);

            s.setSpan(textAppearanceSpan, wr.start, wr.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(backgroundColorSpan, wr.start, wr.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return s;
    }
}
