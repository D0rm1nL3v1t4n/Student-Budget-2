package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddCategoryActivity extends AppCompatActivity {

    int selectedColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedColour = ContextCompat.getColor(AddCategoryActivity.this, R.color.colorPrimary);
        setSelectedColour();
        setColourEvent();
        saveClickEvent();
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
                TextView categoryNameEditText = findViewById(R.id.categoryNameEditText);
                String hexColour = String.format("#%06X", (0xFFFFFF & selectedColour));
                db.insertIntoCategories(categoryNameEditText.getText().toString(), hexColour);
                Toast.makeText(getBaseContext(), "Category saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
;
    }
}
