package com.example.DrinkRegisterApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE users (id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, group_ TEXT, balance REAL, pin_code INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades as needed
    }

    public void emptyDb() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM users";
        db.execSQL(sql);
    }

    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("group_", user.getGroup());
        values.put("balance", user.getBalance());
        values.put("pin_code", user.getPinCode());
        db.insert("users", null, values);
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM users WHERE balance >= 0";
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String group = cursor.getString(3);
            double balance = cursor.getDouble(4);
            int pinCode = cursor.getInt(5);

            User user = new User(id, firstName, lastName, group, pinCode);
            user.setBalance(balance);
            users.add(user);
        }
        cursor.close();
        return users;
    }

    public void updateBalance(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", user.getBalance());
        String[] selectionArgs = {user.getId() + ""};

        db.update("users", values, "id LIKE ?", selectionArgs);
    }
}
