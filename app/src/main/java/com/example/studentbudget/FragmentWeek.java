package com.example.studentbudget;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWeek extends Fragment {

    View view;
    private MySharedPreferences sp;
    private float budget;
    private float expenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week, container, false);
        operations();
        return view;
    }

    private void operations() {
        sp = new MySharedPreferences(getActivity(), MySharedPreferences.PREFERENCE_WEEK_KEY);
        getData();
        showData();
    }

    private void getData() {
        budget = sp.readPreferenceData(MySharedPreferences.KEY_BUDGET);
        expenses = sp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);
    }

    private void showData() {
        ProgressBar budgetProgressBar = view.findViewById(R.id.weekBudgetPBar);
        TextView budgetPercentageTextView = view.findViewById(R.id.weekBudgetPercentageTextView);
        TextView expensesValueTextView = view.findViewById(R.id.weekExpensesValueTextView);
        TextView budgetValueTextView = view.findViewById(R.id.weekBudgetValueTextView);

        float budgetPercentage = expenses * 100 / budget;
        budgetProgressBar.setProgress(Math.round(budgetPercentage));
        budgetPercentageTextView.setText(Math.round(10 * budgetPercentage) / 10 + "%");
        expensesValueTextView.setText("£" + expenses);
        budgetValueTextView.setText("£" + budget);
    }

}
