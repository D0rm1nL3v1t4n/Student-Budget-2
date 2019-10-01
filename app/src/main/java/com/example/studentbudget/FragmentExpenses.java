package com.example.studentbudget;


import android.content.Intent;
import android.database.Cursor;
import android.nfc.FormatException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentExpenses extends Fragment {

    View view;
    final int RECENT_EXPENSE_COUNT = 5;
    DatabaseHelper db;

    String[] expenseCategoryColour = new String[RECENT_EXPENSE_COUNT];
    String[] expenseName = new String[RECENT_EXPENSE_COUNT];
    String[] expenseCategoryName = new String[RECENT_EXPENSE_COUNT];
    String[] expensePrice = new String[RECENT_EXPENSE_COUNT];
    String[] expenseDate = new String[RECENT_EXPENSE_COUNT];

    SimpleDateFormat startSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    SimpleDateFormat endSDF = new SimpleDateFormat("dd/MM/yy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_expenses, container, false);
        operations();
        return view;
    }

    private void operations() {
        db = new DatabaseHelper(getActivity());
        initialiseDefaultData();
        getRecentExpenses();
        setupExpensesAdapter();
        addExpenseClickEvent();
        expenseClickEvent();
        viewAllExpensesClickEvent();
    }

    private void getRecentExpenses() {
        String query = "select * from " + DatabaseHelper.TABLE_EXPENSES + " order by " + DatabaseHelper.COL_DATE + " desc limit " + RECENT_EXPENSE_COUNT + ";";
        Cursor expenseData = db.myQuery(query);
        int iterations = RECENT_EXPENSE_COUNT;
        if (expenseData.getCount() < RECENT_EXPENSE_COUNT) {
            iterations = expenseData.getCount();
            initialiseDefaultData();
        }
        for (int i = 0; i < iterations; ++i) {
            expenseData.moveToPosition(i);
            categoryData(expenseData(expenseData, i), i);
        }
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

    private void initialiseDefaultData() {
        for (int i = 0; i < RECENT_EXPENSE_COUNT; ++i) {
            expenseCategoryColour[i] = "";
            expenseName[i] = "None";
            expensePrice[i] = "";
            expenseCategoryName[i] = "";
            expenseDate[i] = "";
        }
    }

    private int getCategoriesCount() {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, DatabaseHelper.COL_NAME);
        return data.getCount();
    }

    private void setupExpensesAdapter() {
        ListView recentExpensesListView = view.findViewById(R.id.recentExpensesListView);
        ExpensesListAdapter expensesListAdapter = new ExpensesListAdapter(getActivity(), expenseCategoryColour, expenseName, expenseCategoryName, expensePrice, expenseDate);
        recentExpensesListView.setAdapter(expensesListAdapter);
    }

    private void addExpenseClickEvent() {
        Button addExpenseButton = view.findViewById(R.id.addExpenseButton);
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCategoriesCount() > 0) {
                    Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getActivity(),"Categories must be created before expenses can be added.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void expenseClickEvent() {
        final ListView expensesListView = view.findViewById(R.id.recentExpensesListView);
        expensesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewExpenseActivity.class);
                intent.putExtra("EXPENSE_NAME", expensesListView.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
    }

    private void viewAllExpensesClickEvent() {
        Button viewAllExpensesButton = view.findViewById(R.id.viewAllExpensesButton);
        viewAllExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = db.searchData(DatabaseHelper.TABLE_EXPENSES, DatabaseHelper.COL_ID);
                if (data.getCount() > 0) {
                    Intent intent = new Intent(getActivity(), MyExpensesActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), "You have no expenses to view.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getRecentExpenses();
        setupExpensesAdapter();
    }
}
