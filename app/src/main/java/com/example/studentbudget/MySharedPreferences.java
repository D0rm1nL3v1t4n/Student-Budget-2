package com.example.studentbudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MySharedPreferences {

    public static final String PREFERENCE_WEEK_KEY = ".com.example.studentbudget.weekdatakey";
    public static final String PREFERENCE_MONTH_KEY = ".com.example.studentbudget.monthdatakey";

    public static final String KEY_BUDGET = "budget";
    public static final String KEY_EXPENSES = "expenses";

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

    public void writePreferenceData(float budget, float expenses) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_BUDGET, budget);
        editor.putFloat(KEY_EXPENSES, expenses);
        editor.commit();
    }

    public void writeBudgetData(float budget) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_BUDGET, budget);
        editor.commit();
    }

    public void writeExpensesData(float expenses) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_EXPENSES, expenses);
        editor.commit();
    }


    public float readPreferenceData(String searchKey) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(searchKey, 0);
    }

    public void resetSharedPreferenceData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_BUDGET, 0);
        editor.putFloat(KEY_EXPENSES, 0);
        editor.commit();
    }


}
