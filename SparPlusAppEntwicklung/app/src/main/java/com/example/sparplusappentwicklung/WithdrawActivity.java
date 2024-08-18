package com.example.sparplusappentwicklung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WithdrawActivity extends AppCompatActivity implements BalanceManager.BalanceChangeListener {

    private EditText withdrawAmountEditText;
    private Button btnWithdraw;
    private Button btnBack;
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        withdrawAmountEditText = findViewById(R.id.WithdrawEditTextAmount);
        btnWithdraw = findViewById(R.id.WithdrawButtonWithdraw);
        btnBack = findViewById(R.id.WithdrawButtonBack);

        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getIntent().getStringExtra("userEmail");

        btnWithdraw.setOnClickListener(v -> handleWithdraw());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
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

    private void handleWithdraw() { //Führt die Abhebung durch
        String amountText = withdrawAmountEditText.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Bitte geben Sie einen Betrag ein", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ungültiger Betrag", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Bitte geben Sie einen positiven Betrag ein", Toast.LENGTH_SHORT).show();
            return;
        }

        double currentBalance = dbHelper.getBalance(currentUserEmail);
        if (amount > currentBalance) {
            Toast.makeText(this, "Nicht genügend Guthaben", Toast.LENGTH_SHORT).show();
            return;
        }

        currentBalance -= amount;

        boolean balanceUpdated = dbHelper.updateBalance(currentUserEmail, currentBalance);
        boolean transactionInserted = dbHelper.insertTransaction(currentUserEmail, "Abhebung", amount);

        if (balanceUpdated && transactionInserted) {
            BalanceManager.getInstance().setCurrentBalance(currentBalance); // Aktualisiere Balance im BalanceManager
            Toast.makeText(this, "Abhebung erfolgreich", Toast.LENGTH_SHORT).show();
            withdrawAmountEditText.setText("");
        } else {
            Toast.makeText(this, "Abhebung fehlgeschlagen. Bitte versuchen Sie es erneut.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBalanceChanged(double newBalance) {
    }
}




