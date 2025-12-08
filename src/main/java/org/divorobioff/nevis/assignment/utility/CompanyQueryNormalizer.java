package org.divorobioff.nevis.assignment.utility;

import java.util.Locale;

public final class CompanyQueryNormalizer {

    private CompanyQueryNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[^a-z0-9]", "");
        return normalized;
    }
}
