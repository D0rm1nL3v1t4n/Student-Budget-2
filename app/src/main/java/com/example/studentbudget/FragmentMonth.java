package com.example.studentbudget;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormatSymbols;

public class FragmentMonth extends Fragment {

    View view;
    MySharedPreferences sp;
    DatabaseHelper db;
    float budget;
    float expenses;
    String month;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    final int LARGEST_EXPENSES_COUNT = 3;
    String[] expenseName = new String[LARGEST_EXPENSES_COUNT];
    String[] expenseCategoryColour = new String[LARGEST_EXPENSES_COUNT];
    String[] expenseCategoryName = new String[LARGEST_EXPENSES_COUNT];
    String[] expensePrice = new String[LARGEST_EXPENSES_COUNT];
    String[] expenseDate = new String[LARGEST_EXPENSES_COUNT];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_month, container, false);
        operations();
        return view;
    }

    private void operations() {
        sp = new MySharedPreferences(getActivity(), MySharedPreferences.PREFERENCE_MONTH_KEY);
        db = new DatabaseHelper(getActivity());
        budget = getBudget();
        expenses = getExpenses();
        month = getMonth();
        initialiseDefaultData();
        getLargestExpenses();
        setupListAdapter();
        showData();
        editBudgetEvent();
        viewBudgetHistoryEvent();
    }

    private float getBudget() {
        return sp.readPreferenceData(MySharedPreferences.KEY_BUDGET);

    }

    private float getExpenses() {
        return sp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);
    }

    private String getMonth() {
        Calendar monthNumberCalendar = Calendar.getInstance();
        monthNumberCalendar.setTime(new Date());
//        switch (monthNumberCalendar.get(Calendar.MONTH)) {
//            case 0:
//                return "January";
//            case 1:
//                return "February";
//            case 2:
//                return "March";
//            case 3:
//                return "April";
//            case 4:
//                return "May";
//            case 5:
//                return "June";
//            case 6:
//                return "July";
//            case 7:
//                return "August";
//            case 8:
//                return "September";
//            case 9:
//                return "October";
//            case 10:
//                return "November";
//            case 11:
//                return "December";
//        }
//        return null;
        return new DateFormatSymbols().getMonths()[monthNumberCalendar.get(Calendar.MONTH)];
    }

    private void initialiseDefaultData() {
        for (int i = 0; i < LARGEST_EXPENSES_COUNT; ++i) {
            expenseCategoryColour[i] = "";
            expenseName[i] = "None";
            expensePrice[i] = "";
            expenseCategoryName[i] = "";
            expenseDate[i] = "";
        }
    }

    private void getLargestExpenses() {
        String dateToday = sdf.format(new Date());
        String[] dateTodayArray = dateToday.split("/");
        String dateMonthStart = "01/" + dateTodayArray[1] + "/" + dateTodayArray[2];
        String query = "select * from " + DatabaseHelper.TABLE_EXPENSES + " where " + DatabaseHelper.COL_DATE +
                " between date('" + dateToday + "') and date('" + dateMonthStart + "') order by " + DatabaseHelper.COL_PRICE + " desc limit " + LARGEST_EXPENSES_COUNT + ";" ;
        Cursor expenseData = db.myQuery(query);
        expenseData.moveToFirst();
        int iterationCount = LARGEST_EXPENSES_COUNT;
        if (expenseData.getCount() < 3)
            iterationCount = expenseData.getCount();
        for (int i = 0; i < iterationCount; ++i) {
            getCategoryData(getExpenseData(expenseData, i), i);
            expenseData.moveToNext();
        }
    }

    private int getExpenseData(Cursor expenseData, int i) {
        expenseName[i] = expenseData.getString(1);
        expensePrice[i] = "£" + expenseData.getFloat(2);
        expenseDate[i] = expenseData.getString(4);
        return expenseData.getInt(3);
    }

    private void getCategoryData(int categoryId, int i) {
        Cursor categoryData = db.searchData(DatabaseHelper.TABLE_CATEGORIES, "*", DatabaseHelper.COL_ID, categoryId);
        categoryData.moveToFirst();
        expenseCategoryName[i] = categoryData.getString(1);
        expenseCategoryColour[i] = categoryData.getString(2);
    }

    private void setupListAdapter() {
        ListView largestExpensesListView = view.findViewById(R.id.monthLargestExpensesListView);
        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(getActivity(), expenseCategoryColour, expenseName, expenseCategoryName, expensePrice, expenseDate);
        largestExpensesListView.setAdapter(expensesListAdapter);
    }

    private void showData() {
        TextView monthHeadingTextView = view.findViewById(R.id.monthBudgetTabHeadingTextView);
        ProgressBar budgetProgressBar = view.findViewById(R.id.monthBudgetPBar);
        TextView budgetPercentageTextView = view.findViewById(R.id.monthBudgetPercentageTextView);
        TextView expensesValueTextView = view.findViewById(R.id.monthExpensesValueTextView);
        TextView budgetValueTextView = view.findViewById(R.id.monthBudgetValueTextView);

        monthHeadingTextView.setText(month + " budget");
        float budgetPercentage = expenses * 100 / budget;
        budgetProgressBar.setProgress(Math.round(budgetPercentage));
        budgetPercentageTextView.setText(Math.round(10 * budgetPercentage) / 10 + "%");
        expensesValueTextView.setText("£" + expenses);
        budgetValueTextView.setText("£" + budget);
    }

    private void editBudgetEvent() {
        Button editBudgetButton = view.findViewById(R.id.monthEditBudgetButton);
        editBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditBudgetActivity.class);
                intent.putExtra("EDIT_BUDGET_TYPE", "MONTH");
                startActivity(intent);
            }
        });
    }

    private void viewBudgetHistoryEvent() {
        Cursor data = db.searchData(DatabaseHelper.TABLE_MONTHLY_BUDGET_HISTORY, "*");
        final int count = data.getCount();
        Button viewBudgetHistoryButton = view.findViewById(R.id.monthlyBudgetHistoryButton);
        viewBudgetHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 1) {
                    Toast.makeText(getActivity(), "No monthly history data found.", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), BudgetHistoryActivity.class);
                    intent.putExtra("BUDGET_HISTORY_TYPE", "MONTH");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        budget = getBudget();
        expenses = getExpenses();
        month = getMonth();
        initialiseDefaultData();
        getLargestExpenses();
        setupListAdapter();
        showData();
    }
}
