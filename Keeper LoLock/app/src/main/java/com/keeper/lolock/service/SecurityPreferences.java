package com.keeper.lolock.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

public final class SecurityPreferences {

    private static final String PREF_NAME = "keeper_lolock_sec_prefs";
    private static final String KEY_PASS_TIMESTAMP = "pass_timestamp";
    private static final String KEY_PASS_SET = "is_pass_set";
    private static final long ROTATION_PERIOD_DAYS = 180L;
    private static final long ROTATION_PERIOD_MS = TimeUnit.DAYS.toMillis(ROTATION_PERIOD_DAYS);

    private final SharedPreferences prefs;

    public SecurityPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isPasswordConfigured() {
        return prefs.getBoolean(KEY_PASS_SET, false);
    }

    public void markPasswordConfigured() {
        prefs.edit()
                .putBoolean(KEY_PASS_SET, true)
                .putLong(KEY_PASS_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public long getPasswordSetTimestamp() {
        return prefs.getLong(KEY_PASS_TIMESTAMP, 0L);
    }

    public boolean isRotationDue() {
        if (!isPasswordConfigured()) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - getPasswordSetTimestamp();
        return elapsed >= ROTATION_PERIOD_MS;
    }

    public long getRemainingDaysUntilRotation() {
        if (!isPasswordConfigured()) {
            return ROTATION_PERIOD_DAYS;
        }
        long elapsed = System.currentTimeMillis() - getPasswordSetTimestamp();
        long remainingMs = ROTATION_PERIOD_MS - elapsed;
        if (remainingMs <= 0) {
            return 0;
        }
        return TimeUnit.MILLISECONDS.toDays(remainingMs);
    }
}
