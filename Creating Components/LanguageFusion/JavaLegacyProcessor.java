package com.keeper.components.fusion;

import java.util.ArrayList;
import java.util.List;

public class JavaLegacyProcessor {

    public List<String> filterAndTransform(List<String> inputs) {
        List<String> results = new ArrayList<>();
        if (inputs == null) {
            return results;
        }
        for (String s : inputs) {
            if (s != null && s.length() > 3) {
                results.add(s.trim().toUpperCase());
            }
        }
        return results;
    }
}
