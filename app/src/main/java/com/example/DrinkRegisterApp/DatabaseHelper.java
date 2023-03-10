package com.example.DrinkRegisterApp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "drink-register-app.db";
    private static final int DATABASE_VERSION = 1;
    private final MainActivity app;

    public DatabaseHelper(MainActivity app) {
        super(app, DATABASE_NAME, null, DATABASE_VERSION);
        this.app = app;
    }

    //------------------------- DB Commands ---------------------------------------//

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlUsers = "CREATE TABLE users (id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, group_ TEXT, rank TEXT, balance INTEGER, pin_code INTEGER)";
        db.execSQL(sqlUsers);
        String sqlLog = "CREATE TABLE log (id INTEGER PRIMARY KEY, user_1 TEXT, user_2 TEXT, action_ TEXT, amount INTEGER, date TEXT)";
        db.execSQL(sqlLog);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades as needed
    }

    public void emptyDb() {
        SQLiteDatabase db = getWritableDatabase();
        String sqlUsers = "DELETE FROM users";
        db.execSQL(sqlUsers);
        String sqlLog = "DELETE FROM log";
        sqlLog = "DROP TABLE log";
        db.execSQL(sqlLog);
        String sqlog = "CREATE TABLE log (id INTEGER PRIMARY KEY, user_1 TEXT, user_2 TEXT, action_ TEXT, amount INTEGER, date TEXT)";
        db.execSQL(sqlog);
        app.finish();
        app.startActivity(app.getIntent());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void exportDB(String fileName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        CSVWriter csvWrite;
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/backup");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, fileName);
            file.createNewFile();
            csvWrite = new CSVWriter(new FileWriter(file));
        } catch (Exception e) {
            return;
        }
        String firstName = app.getResources().getString(R.string.first_name);
        String lastName = app.getResources().getString(R.string.last_name);
        String balance = app.getResources().getString(R.string.balance_title);
        csvWrite.writeNext(new String[]{firstName, lastName, balance});
        while (cursor.moveToNext()) {
            String[] rowData = {cursor.getString(1), cursor.getString(2), cursor.getString(5)};
            csvWrite.writeNext(rowData);
        }
        csvWrite.close();
        cursor.close();
    }



    //------------------------- Handles users ---------------------------------------//

    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("group_", user.getGroup());
        values.put("rank", user.getRank());
        values.put("balance", user.getBalance());
        values.put("pin_code", user.getPinCode());
        db.insert("users", null, values);
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM users";
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String group = cursor.getString(3);
            String rank = cursor.getString(4);
            int balance = cursor.getInt(5);
            int pinCode = cursor.getInt(6);

            User user = new User(id, firstName, lastName, group, rank, balance, pinCode);
            users.add(user);
        }
        cursor.close();
        return users;
    }

    public List<String> getBalances() {
        List<String> balances = new ArrayList<>();
        int total = 0;

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM users";
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            int balance = cursor.getInt(5);

            balances.add('\n' + firstName + " " + lastName + " : " + balance);
            total += balance;
        }
        cursor.close();
        balances.add("\n " + app.getResources().getString(R.string.total) + " : " + total + '\n');
        Collections.sort(balances);
        return balances;
    }

    public User findUserByName(String fName, String lName) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM users WHERE first_name='" + fName + "' AND  last_name='" + lName + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount()== 0) return null;

        cursor.moveToNext();
        int id = cursor.getInt(0);
        String firstName = cursor.getString(1);
        String lastName = cursor.getString(2);
        String group = cursor.getString(3);
        String rank = cursor.getString(4);
        int balance = cursor.getInt(5);
        int pinCode = cursor.getInt(6);
        User user = new User(id, firstName, lastName, group, rank, balance, pinCode);

        cursor.close();
        return user;
    }

    public void updateGroup(int id, String group) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_", group);
        String[] selectionArgs = {id + ""};

        db.update("users", values, "id LIKE ?", selectionArgs);
    }

    public void updateRank(int id, String rank) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rank", rank);
        String[] selectionArgs = {id + ""};

        db.update("users", values, "id LIKE ?", selectionArgs);
    }

    public void updateBalance(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", user.getBalance());
        String[] selectionArgs = {user.getId() + ""};

        db.update("users", values, "id LIKE ?", selectionArgs);
    }

    public void updatePincode(int id, int pincode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pin_code", pincode);
        String[] selectionArgs = {id + ""};

        db.update("users", values, "id LIKE ?", selectionArgs);
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM users WHERE id=" + id;
        db.execSQL(sql);
    }

    //------------------------- Handles logs ---------------------------------------//

    public void insertLog(String user1, String user2, String action, int amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_1", user1);
        values.put("user_2", user2);
        values.put("action_", action);
        values.put("amount", amount);
        values.put("date", app.getDate());
        db.insert("log", null, values);
    }

    public List<String> getLog() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM log";
        Cursor cursor = db.rawQuery(sql, null);

        List<String> log = new ArrayList<>();
        while (cursor.moveToNext()) {
            String logLine;
            String actionText;
            switch (cursor.getString(3)) {
                case "addition":
                    actionText = " " + app.getResources().getString(R.string.addition) + " ";
                    logLine = '\n' + cursor.getString(5).substring(0,5) + ": " + cursor.getString(1) + actionText + cursor.getString(2) + " : " + cursor.getInt(4);
                    break;
                case "creation":
                    actionText = " " + app.getResources().getString(R.string.creation) + " ";
                    logLine = '\n' + cursor.getString(5).substring(0,5) + ": " + cursor.getString(1) + actionText + cursor.getString(2);
                    break;
                case "deletion":
                    actionText = " " + app.getResources().getString(R.string.deletion) + " ";
                    logLine = '\n' + cursor.getString(5).substring(0,5) + ": " + cursor.getString(1) + actionText + cursor.getString(2) + " : " + cursor.getInt(4);
                    break;
                default:
                    logLine = app.getResources().getString(R.string.error);
                    break;
            }
            log.add(logLine);
        }
        cursor.close();
        Collections.reverse(log);
        return log;
    }

    public List<String> findLogByName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM log WHERE user_2='" + name + "' AND  (action_='addition' OR action_='deletion')";
        Cursor cursor = db.rawQuery(sql, null);

        List<String> log = new ArrayList<>();
        while (cursor.moveToNext()) {
            String logLine;
            switch (cursor.getString(3)) {
                case "addition":
                    logLine = '\n' + cursor.getString(5).substring(0,14) + "  " + cursor.getString(1) + " : " + cursor.getInt(4);
                    break;
                case "deletion":
                    logLine = '\n' + cursor.getString(5).substring(0,14) + "  " + cursor.getString(1) + " : -" + cursor.getInt(4);
                    break;
                default:
                    logLine = app.getResources().getString(R.string.error);
                    break;
            }
            log.add(logLine);
        }
        cursor.close();
        Collections.reverse(log);
        return log;
    }
}
