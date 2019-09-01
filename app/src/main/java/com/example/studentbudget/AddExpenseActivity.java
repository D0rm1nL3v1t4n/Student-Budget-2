package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
    Boolean today = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        categories = getAllCategories();
        setupCategoriesSpinner();
        toggleDateSpinnerVisibility();
        saveEvent();
    }

    private String[] getAllCategories() {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME);
        data.moveToFirst();
        String[] myCategories = new String[data.getCount()];
        for (int i = 0; i < data.getCount(); ++i) {
            myCategories[i] = data.getString(0);
            data.moveToNext();
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
                        expenseDate = simpleDateFormat.parse(expenseDateDP.getDayOfMonth() + "/" + expenseDateDP.getMonth() + 1 + "/" + expenseDateDP.getYear());
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                int categoryId = getCategoryId(expenseCategorySpinner.getSelectedItem().toString());
                db.insertIntoExpenses(expenseNameEditText.getText().toString(), Float.parseFloat(expensePriceEditText.getText().toString()), categoryId, expenseDate);
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
