package com.example.studentbudget;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.database.Cursor;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentWeek extends Fragment {

    View view;
    MySharedPreferences sp;
    DatabaseHelper db;
    float budget;
    float expenses;
    int weekNumber;
    Date weekBeginning;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    final int LARGEST_EXPENSES_COUNT = 3;
    String[] expenseName = new String[LARGEST_EXPENSES_COUNT];
    String[] expenseCategoryColour = new String[LARGEST_EXPENSES_COUNT];
    String[] expenseCategoryName = new String[LARGEST_EXPENSES_COUNT];
    String[] expensePrice = new String[LARGEST_EXPENSES_COUNT];
    String[] expenseDate = new String[LARGEST_EXPENSES_COUNT];

    SimpleDateFormat startSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    SimpleDateFormat endSDF = new SimpleDateFormat("dd/MM/yy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week, container, false);
        operations();
        return view;
    }

    private void operations() {
        sp = new MySharedPreferences(getActivity(), MySharedPreferences.PREFERENCE_WEEK_KEY);
        db = new DatabaseHelper(getActivity());
        budget = getBudget(sp);
        expenses = getExpenses(sp);
        weekNumber = getWeekNumber();
        weekBeginning = getWeekBeginning(weekNumber);
        getLargestExpensesData();
        setupListAdapter();
        showData();
        editBudgetEvent();
        viewBudgetHistoryEvent();
    }

    public static float getBudget(MySharedPreferences sp) {
        return sp.readPreferenceData(MySharedPreferences.KEY_BUDGET);
    }

    public static float getExpenses(MySharedPreferences sp) {
        return sp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);
    }

    public static int getWeekNumber() {
        Calendar weekNumCalendar = Calendar.getInstance();
        weekNumCalendar.setTime(new Date());
        return weekNumCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static Date getWeekBeginning(int weekNumber) {
        Calendar weekStartCalendar = Calendar.getInstance();
        weekStartCalendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
        weekStartCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        weekStartCalendar.set(Calendar.DAY_OF_WEEK, 2);
        return weekStartCalendar.getTime();
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

    private void getLargestExpensesData() {
        Date nextWeekStart = getWeekBeginning(weekNumber + 1);
        initialiseDefaultData();
        String query = "select * from " + DatabaseHelper.TABLE_EXPENSES + " order by " + DatabaseHelper.COL_PRICE + " desc;";
        Cursor expenseData = db.myQuery(query);

        int count = 0;
        for (int i = 0; i < expenseData.getCount(); ++i) {
            expenseData.moveToPosition(i);
            Date date;
            try {
                date =  startSDF.parse(expenseData.getString(4));
                if (date.compareTo(weekBeginning) >= 0 && date.compareTo(nextWeekStart) < 0) {
                    getCategoryData(getExpenseData(expenseData, count), count);
                    ++count;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (count == 3)
                return;
        }
    }

    private int getExpenseData(Cursor expenseData, int i) {
        expenseName[i] = expenseData.getString(1);
        expensePrice[i] = "£" + expenseData.getFloat(2);
        try {
            expenseDate[i] = endSDF.format(startSDF.parse(expenseData.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return expenseData.getInt(3);
    }

    private void getCategoryData(int categoryId, int i) {
        Cursor categoryData = db.searchData(DatabaseHelper.TABLE_CATEGORIES, "*", DatabaseHelper.COL_ID, categoryId);
        categoryData.moveToFirst();
        expenseCategoryName[i] = categoryData.getString(1);
        expenseCategoryColour[i] = categoryData.getString(2);
    }

    private void setupListAdapter() { ;
        ListView largestExpensesListView = view.findViewById(R.id.weekLargestExpensesListView);
        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(getActivity(), expenseCategoryColour, expenseName, expenseCategoryName, expensePrice, expenseDate);
        largestExpensesListView.setAdapter(expensesListAdapter);
    }

    private void showData() {
        TextView weekHeadingTextView = view.findViewById(R.id.weekBudgetTabHeadingTextView);
        ProgressBar budgetProgressBar = view.findViewById(R.id.weekBudgetPBar);
        TextView budgetPercentageTextView = view.findViewById(R.id.weekBudgetPercentageTextView);
        TextView expensesValueTextView = view.findViewById(R.id.weekExpensesValueTextView);
        TextView budgetValueTextView = view.findViewById(R.id.weekBudgetValueTextView);

        weekHeadingTextView.setText("Week " + weekNumber + " " + sdf.format(weekBeginning));
        float budgetPercentage = expenses * 100 / budget;
        if (expenses == 0 && budget == 0)
            budgetPercentage = 0;
        budgetProgressBar.setProgress(Math.round(budgetPercentage));
        budgetPercentageTextView.setText(Math.round(10 * budgetPercentage) / 10 + "%");
        expensesValueTextView.setText("£" + expenses);
        budgetValueTextView.setText("£" + budget);
    }

    private void editBudgetEvent() {
        Button editWeeklyBudgetButton = view.findViewById(R.id.weekEditBudgetButton);
        editWeeklyBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditBudgetActivity.class);
                intent.putExtra("EDIT_BUDGET_TYPE", "WEEK");
                startActivity(intent);
            }
        });
    }

    private void viewBudgetHistoryEvent() {
        Cursor data = db.searchData(DatabaseHelper.TABLE_WEEKLY_BUDGET_HISTORY, "*");
        final int count = data.getCount();
        Button viewBudgetHistoryButton = view.findViewById(R.id.weeklyBudgetHistoryButton);
        viewBudgetHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 1) {
                    Toast.makeText(getActivity(), "No weekly history data found.", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), BudgetHistoryActivity.class);
                    intent.putExtra("BUDGET_HISTORY_TYPE", "WEEK");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        budget = getBudget(sp);
        expenses = getExpenses(sp);
        weekNumber = getWeekNumber();
        weekBeginning = getWeekBeginning(weekNumber);
        getLargestExpensesData();
        setupListAdapter();
        showData();
    }
}
