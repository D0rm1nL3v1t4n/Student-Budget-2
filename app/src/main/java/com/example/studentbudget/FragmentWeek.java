package com.example.studentbudget;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.database.Cursor;

import androidx.fragment.app.Fragment;

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
    float[] expensePrice = new float[LARGEST_EXPENSES_COUNT];
    String[] expenseDate = new String[LARGEST_EXPENSES_COUNT];

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
        initialiseEmptyData();
        getLargestExpensesData();
        setupListAdapter();
        showData();
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
        return weekNumCalendar.get(Calendar.WEEK_OF_MONTH);
    }

    public static Date getWeekBeginning(int weekNumber) {
        Calendar weekStartCalendar = Calendar.getInstance();
        weekStartCalendar.set(Calendar.WEEK_OF_YEAR, weekNumber);
        weekStartCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        weekStartCalendar.set(Calendar.DAY_OF_WEEK, 2);
        return weekStartCalendar.getTime();
    }

    private void initialiseEmptyData() {
        for (int i = 0; i < LARGEST_EXPENSES_COUNT; ++i) {
            expenseCategoryColour[i] = "";
            expenseName[i] = "None";
            expensePrice[i] = 0;
            expenseCategoryName[i] = "";
            expenseDate[i] = "";
        }
    }

    private void getLargestExpensesData() {
        String dateToday = sdf.format(new Date());
        String dateWeekStart = sdf.format(weekBeginning);
        String query = "select * from " + DatabaseHelper.TABLE_EXPENSES + " where " + DatabaseHelper.COL_DATE +
                " between date('" + dateToday + "') and date('" + dateWeekStart + "') order by " + DatabaseHelper.COL_PRICE + ";" ;
        Cursor expenseData = db.myQuery(query);
        expenseData.moveToFirst();
        int iterationCount = LARGEST_EXPENSES_COUNT;
        if (expenseData.getCount() < 3)
            iterationCount = expenseData.getCount();
        int categoryId;
        for (int i = 0; i < iterationCount; ++i) {
            categoryId = getExpenseData(expenseData, i);
            Cursor categoryData = db.searchData(DatabaseHelper.TABLE_CATEGORIES, "*", DatabaseHelper.COL_ID, categoryId);
            getCategoryData(categoryData, i);
            expenseData.moveToNext();
        }
    }

    private int getExpenseData(Cursor expenseData, int i) {
        expenseName[i] = expenseData.getString(1);
        expensePrice[i] = expenseData.getFloat(2);
        expenseDate[i] = expenseData.getString(4);
        return expenseData.getInt(3);
    }

    private void getCategoryData(Cursor categoryData, int i) {
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

        weekHeadingTextView.setText("Week (" + weekNumber + ") beginning " + weekBeginning + " budget");
        float budgetPercentage = expenses * 100 / budget;
        if (expenses == 0 && budget == 0)
            budgetPercentage = 0;
        budgetProgressBar.setProgress(Math.round(budgetPercentage));
        budgetPercentageTextView.setText(Math.round(10 * budgetPercentage) / 10 + "%");
        expensesValueTextView.setText("£" + expenses);
        budgetValueTextView.setText("£" + budget);
    }

}
