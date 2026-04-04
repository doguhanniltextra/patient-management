package com.project.patient_service.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SanitizationUtils {
    private static final Logger log = LoggerFactory.getLogger(SanitizationUtils.class);

    /**
     * Strips all HTML tags from the input string to prevent XSS.
     * If input is null, returns null.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = Jsoup.clean(input, Safelist.none());
        if (!input.equals(cleaned)) {
            log.warn("Sanitization triggered: HTML tags removed from input.");
        }
        return cleaned;
    }

    /**
     * Simple check to see if string contains HTML-like characters.
     */
    public static boolean isUnsafe(String input) {
        if (input == null) return false;
        return !input.equals(Jsoup.clean(input, Safelist.none()));
    }
}
