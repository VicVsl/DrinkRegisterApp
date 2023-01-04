package com.example.DrinkRegisterApp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        String sqlLog = "CREATE TABLE log (id INTEGER PRIMARY KEY, user_1 TEXT, user_2 TEXT, action_ TEXT, amount INTEGER)";
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
        db.execSQL(sqlLog);
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

    public User findUserByName(String fName, String lName) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM users WHERE first_name='" + fName + "' AND  last_name='" + lName + "'";
        Cursor cursor = db.rawQuery(sql, null);

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

    public void updateBalance(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", user.getBalance());
        String[] selectionArgs = {user.getId() + ""};

        db.update("users", values, "id LIKE ?", selectionArgs);
    }

//    public void updateRank(User user) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("rank", user.getRank());
//        String[] selectionArgs = {user.getId() + ""};
//
//        db.update("users", values, "id LIKE ?", selectionArgs);
//    }

    //------------------------- Handles logs ---------------------------------------//

    public void insertLog(String user1, String user2, String action, int amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_1", user1);
        values.put("user_2", user2);
        values.put("action_", action);
        values.put("amount", amount);
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
                    logLine = '\n' + cursor.getString(1) + actionText + cursor.getString(2) + " : " + cursor.getInt(4);
                    break;
                case "creation":
                    actionText = " " + app.getResources().getString(R.string.creation) + " ";
                    logLine = '\n' + cursor.getString(1) + actionText + cursor.getString(2);
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
        String sql = "SELECT * FROM log WHERE user_2='" + name + "' AND  action_='addition'";
        Cursor cursor = db.rawQuery(sql, null);

        List<String> log = new ArrayList<>();
        while (cursor.moveToNext()) {
            String actionText = " " + app.getResources().getString(R.string.addition) + " ";
            String logLine = '\n' + cursor.getString(1) + actionText + cursor.getString(2) + " : " + cursor.getInt(4);
            log.add(logLine);
        }
        cursor.close();
        Collections.reverse(log);
        return log;
    }


    //Can be deleted when the app is finished
    public void startDatabase() {
        emptyDb();
        // Creates users
        User vic = new User(0,"Vic", "Vansteelant", "Verkenners", "admin", 10, 1111);
        User jannes = new User(0, "Jannes", "Dekeyzer", "Kapoenen", "mod", 5, 2222);
        User bavo = new User(0, "Bavo", "Dewaele", "Jins", "regular", 100, 3333);

        // Inserts the users in the database
        insertUser(vic);
        insertUser(jannes);
        for(int i = 0; i < 10; i++ ) {
            insertUser(bavo);
        }
        app.finish();
        app.startActivity(app.getIntent());
    }

}
