package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddCategoryActivity extends AppCompatActivity {

    int selectedColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        selectedColour = ContextCompat.getColor(AddCategoryActivity.this, R.color.colorPrimary);
        hideCategoryWarningText();
        setSelectedColour();
        setColourEvent();
        saveClickEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void hideCategoryWarningText() {
        TextView categoryWarningTextView = findViewById(R.id.categoryWarningTextView);
        categoryWarningTextView.setVisibility(View.GONE);
    }

    private void setColourEvent() {
        Button setColourButton = findViewById(R.id.setColourButton);
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
        View selectedColourView = findViewById(R.id.categorySelectedColourView);
        selectedColourView.setBackgroundColor(selectedColour);
    }

    private void saveClickEvent() {
        final DatabaseHelper db = new DatabaseHelper(this);
        Button saveButton = findViewById(R.id.categorySaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText categoryNameEditText = findViewById(R.id.categoryNameEditText);
                if (db.searchUnique(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME, categoryNameEditText.getText().toString())) {
                    String hexColour = String.format("#%06X", (0xFFFFFF & selectedColour));
                    db.insertIntoCategories(categoryNameEditText.getText().toString(), hexColour);
                    Toast.makeText(getBaseContext(), "Category saved", Toast.LENGTH_SHORT).show();
                    db.close();
                    finish();
                }
                else {
                    TextView categoryWarningTextView = findViewById(R.id.categoryWarningTextView);
                    categoryWarningTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
