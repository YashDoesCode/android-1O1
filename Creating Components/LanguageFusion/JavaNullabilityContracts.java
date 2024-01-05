package com.keeper.components.fusion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class JavaNullabilityContracts {

    private JavaNullabilityContracts() {
    }

    @NonNull
    public static String sanitizeInput(@Nullable String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }
}
