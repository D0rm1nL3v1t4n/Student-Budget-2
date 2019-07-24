package com.example.studentbudget;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBudgetTab extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_budget_tab, null);

        TabLayout tabs = view.findViewById(R.id.tabs);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new SectionsPagerAdapter(getActivity(), getChildFragmentManager()));
        tabs.setupWithViewPager(viewPager);

        return view;
    }

}
