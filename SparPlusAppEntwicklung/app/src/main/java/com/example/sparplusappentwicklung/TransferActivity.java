package com.example.sparplusappentwicklung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TransferActivity extends AppCompatActivity implements BalanceManager.BalanceChangeListener {

    private EditText editTextAmount;
    private EditText editTextUser;
    private Button btnTransfer;
    private Button btnBack;
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        editTextAmount = findViewById(R.id.TransferEditTextAmount);
        editTextUser = findViewById(R.id.TransferEditTextUser);
        btnTransfer = findViewById(R.id.TransferButtonTransfer);
        btnBack = findViewById(R.id.TransferButtonBack);

        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getIntent().getStringExtra("userEmail");

        btnTransfer.setOnClickListener(v -> performTransfer());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TransferActivity.this, MainActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
            finish();
        });

        // Listener hinzufügen
        BalanceManager.getInstance().addBalanceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listener entfernen
        BalanceManager.getInstance().removeBalanceChangeListener(this);
    }

    private void performTransfer() { //Führt die Überweisung durch
        String amountStr = editTextAmount.getText().toString();
        String userStr = editTextUser.getText().toString();

        if (amountStr.isEmpty() || userStr.isEmpty()) {
            if (amountStr.isEmpty()) {
                editTextAmount.setError("Bitte Betrag eingeben");
            }
            if (userStr.isEmpty()) {
                editTextUser.setError("Bitte Benutzer eingeben");
            }
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            editTextAmount.setError("Ungültiger Betrag");
            return;
        }

        // vgl mit DB
        double currentBalance = dbHelper.getBalance(currentUserEmail);
        double recipientBalance = dbHelper.getBalance(userStr);

        if (amount > currentBalance) {
            editTextAmount.setError("Unzureichende Mittel");
            return;
        }

        // Update balance and insert transactions
        boolean isSenderUpdated = dbHelper.updateBalance(currentUserEmail, currentBalance - amount);
        boolean isRecipientUpdated = dbHelper.updateBalance(userStr, recipientBalance + amount);
        boolean isSenderTransactionInserted = dbHelper.insertTransaction(currentUserEmail, "Überweisung", amount);
        boolean isRecipientTransactionInserted = dbHelper.insertTransaction(userStr, "Überweisung empfangen", amount);

        if (isSenderUpdated && isRecipientUpdated && isSenderTransactionInserted && isRecipientTransactionInserted) {
            BalanceManager.getInstance().setCurrentBalance(currentBalance - amount); // Aktualisiere Balance im BalanceManager
            Toast.makeText(this, "Überweisung erfolgreich", Toast.LENGTH_SHORT).show();
            editTextAmount.setText("");
            editTextUser.setText("");
        } else {
            editTextAmount.setError("Überweisung fehlgeschlagen. Bitte versuchen Sie es erneut.");
        }
    }

    @Override
    public void onBalanceChanged(double newBalance) {
        // Keine UI-Aktualisierung hier nötig, da dies die TransferActivity betrifft
    }
}

