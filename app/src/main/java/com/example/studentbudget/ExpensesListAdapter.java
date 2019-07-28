package com.example.studentbudget;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExpensesListAdapter extends BaseAdapter {

    String[] mColours;
    String[] mNames;
    String[] mCategories;
    float[] mPrices;
    String[] mDates;
    LayoutInflater mInflater;

    public ExpensesListAdapter(Context context, String[] colours, String[] names, String[] categories, float[] prices, String[] dates) {
        mColours = colours;
        mNames = names;
        mCategories = categories;
        mPrices = prices;
        mDates = dates;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mNames.length;
    }

    @Override
    public Object getItem(int i) {
        return mNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.expenses_list_view, null);

        View colouredRectangleView = view.findViewById(R.id.colouredRectangleView);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView categoryTextView = view.findViewById(R.id.categoryTextView);
        TextView priceTextView = view.findViewById(R.id.priceTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);

        if (mColours[i] != "")
            colouredRectangleView.setBackgroundColor(Color.parseColor(mColours[i]));
        nameTextView.setText(mNames[i]);
        categoryTextView.setText(mCategories[i]);
        priceTextView.setText(mPrices[i] + "");
        dateTextView.setText(mDates[i]);

        return view;
    }
}
