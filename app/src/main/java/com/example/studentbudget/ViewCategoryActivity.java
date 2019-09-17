package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ViewCategoryActivity extends AppCompatActivity {

    DatabaseHelper db;
    int categoryId;
    String categoryColour;
    int selectedColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);

        String categoryName = getIntentData();
        db = new DatabaseHelper(this);
        getCategoryData(categoryName);
        setCategoryData(categoryName);
        toggleEdit(false);

        setColourEvent();
        editCategoryEvent();
        cancelCategoryEditEvent();
        saveCategoryEvent();
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
        return intent.getStringExtra("CATEGORY_NAME");
    }

    private void getCategoryData(String categoryName) {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, "*", DatabaseHelper.COL_NAME, categoryName);
        data.moveToFirst();
        categoryId = data.getInt(0);
        categoryColour = data.getString(2);
        selectedColour = Color.parseColor(categoryColour);
    }

    private void setCategoryData(String categoryName) {
        EditText categoryNameEditText = findViewById(R.id.viewCategoryNameEditText);
        View categoryColourView = findViewById(R.id.viewCategorySelectedColourView);
        categoryNameEditText.setText(categoryName);
        categoryColourView.setBackgroundColor(Color.parseColor(categoryColour));
    }

    private void toggleEdit(Boolean editState) {
        //do I want to find these views every time I change the edit state of the activity?
        // potential solution 1 --> add views as parameters
        // potential solution 2 --> set views to be 'globals' defined in onCreate
        EditText categoryNameEditText = findViewById(R.id.viewCategoryNameEditText);
        Button categorySetColourButton = findViewById(R.id.viewCategorySetColourButton);

        Button editButton = findViewById(R.id.editCategoryButton);
        Button saveChangesButton = findViewById(R.id.saveCategoryChangesButton);
        Button cancelEditButton = findViewById(R.id.cancelCategoryEditButton);

        categoryNameEditText.setEnabled(editState);
        categorySetColourButton.setEnabled(editState);
        if (editState) {
            cancelEditButton.setVisibility(View.VISIBLE);
            saveChangesButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.INVISIBLE);
        }
        else {
            cancelEditButton.setVisibility(View.INVISIBLE);
            saveChangesButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }
    }

    private void editCategoryEvent() {
        Button editCategoryButton = findViewById(R.id.editCategoryButton);
        editCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEdit(true);
            }
        });
    }

    private void cancelCategoryEditEvent() {
        Button cancelCategoryEditButton = findViewById(R.id.cancelCategoryEditButton);
        cancelCategoryEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEdit(false);
            }
        });
    }

    private void saveCategoryEvent() {
        Button saveChangesButton = findViewById(R.id.saveCategoryChangesButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { EditText categoryNameEditText = findViewById(R.id.viewCategoryNameEditText);
               String hexColour = String.format("#%06X", (0xFFFFFF & selectedColour));
               db.updateData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME, categoryNameEditText.getText().toString(), DatabaseHelper.COL_ID, categoryId);
               db.updateData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_COLOUR, hexColour, DatabaseHelper.COL_ID, categoryId);
               finish();
            }
        });
    }

    private void setColourEvent() {
        Button setColourButton = findViewById(R.id.viewCategorySetColourButton);
        setColourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColourPicker();
            }
        });
    }

    private void openColourPicker() {
        AmbilWarnaDialog colourPicker = new AmbilWarnaDialog(this, selectedColour, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}
            @Override
            public void onOk(AmbilWarnaDialog dialog, int colour) {
                selectedColour = colour;
                setSelectedColour();
            }
        });
        colourPicker.show();
    }

    private void setSelectedColour() {
        View selectedColourView = findViewById(R.id.viewCategorySelectedColourView);
        selectedColourView.setBackgroundColor(selectedColour);
    }
}
