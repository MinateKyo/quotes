package com.zularizal.homedashboard;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Quote implements Parcelable {

    private int ID;
    public String Speaker;
    public String Quotation;
    public String Place;
    public Date Timestamp;

    Quote(int id, String speaker, String quotation, String place, Date timestamp) {
        ID = id;
        Speaker = speaker;
        Quotation = quotation;
        Place = place;
        Timestamp = timestamp;
    }

    private Quote(Parcel in) {
        ID = in.readInt();
        Speaker = in.readString();
        Quotation = in.readString();
        Place = in.readString();
        Timestamp = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(Speaker);
        parcel.writeString(Quotation);
        parcel.writeString(Place);
        parcel.writeSerializable(Timestamp);
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
}
