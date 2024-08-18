package com.example.sparplusappentwicklung;

import java.util.ArrayList;
import java.util.List;

public class BalanceManager {

    private static BalanceManager instance;
    private double currentBalance;

    // Liste von Listenern, die über Änderungen des Kontostands benachrichtigt werden
    private final List<BalanceChangeListener> listeners = new ArrayList<>();

    private BalanceManager() {
    }

    // Gibt die einzige Instanz der BalanceManager-Klasse zurück
    public static synchronized BalanceManager getInstance() {
        if (instance == null) { // fals Instanz nicht vorhanden
            instance = new BalanceManager();
        }
        return instance;
    }

    // Setzt den aktuellen Kontostand + benachrichtigt alle Listener über Änderungen (Alle Screens)
    public void setCurrentBalance(double balance) {
        this.currentBalance = balance;
        notifyBalanceChanged();
    }

    // Gibt den aktuellen Kontostand zurück
    public double getCurrentBalance() {
        return currentBalance;
    }

    // Listener + Interface
    public void addBalanceChangeListener(BalanceChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeBalanceChangeListener(BalanceChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyBalanceChanged() {
        for (BalanceChangeListener listener : listeners) {
            listener.onBalanceChanged(currentBalance);
        }
    }

    // Interface für Listener, die auf Änderungen des Kontostands reagieren
    public interface BalanceChangeListener {
        void onBalanceChanged(double newBalance);
    }
}

