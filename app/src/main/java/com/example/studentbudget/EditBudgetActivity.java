package com.example.studentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditBudgetActivity extends AppCompatActivity {

    Boolean isWeek;
    MySharedPreferences sp;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String editType = intent.getStringExtra("EDIT_BUDGET_TYPE");
        Log.d("EDIT BUDGET TYPE", editType);
        if (editType.equals("WEEK"))
            isWeek = true;
        else
            isWeek = false;
        setHeading();
        float budget = getBudgetData();
        setBudgetData(budget);
        saveBudgetData();
    }

    private void setHeading() {
        TextView editBudgetHeadingTextView = findViewById(R.id.editBudgetHeadingTextView);
        if (isWeek)
            editBudgetHeadingTextView.setText("Edit Weekly Budget");
        else
            editBudgetHeadingTextView.setText("Edit Monthly Budget");
    }

    private float getBudgetData() {
        String preferenceKey;
        if (isWeek)
            preferenceKey = MySharedPreferences.PREFERENCE_WEEK_KEY;
        else
            preferenceKey = MySharedPreferences.PREFERENCE_MONTH_KEY;
        sp = new MySharedPreferences(this, preferenceKey);
        return sp.readPreferenceData(MySharedPreferences.KEY_BUDGET);
    }

    private void setBudgetData(float budget) {
        TextView currentBudgetTextView = findViewById(R.id.currentBudgetTextView);
        if (isWeek)
            currentBudgetTextView.setText("My current weekly budget: £" + budget);
        else
            currentBudgetTextView.setText("My current monthly budget: £" + budget);
    }

    private void saveBudgetData() {
        Button saveBudgetButton = findViewById(R.id.saveBudgetButton);
        saveBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newBudgetEditText = findViewById(R.id.newBudgetEditText);
                float newBudget = Integer.parseInt(newBudgetEditText.getText().toString());
                sp.writeBudgetData(newBudget);
                Toast.makeText(getBaseContext(), "Budget has been saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
