package com.example.sparplusappentwicklung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DepositActivity extends AppCompatActivity implements BalanceManager.BalanceChangeListener {

    //UI Elemente
    private EditText depositAmountEditText;
    private Button btnDeposit;
    private Button btnBack;

    //DB und EMail
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        depositAmountEditText = findViewById(R.id.DepositEditTextAmount);
        btnDeposit = findViewById(R.id.DepositButtonDeposit);
        btnBack = findViewById(R.id.DepositButtonBack);

        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getIntent().getStringExtra("userEmail");

        if (currentUserEmail == null) {
            Toast.makeText(this, "Fehler: Benutzer-E-Mail nicht gefunden.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnDeposit.setOnClickListener(v -> handleDeposit());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(DepositActivity.this, MainActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
            finish();
        });

        // BalanceManager Listener hinzufügen
        BalanceManager.getInstance().addBalanceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listener entfernen, wenn Activity zerstört wird
        BalanceManager.getInstance().removeBalanceChangeListener(this);
    }

    private void handleDeposit() { //Verarbeitet die Einzahlung des Benutzers
        String amountText = depositAmountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Bitte geben Sie einen Betrag ein", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Log.e("DepositActivity", "Invalid amount format", e);
            Toast.makeText(this, "Ungültiger Betrag", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Bitte geben Sie einen positiven Betrag ein", Toast.LENGTH_SHORT).show();
            return;
        }

        double currentBalance = dbHelper.getBalance(currentUserEmail);
        currentBalance += amount;

        boolean balanceUpdated = dbHelper.updateBalance(currentUserEmail, currentBalance);
        boolean transactionInserted = dbHelper.insertTransaction(currentUserEmail, "Einzahlung", amount);

        if (balanceUpdated && transactionInserted) {
            // Update balance in BalanceManager
            BalanceManager.getInstance().setCurrentBalance(currentBalance);

            Toast.makeText(this, "Einzahlung erfolgreich", Toast.LENGTH_SHORT).show();
            depositAmountEditText.setText("");
        } else {
            Log.e("DepositActivity", "Failed to update balance or insert transaction");
            Toast.makeText(this, "Einzahlung fehlgeschlagen. Bitte versuchen Sie es erneut.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override // Methode aus BalanceChangeListener vorgeschrieben
    public void onBalanceChanged(double newBalance) {
    }
}

