package com.example.studentbudget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

public class CategoriesListAdapter extends BaseAdapter {

    String[] mCategoryColours;
    String[] mCategoryNames;
    LayoutInflater mInflater;

    public CategoriesListAdapter(Context context, String[] categoryColours, String[] categoryNames) {
        mCategoryColours = categoryColours;
        mCategoryNames = categoryNames;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mCategoryNames.length;
    }

    @Override
    public Object getItem(int i) {
        return mCategoryNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.categories_list_view, null);

        View categoryColourView = view.findViewById(R.id.categoryColourView);
        TextView categoryNameTextView = view.findViewById(R.id.categoriesNameTextView);

        if (mCategoryColours[i] != "")
            categoryColourView.setBackgroundColor(Color.parseColor(mCategoryColours[i]));
        categoryNameTextView.setText(mCategoryNames[i]);

        return view;
    }
}
