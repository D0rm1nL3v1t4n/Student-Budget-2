package com.example.studentbudget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {


    Context mContext;

    public static final String DATABASE_NAME = "StudentBudget.db";

    public static final String TABLE_WEEKLY_BUDGET_HISTORY = "weekly_budget_history";
    public static final String TABLE_MONTHLY_BUDGET_HISTORY = "monthly_budget_history";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_EXPENSES = "expenses";

    public static final String COL_ID = "id";
    public static final String COL_BUDGET = "budget";
    public static final String COL_EXPENSES = "expenses";
    public static final String COL_WEEK_NUMBER = "week_number";
    public static final String COL_WEEK_BEGINNING = "week_begining";
    public static final String COL_MONTH = "month";
    public static final String COL_NAME = "name";
    public static final String COL_COLOUR = "colour";
    public static final String COL_PRICE = "price";
    public static final String COL_CATEGORY_ID = "category_id";
    public static final String COL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 5);
        mContext = context;
    }

    /*
        tableName - the name of the table that the data being searched for exists in
        returnCol - the data from this column will be returned once the data is found
            --> this can be more than one by listing the columns "col1, col2, col3" or all *
        searchCol - the column in the table that will have data compared to
        searchData - the data which is being searched for
    */

    @Override
    public void onCreate(SQLiteDatabase db) {
        createWeeklyBudgetHistory(db);
        createMonthlyBudgetHistory(db);
        createCategories(db);
        createExpenses(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS BudgetTerm");

        //resetDatabase(db);

    }

    private void resetDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY_BUDGET_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEEKLY_BUDGET_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        createWeeklyBudgetHistory(db);
        createMonthlyBudgetHistory(db);
        createCategories(db);
        createExpenses(db);
    }

    public int getMaxId(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxIdResult = db.rawQuery("select max(Id) from " + tableName, null);
        maxIdResult.moveToFirst();
        if (maxIdResult.getString(0) == null)
            return 0;
        return Integer.parseInt(maxIdResult.getString(0));
    }

    public Cursor searchData(String tableName, String returnCol, String searchCol, Object searchData) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select " + returnCol + " from " + tableName + " where " + searchCol + " = " + searchData, null);
    }

//    public Cursor searchData(String tableName, String returnCol, String searchCol, int searchData) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.rawQuery("select " + returnCol + " from " + tableName + " where " + searchCol + " = " + searchData, null);
//    }

    public Cursor searchData(String tableName, String returnCol) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select " + returnCol + " from " + tableName, null);
    }

    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + tableName);
    }

    public void updateData(String tableName, String updateCol, String newData, String searchCol, Object searchData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + tableName + " SET " + updateCol + " = '" + newData + "' WHERE " + searchCol + " = " + searchData);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// Functions to insert data ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void insertIntoWeeklyBudgetHistory(float budget, float expenses, Date weekBeginning, int weekNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_WEEKLY_BUDGET_HISTORY) + 1;
        db.execSQL("INSERT INTO " + TABLE_WEEKLY_BUDGET_HISTORY + " VALUES (" + maxId + ", " + budget + ", " + expenses + ", " + weekBeginning + ", " + weekNumber + ");");
    }

    public void insertIntoMonthlyBudgetHistory(float budget, float expenses, String month) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_MONTHLY_BUDGET_HISTORY) + 1;
        db.execSQL("INSERT INTO " + TABLE_MONTHLY_BUDGET_HISTORY + " VALUES (" + maxId + ", " + budget + ", " + expenses + ",  '" + month + "');");
    }

    public void insertIntoCategories(String name, String colour) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_CATEGORIES) + 1;
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " VALUES (" + maxId + ", '" + name + "', '" + colour + "');");
    }

    public void insertIntoExpenses(String name, float price, int catId, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        int maxId = getMaxId(TABLE_EXPENSES) + 1;
        db.execSQL("INSERT INTO " + TABLE_EXPENSES + " VALUES (" + maxId + ", '" + name + "', " + price + ", " + catId + ", " + date + ");");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// Functions to create tables //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void createWeeklyBudgetHistory(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_WEEKLY_BUDGET_HISTORY + " ( " +
                COL_ID + " integer primary key, " +
                COL_BUDGET + " float, " +
                COL_EXPENSES + " float, " +
                COL_WEEK_BEGINNING + " date, " +
                COL_WEEK_NUMBER + " integer );");
    }

    private void createMonthlyBudgetHistory(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MONTHLY_BUDGET_HISTORY + " ( " +
                COL_ID + " integer primary key, " +
                COL_BUDGET + " float, " +
                COL_EXPENSES + " float, " +
                COL_MONTH + " text );");
    }

    private void createCategories(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CATEGORIES + " ( " +
                COL_ID + " integer primary key, " +
                COL_NAME + " text, " +
                COL_COLOUR + " text);");
    }

    private void createExpenses(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_EXPENSES + " ( " +
                COL_ID + " integer primary key, " +
                COL_NAME + " text, " +
                COL_PRICE + " float, " +
                COL_CATEGORY_ID + " integer, " +
                COL_DATE + " date );");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////


    public Cursor myQuery(String myQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery(myQuery, null);
        return data;
    }

}
