package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

import java.text.DateFormatSymbols;

public class BudgetHistoryActivity extends AppCompatActivity {

    Boolean isWeek;
    DatabaseHelper db;

    String[] primaryDateInfo;
    String[] secondaryDateInfo;
    float[] budgetValues;
    float[] expensesValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_history);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isWeek = getIntentData();
        db = new DatabaseHelper(this);

        getHistoryData();
        setAdapter();
    }

    private Boolean getIntentData() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("BUDGET_HISTORY_TYPE");
        if (type.equals("WEEK"))
            return true;
        else
            return false;
    }


    private void getHistoryData() {
        Cursor data;
        if (isWeek)
            data = db.searchData(DatabaseHelper.TABLE_WEEKLY_BUDGET_HISTORY, "*");
        else
            data = db.searchData(DatabaseHelper.TABLE_MONTHLY_BUDGET_HISTORY, "*");

        int count = data.getCount();

        primaryDateInfo = new String[count];
        secondaryDateInfo = new String[count];
        budgetValues = new float[count];
        expensesValues = new float[count];

        data.moveToFirst();
        for (int i = 0; i < count; ++i) {
            if (isWeek) {
                primaryDateInfo[i] = data.getInt(4) + "";
                secondaryDateInfo[i] = data.getString(3);
            }
            else {
                primaryDateInfo[i] = new DateFormatSymbols().getMonths()[data.getInt(3)];
                secondaryDateInfo[i] = "";
            }
            budgetValues[i] = data.getFloat(1);
            expensesValues[i] = data.getFloat(2);
            data.moveToNext();
        }
    }

    private void setAdapter() {
        ListView budgetHistoryListView = findViewById(R.id.budgetHistoryListView);
        BudgetHistoryListAdapter budgetHistoryListAdapter = new BudgetHistoryListAdapter(this, primaryDateInfo, secondaryDateInfo, budgetValues, expensesValues);
        budgetHistoryListView.setAdapter(budgetHistoryListAdapter);
    }
}
