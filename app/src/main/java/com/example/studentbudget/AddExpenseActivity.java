package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {

    DatabaseHelper db;
    String[] categories;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Boolean today = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        db = new DatabaseHelper(this);
        categories = getAllCategories(db);
        setupCategoriesSpinner();
        toggleDateSpinnerVisibility();
        saveEvent();
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
        String[] myCategories = new String[data.getCount()];
        for (int i = 0; i < data.getCount(); ++i) {
            data.moveToPosition(i);
            myCategories[i] = data.getString(0);
        }
        return myCategories;
    }

    private void setupCategoriesSpinner() {
        Spinner categoriesSpinner = findViewById(R.id.expenseCategorySpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.categories_spinner, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(arrayAdapter);
    }

    private void toggleDateSpinnerVisibility() {
        final CheckBox dateCheckBox = findViewById(R.id.expenseTodayCheckBox);
        dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatePicker expenseDateDP = findViewById(R.id.expenseDateDP);
                if (isChecked) {
                    today = true;
                    expenseDateDP.setVisibility(View.GONE);
                }
                else {
                    today = false;
                    expenseDateDP.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void saveEvent() {
        final MySharedPreferences weekSp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_WEEK_KEY);
        final MySharedPreferences monthSp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_MONTH_KEY);
        Button saveButton = findViewById(R.id.expenseSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText expenseNameEditText = findViewById(R.id.expenseNameEditText);
                EditText expensePriceEditText = findViewById(R.id.expensePriceEditText);
                Spinner expenseCategorySpinner = findViewById(R.id.expenseCategorySpinner);

                Date expenseDate = new Date();
                if (!today) {
                    DatePicker expenseDateDP = findViewById(R.id.expenseDateDP);
                    try {
                        int month = expenseDateDP.getMonth() + 1;
                        expenseDate = sdf.parse(expenseDateDP.getDayOfMonth() + "/" + month + "/" + expenseDateDP.getYear());
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                int categoryId = getCategoryId(expenseCategorySpinner.getSelectedItem().toString());
                float expenseValue = Float.parseFloat(expensePriceEditText.getText().toString());
                db.insertIntoExpenses(expenseNameEditText.getText().toString(), expenseValue, categoryId, expenseDate);
                float totalWeekExpenses = weekSp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);
                float totalMonthExpenses = monthSp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);
                weekSp.writeExpensesData(totalWeekExpenses + expenseValue);
                monthSp.writeExpensesData(totalMonthExpenses + expenseValue);
                Toast.makeText(getBaseContext(), "Expense saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private int getCategoryId(String selectedCategory) {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_ID, DatabaseHelper.COL_NAME, selectedCategory);
        data.moveToFirst();
        return data.getInt(0);
    }
}