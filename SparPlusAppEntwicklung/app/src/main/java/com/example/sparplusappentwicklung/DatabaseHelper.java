package com.example.sparplusappentwicklung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Signup.db";
    public static final int DATABASE_VERSION = 2; // Erhöhe die Version bei Änderungen

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Wird aufgerufen, wenn die Datenbank zum ersten Mal erstellt wird
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE allusers(email TEXT PRIMARY KEY, password TEXT, balance REAL)");
        db.execSQL("CREATE TABLE transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, type TEXT, amount REAL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }


    @Override  // Wird aufgerufen, wenn die Datenbank-Version geändert wird
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS allusers");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    // Fügt neue Benutzerdaten in die Tabelle ein
    public boolean insertData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("balance", 0.00); // Anfangsguthaben 0
        long result = db.insert("allusers", null, values);
        return result != -1;
    }

    // Überprüft, ob eine E-Mail-Adresse in der Tabelle vorhanden ist
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //Stimmen Email und Pssword überein??
    public boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE email = ? AND password = ?", new String[]{email, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid; //wenn Kombination vorhanden
    }

    // Gibt den Kontostand eines Benutzers zurück
    public double getBalance(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        double balance = 0.0;
        try {
            cursor = db.rawQuery("SELECT balance FROM allusers WHERE email = ?", new String[]{email});
            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting balance", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return balance;
    }

    public boolean updateBalance(String email, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", balance);
        int rowsAffected = db.update("allusers", values, "email = ?", new String[]{email});
        Log.d("DatabaseHelper", "Rows affected by updateBalance: " + rowsAffected);
        return rowsAffected > 0;
    }

    public boolean insertTransaction(String email, String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("type", type);
        values.put("amount", amount);
        long result = db.insert("transactions", null, values);
        Log.d("DatabaseHelper", "Transaction insert result: " + result);
        return result != -1;
    }

    // Gibt alle Transaktionen eines Benutzers
    public Cursor getTransactions(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM transactions WHERE email = ? ORDER BY timestamp DESC", new String[]{email});
    }
}

