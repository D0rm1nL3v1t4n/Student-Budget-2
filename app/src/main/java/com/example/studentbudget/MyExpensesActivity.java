package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyExpensesActivity extends AppCompatActivity {

    DatabaseHelper db;

    String[] expenseCategoryColour;
    String[] expenseName;
    String[] expenseCategoryName;
    String[] expensePrice;
    String[] expenseDate;

    SimpleDateFormat startSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    SimpleDateFormat endSDF = new SimpleDateFormat("dd/MM/yy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_expenses);

        db = new DatabaseHelper(this);
        String[] categories = getAllCategories(db);
        getExpensesData();
        setupCategoriesSpinner(categories);
        setupExpensesListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private String[] getAllCategories(DatabaseHelper db) {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME);
        String[] myCategories = new String[data.getCount() + 1];
        myCategories[0] = "All";
        for (int i = 0; i < data.getCount(); ++i) {
            data.moveToPosition(i);
            myCategories[i + 1] = data.getString(0);
        }
        return myCategories;
    }

    private void setupCategoriesSpinner(String[] categories) {
        Spinner categoriesSpinner = findViewById(R.id.categoryFilterSpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.categories_spinner, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(arrayAdapter);
    }

    private void getExpensesData() {
        String query = "select * from " + DatabaseHelper.TABLE_EXPENSES + " order by " + DatabaseHelper.COL_DATE + " desc;";
        Cursor expenseData = db.myQuery(query);
        initialiseArrays(expenseData.getCount());
        for (int i = 0; i < expenseData.getCount(); ++i) {
            expenseData.moveToPosition(i);
            categoryData(expenseData(expenseData, i), i);
        }
    }

    private void initialiseArrays(int count) {
        expenseCategoryColour = new String[count];
        expenseName = new String[count];
        expenseCategoryName = new String[count];
        expensePrice = new String[count];
        expenseDate = new String[count];
    }

    private int expenseData(Cursor expenseData, int i) {
        expenseName[i] = expenseData.getString(1);
        expensePrice[i] = "£" + expenseData.getFloat(2);
        try {
            expenseDate[i] =  endSDF.format(startSDF.parse(expenseData.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return expenseData.getInt(3);
    }

    private void categoryData(int categoryId, int i) {
        Cursor categoryData = db.searchData(DatabaseHelper.TABLE_CATEGORIES, "*", DatabaseHelper.COL_ID, categoryId);
        categoryData.moveToFirst();
        expenseCategoryName[i] = categoryData.getString(1);
        expenseCategoryColour[i] = categoryData.getString(2);
    }

    private void setupExpensesListView() {
        ListView myExpensesListView = findViewById(R.id.myExpensesListView);
        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(this,expenseCategoryColour, expenseName, expenseCategoryName, expensePrice, expenseDate);
        myExpensesListView.setAdapter(expensesListAdapter);
    }
}
