package com.squareandcube.locationtracking;

import android.content.Context;
import android.content.SharedPreferences;

public class LatlongPreferences {

    Context context;

    public LatlongPreferences(Context context) {
        this.context = context;
    }

    public void saveLatLong(String emplat, String emplong) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LatLong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LatValue", emplat);
        editor.putString("LongValue", emplong);
        editor.commit();
    }


    public String getLat() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LatLong", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LatValue", "");
    }

    public String getLong() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LatLong", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LongValue", "");
    }

//    public Boolean isEmpty() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("LatLong", Context.MODE_PRIVATE);
//        boolean isIdEmpty = sharedPreferences.getString("LatValue", "").isEmpty();
//        boolean isEmailEmpty = sharedPreferences.getString("LongValue", "").isEmpty();
//        return isIdEmpty || isEmailEmpty;
//    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LatLong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("LatValue");
        editor.remove("LongValue");
        editor.commit();
    }
}
