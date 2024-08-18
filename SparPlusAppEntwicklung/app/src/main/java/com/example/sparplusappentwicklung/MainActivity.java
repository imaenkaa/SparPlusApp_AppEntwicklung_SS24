package com.example.sparplusappentwicklung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements BalanceManager.BalanceChangeListener {

    private TextView greetingTextView;
    private TextView balanceTextView;
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greetingTextView = findViewById(R.id.WelcomeTextViewGreeting);  // Willkommenssatz
        balanceTextView = findViewById(R.id.WelcomeTextViewCurrentBalance);  // Kontostand
        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getIntent().getStringExtra("userEmail");

        if (currentUserEmail != null) {
            updateBalance();
        }

        // BalanceManager-Listener hinzufügen
        BalanceManager.getInstance().addBalanceChangeListener(this);

        findViewById(R.id.WelcomeButtonDeposit).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DepositActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
        });

        findViewById(R.id.WelcomeButtonWithdraw).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
        });

        findViewById(R.id.WelcomeButtonTransfer).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransferActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
        });

        findViewById(R.id.WelcomeButtonTransactions).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);
            intent.putExtra("userEmail", currentUserEmail);
            startActivity(intent);
        });

        findViewById(R.id.WelcomeButtonLogout).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override //wird angezeigt wenn man aus andere Activities zurückkehrt
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listener entfernen, um Speicherlecks zu vermeiden
        BalanceManager.getInstance().removeBalanceChangeListener(this);
    }

    private void updateBalance() {
        double balance = BalanceManager.getInstance().getCurrentBalance();
        balanceTextView.setText(String.format("Aktueller Kontostand: €%.2f", balance));
    }

    @Override
    public void onBalanceChanged(double newBalance) {
        // Aktualisiere den Kontostand in der UI, wenn sich der Wert ändert
        updateBalance();
    }
}




