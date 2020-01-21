package com.example.chatbot;

import android.os.Parcel;
import android.os.Parcelable;

public class Messages implements Parcelable {
    private  String from,message,type;

    public  Messages(){

    }
    public Messages(String from, String message, String type) {
        this.from = from;
        this.message = message;
        this.type = type;
    }

    protected Messages(Parcel in) {
        from = in.readString();
        message = in.readString();
        type = in.readString();
    }

    public static final Creator<Messages> CREATOR = new Creator<Messages>() {
        @Override
        public Messages createFromParcel(Parcel in) {
            return new Messages(in);
        }

        @Override
        public Messages[] newArray(int size) {
            return new Messages[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(from);
        parcel.writeString(message);
        parcel.writeString(type);
    }
}
