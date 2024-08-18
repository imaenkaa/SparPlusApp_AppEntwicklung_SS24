package com.example.sparplusappentwicklung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TransactionsActivity extends AppCompatActivity {

    private static final String TAG = "TransactionsActivity";

    private LinearLayout transactionsList;
    private Button btnBack;
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        transactionsList = findViewById(R.id.TransactionsTransactionsList);
        btnBack = findViewById(R.id.TransactionsButtonBack);

        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getIntent().getStringExtra("userEmail");

        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Log.e(TAG, "Error: User email is null or empty!");
            return;
        }

        displayTransactions(); //aufgerufen

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionsActivity.this, MainActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
            finish();
        });
    }

    private void displayTransactions() { //holt Transactions aus DB
        Cursor cursor = null;
        try {
            cursor = dbHelper.getTransactions(currentUserEmail);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                    String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                    String transactionDetail = String.format("%s: €%.2f am %s", type, amount, timestamp);
                    addTransaction(transactionDetail);
                } while (cursor.moveToNext());
            } else {
                Log.e(TAG, "Keine Transaktionen gefunden oder Cursor ist null.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Abrufen der Transaktionen: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //Bei neue Transactiom wird jedes Mal ein neues TextView mit Größe, Padding und Farbe erstellt
    private void addTransaction(String transactionDetail) {
        TextView transactionTextView = new TextView(this);
        transactionTextView.setText(transactionDetail);
        transactionTextView.setTextSize(18);
        transactionTextView.setPadding(12, 12, 12, 12);
        transactionTextView.setTextColor(getResources().getColor(R.color.black));
        transactionsList.addView(transactionTextView);
    }
}

