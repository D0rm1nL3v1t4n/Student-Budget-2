package com.example.studentbudget;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.provider.ContactsContract;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseHelper db = new DatabaseHelper(this);
        checkNewWeek(db);
        checkNewMonth(db);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentBudgetTab()).commit();
    }

    private void checkNewWeek(DatabaseHelper db) {
        Calendar calendar = Calendar.getInstance();
        int currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        Cursor data = db.searchData(DatabaseHelper.TABLE_WEEKLY_BUDGET_HISTORY, DatabaseHelper.COL_WEEK_NUMBER);
        if (data.getCount() == 0) {
            writeInitialWeekData(db, currentWeekNumber);
            return;
        }
        data.moveToLast();
        int weekNum = data.getInt(0);
        if (weekNum + 2 == currentWeekNumber || (weekNum == 51 && currentWeekNumber == 1) || (weekNum == 52 && currentWeekNumber == 2)) {
            MySharedPreferences sp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_WEEK_KEY);
            float budget = sp.readPreferenceData(MySharedPreferences.KEY_BUDGET);
            float expenses = sp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);
            Date weekBeginning = FragmentWeek.getWeekBeginning(currentWeekNumber - 1);
            db.insertIntoWeeklyBudgetHistory(budget, expenses, weekBeginning, currentWeekNumber - 1);
            sp.resetSharedPreferenceData();
        }
    }

    private void checkNewMonth(DatabaseHelper db) {
        Calendar calendar = Calendar.getInstance();
        int currentMonthNumber = calendar.get(Calendar.MONTH) + 1;
        Cursor data = db.searchData(DatabaseHelper.TABLE_MONTHLY_BUDGET_HISTORY, DatabaseHelper.COL_MONTH);
        if (data.getCount() == 0) {
            writeInitialMonthData(db, currentMonthNumber - 1);
            return;
        }
        data.moveToLast();
        int monthNum = data.getInt(0) + 1;
        if (monthNum + 2 == currentMonthNumber || (monthNum == 11 && currentMonthNumber == 1) || (monthNum == 12 && currentMonthNumber == 2)) {
            MySharedPreferences sp = new MySharedPreferences(this, MySharedPreferences.PREFERENCE_MONTH_KEY);
            float budget = sp.readPreferenceData(MySharedPreferences.KEY_BUDGET);
            float expenses = sp.readPreferenceData(MySharedPreferences.KEY_EXPENSES);

            int dataMonthNum;
            if (currentMonthNumber > 1)
                dataMonthNum = currentMonthNumber - 2;
            else
                dataMonthNum = 11;
            db.insertIntoMonthlyBudgetHistory(budget, expenses, dataMonthNum);
            sp.resetSharedPreferenceData();
        }
    }

    private void writeInitialWeekData(DatabaseHelper db, int currentWeekNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(FragmentWeek.getWeekBeginning(currentWeekNumber));
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        Date initialWeekBeginning = calendar.getTime();
        int initialWeekNumber = currentWeekNumber - 1;

        db.insertIntoWeeklyBudgetHistory(-1, -1, initialWeekBeginning, initialWeekNumber);
    }

    private void writeInitialMonthData(DatabaseHelper db, int currentMonthNumber) {
        int initialMonth;
        if (currentMonthNumber > 0)
            initialMonth = currentMonthNumber - 1;
        else
            initialMonth = 11;
        db.insertIntoMonthlyBudgetHistory(-1, -1, initialMonth);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_budget) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentBudgetTab()).commit();
        } else if (id == R.id.nav_expenses) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentExpenses()).commit();
        } else if (id == R.id.nav_categories) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentCategories()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
