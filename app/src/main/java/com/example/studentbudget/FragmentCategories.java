package com.example.studentbudget;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCategories extends Fragment {

    View view;
    DatabaseHelper db;
    String[] categoryColours;
    String[] categoryNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categories, container, false);
        operations();
        return view;
    }

    private void operations() {
        db = new DatabaseHelper(getActivity());
        getCategoriesData();
        setupCategoriesList();
        addCategoryEvent();
        categoryClickEvent();
    }

    private void getCategoriesData() {
        Cursor data = db.searchData(DatabaseHelper.TABLE_CATEGORIES, "*");
        int categoriesCount = data.getCount();
        if (categoriesCount == 0) {
            categoryColours = new String[1];
            categoryNames = new String[1];
            categoryColours[0] = "";
            categoryNames[0] = "";
            return;
        }
        categoryColours = new String[categoriesCount];
        categoryNames = new String[categoriesCount];
        data.moveToFirst();
        for (int i = 0; i < categoriesCount; ++i) {
            categoryNames[i] = data.getString(1);
            categoryColours[i] = data.getString(2);
            data.moveToNext();
        }
    }

    private void setupCategoriesList() {
        ListView categoriesListView = view.findViewById(R.id.categoriesListView);
        CategoriesListAdapter categoriesListAdapter = new CategoriesListAdapter(getActivity(), categoryColours, categoryNames);
        categoriesListView.setAdapter(categoriesListAdapter);
    }

    private void addCategoryEvent() {
        Button addCategoryButton = view.findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void categoryClickEvent() {
        final ListView categoriesListView = view.findViewById(R.id.categoriesListView);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewCategoryActivity.class);
                intent.putExtra("CATEGORY_NAME", categoriesListView.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCategoriesData();
        setupCategoriesList();
    }
}
