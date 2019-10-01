package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
        setupAllExpensesListView();
        filterSpinnerChangeEvent(categories);
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
        Cursor expenseData = db.searchData(DatabaseHelper.TABLE_EXPENSES, "*");
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

    private void setupAllExpensesListView() {
        ListView myExpensesListView = findViewById(R.id.myExpensesListView);
        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(this, expenseCategoryColour, expenseName, expenseCategoryName, expensePrice, expenseDate);
        myExpensesListView.setAdapter(expensesListAdapter);
    }

    private void filterSpinnerChangeEvent(final String[] categories) {
        final Spinner categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);
        categoryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (categories[index].equals("All"))
                    setupAllExpensesListView();
                else
                    filterExpenses(categories[index]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //does this scenario exist?
            }
        });
    }

    private void filterExpenses(String categoryName) {
        ListView myExpensesListView = findViewById(R.id.myExpensesListView);
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < expenseName.length; ++i) {
            if (expenseCategoryName[i].equals(categoryName)) {
                indexes.add(i);
            }
        }
        String[] catColour = new String[indexes.size()];
        String[] name = new String[indexes.size()];
        String[] catName = new String[indexes.size()];
        String[] price = new String[indexes.size()];
        String[] date = new String[indexes.size()];

        for (int i = 0; i < indexes.size(); ++i) {
            catColour[i] = expenseCategoryColour[indexes.get(i)];
            name[i] = expenseName[indexes.get(i)];
            catName[i] = expenseCategoryName[indexes.get(i)];
            price[i] = expensePrice[indexes.get(i)];
            date[i] = expenseDate[indexes.get(i)];
        }

        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(this, catColour, name, catName, price, date);
        myExpensesListView.setAdapter(expensesListAdapter);


        //        ListView myExpensesListView = findViewById(R.id.myExpensesListView);
//        List<String> tempCategoryColour = new ArrayList<String>();
//        List<String> tempName = new ArrayList<String>();
//        List<String> tempCategoryName = new ArrayList<String>();
//        List<String> tempPrice = new ArrayList<String>();
//        List<String> tempDate = new ArrayList<String>();
//
//        for (int i = 0; i < expenseName.length; ++i) {
//            if (expenseCategoryName[i].equals(categoryName)) {
//                tempCategoryColour.add(expenseCategoryColour[i]);
//                tempName.add(expenseName[i]);
//                tempCategoryName.add(expenseCategoryName[i]);
//                tempPrice.add(expensePrice[i]);
//                tempDate.add(expenseDate[i]);
//            }
//        }
//
//        String[] catColour = new String[tempCategoryColour.size()];
//        String[] name = new String[tempName.size()];
//        String[] catName = new String[tempCategoryName.size()];
//        String[] price = new String[tempPrice.size()];
//        String[] date = new String[tempDate.size()];
//
//        catColour = tempCategoryColour.toArray(catColour);
//        name = tempName.toArray(name);
//        catName = tempCategoryName.toArray(catName);
//        price = tempPrice.toArray(price);
//        date = tempDate.toArray(date);
//
//        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(this, catColour, name, catName, price, date);
//        myExpensesListView.setAdapter(expensesListAdapter);

    }
}
