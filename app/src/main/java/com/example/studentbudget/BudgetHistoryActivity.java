package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BudgetHistoryActivity extends AppCompatActivity {

    Boolean isWeek;
    DatabaseHelper db;

    String[] primaryDateInfo;
    String[] secondaryDateInfo;
    float[] budgetValues;
    float[] expensesValues;

    SimpleDateFormat startSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    SimpleDateFormat endSDF = new SimpleDateFormat("dd/MM/yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_history);

        isWeek = getIntentData();
        db = new DatabaseHelper(this);

        setHeadingData();
        getHistoryData();
        setAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private Boolean getIntentData() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("BUDGET_HISTORY_TYPE");
        if (type.equals("WEEK"))
            return true;
        else
            return false;
    }

    private void setHeadingData() {
        TextView headingTextView = findViewById(R.id.budgetHistoryHeadingTextView);
        if (isWeek)
            headingTextView.setText("Weekly Budget History");
        else
            headingTextView.setText("Monthly Budget History");
    }

    private void getHistoryData() {
        Cursor data;
        if (isWeek)
            data = db.searchData(DatabaseHelper.TABLE_WEEKLY_BUDGET_HISTORY, "*");
        else
            data = db.searchData(DatabaseHelper.TABLE_MONTHLY_BUDGET_HISTORY, "*");

        int count = data.getCount();

        primaryDateInfo = new String[count - 1];
        secondaryDateInfo = new String[count - 1];
        budgetValues = new float[count - 1];
        expensesValues = new float[count - 1];

        for (int i = 0; i < count - 1; ++i) {
            data.moveToPosition(i + 1);
            if (isWeek) {
                primaryDateInfo[i] = "Week " + data.getInt(4);
                try {
                    secondaryDateInfo[i] =  endSDF.format(startSDF.parse(data.getString(3)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else {
                primaryDateInfo[i] = new DateFormatSymbols().getMonths()[data.getInt(3)];
                secondaryDateInfo[i] = "";
            }
            budgetValues[i] = data.getFloat(1);
            expensesValues[i] = data.getFloat(2);

        }
    }

    private void setAdapter() {
        ListView budgetHistoryListView = findViewById(R.id.budgetHistoryListView);
        BudgetHistoryListAdapter budgetHistoryListAdapter = new BudgetHistoryListAdapter(this, primaryDateInfo, secondaryDateInfo, budgetValues, expensesValues);
        budgetHistoryListView.setAdapter(budgetHistoryListAdapter);
    }
}
