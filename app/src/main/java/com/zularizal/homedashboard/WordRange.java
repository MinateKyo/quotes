package com.zularizal.homedashboard;

import android.os.Parcel;
import android.os.Parcelable;

public class WordRange implements Parcelable {
    public int start;
    public int end;

    public WordRange() {
        start = end = 0;
    }

    WordRange(int startpos, int endpos) {
        start = startpos;
        end = endpos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private WordRange(Parcel in) {
        start = in.readInt();
        end = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(start);
        parcel.writeInt(end);
    }

    public static final WordRange.Creator<WordRange> CREATOR
            = new Parcelable.Creator<WordRange>() {
        public WordRange createFromParcel(Parcel in) {
            return new WordRange(in);
        }

        public WordRange[] newArray(int size) {
            return new WordRange[size];
        }
    };
}
