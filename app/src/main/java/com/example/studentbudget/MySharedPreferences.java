package com.example.studentbudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MySharedPreferences {

    public static final String PREFERENCE_WEEK_KEY = ".com.example.studentbudget.weekdatakey";
    public static final String PREFERENCE_MONTH_KEY = ".com.example.studentbudget.monthdatakey";

    public static final String KEY_BUDGET = "budget";
    public static final String KEY_EXPENSES = "expenses";
    public static final String[] KEY_ALL = {KEY_BUDGET, KEY_EXPENSES};

    Context mContext;
    String mPreferenceKey;
    /*
        mPreferenceKey - the name of the SharedPreference 'file'
            --> one of the constant strings provided above
    */

    public MySharedPreferences(Context context, String preferenceKey) {
        mContext = context;
        mPreferenceKey = preferenceKey;
    }

    public void writePreferenceData(float[] values, int[] index) {
        /*
        values - the values to be stored in the sharedPreference
         */
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < index.length; ++i) {
            editor.putFloat(KEY_ALL[index[i]], values[i]);
            editor.commit();
        }
    }

    public float readPreferenceData(String searchKey) {
        /*
        searchKey - the key being searched for in the SharedPreference 'file'
            --> one of the constant strings provided above
         */
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(searchKey, 0);
    }

    public float[] getAllData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        float[] data = new float[KEY_ALL.length];
        for (int i = 0; i < KEY_ALL.length; ++i) {
            Log.d("READING SP", "Key: " + KEY_ALL[i] + ", Data: " + data[i]);
        }
        return data;
    }

    public void resetSharedPreferenceData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < KEY_ALL.length; ++i) {
            editor.putFloat(KEY_ALL[i], 0);
        }
    }


}
