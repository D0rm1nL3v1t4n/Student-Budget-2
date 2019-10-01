package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ViewExpenseActivity extends AppCompatActivity {

    DatabaseHelper db;

    String[] categories;
    int position = 0;

    int expenseId;
    String expenseName;
    float expensePrice;
    String categoryName;
    String expenseDate;

    SimpleDateFormat startSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    SimpleDateFormat endSDF = new SimpleDateFormat("dd/MM/yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        db = new DatabaseHelper(this);
        expenseName = getIntentData();
        getCategoryData(expenseName);
        categories = getAllCategories(db);
        setupCategoriesSpinner();
        showExpenseData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private String getIntentData(){
        Intent intent = getIntent();
        return intent.getStringExtra("EXPENSE_NAME");
    }

    private void getCategoryData(String expenseName) {
        Cursor data = db.searchData(DatabaseHelper.TABLE_EXPENSES, "*", DatabaseHelper.COL_NAME, expenseName);
        data.moveToFirst();
        expenseId = data.getInt(0);
        expensePrice = data.getFloat(2);
        try {
            expenseDate = endSDF.format(startSDF.parse(data.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int categoryId = data.getInt(3);
        setCategoryData(categoryId);
    }

    private void setCategoryData(int id) {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME, DatabaseHelper.COL_ID, id);
        data.moveToFirst();
        categoryName = data.getString(0);
    }

    private String[] getAllCategories(DatabaseHelper db) {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME);
        String[] myCategories = new String[data.getCount()];
        for (int i = 0; i < data.getCount(); ++i) {
            data.moveToPosition(i);
            myCategories[i] = data.getString(0);
            if (data.getString(0) == categoryName)
                position = i;
        }
        return myCategories;
    }

    private void setupCategoriesSpinner() {
        Spinner categoriesSpinner = findViewById(R.id.viewExpenseCategorySpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.categories_spinner, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(arrayAdapter);
    }

    private void showExpenseData() {
        EditText name = findViewById(R.id.viewExpenseNameEditText);
        EditText price = findViewById(R.id.viewExpensePriceEditText);
        Spinner category = findViewById(R.id.viewExpenseCategorySpinner);
        DatePicker datePicker = findViewById(R.id.viewExpenseDateDP);

        name.setText(expenseName);
        price.setText(expensePrice + "");
        category.setSelection(position);

        String[] splitExpenseDate = expenseDate.split("/");
        String day = splitExpenseDate[0];
        String month = splitExpenseDate[1];
        String year = "20" + splitExpenseDate[2];
        datePicker.updateDate(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
    }



}
