package com.example.studentbudget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BudgetHistoryListAdapter extends BaseAdapter {

    Context mContext;
    String[] mPrimaryDateInfo;
    String[] mSecondaryDateInfo;
    float[] mBudgetValues;
    float[] mExpensesValues;
    LayoutInflater mInflater;

    public BudgetHistoryListAdapter(Context context, String[] primaryDateInfo, String[] secondaryDateInfo, float[] budgetValues, float[] expensesValues) {
        mContext = context;
        mPrimaryDateInfo = primaryDateInfo;
        mSecondaryDateInfo = secondaryDateInfo;
        mBudgetValues = budgetValues;
        mExpensesValues = expensesValues;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPrimaryDateInfo.length;
    }

    @Override
    public Object getItem(int i) {
        return mPrimaryDateInfo[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.budget_history_list_view, null);

        TextView primaryDateInfo = view.findViewById(R.id.primaryBudgetDateInfoTextView);
        TextView secondaryDateInfo = view.findViewById(R.id.secondaryBudgetDateInfoTextView);
        TextView budgetValue = view.findViewById(R.id.budgetHistoryBudgetValTextView);
        TextView expensesValue = view.findViewById(R.id.budgetHistoryExpensesValTextView);
        TextView percentage = view.findViewById(R.id.budgetHistoryBudgetPercentageTextView);

        if (mBudgetValues[i] != -1) {
            primaryDateInfo.setText(mPrimaryDateInfo[i]);
            secondaryDateInfo.setText(mSecondaryDateInfo[i]);
            budgetValue.setText("£" + mBudgetValues[i]);
            expensesValue.setText("£" + mExpensesValues[i]);
            float budgetPercentageValue = Math.round(mExpensesValues[i] * 1000.0 / mBudgetValues[i]) / 10;
            percentage.setText(budgetPercentageValue + "%");
        }

        return view;
    }
}
