package com.documind.util;

import java.util.regex.Pattern;

public final class TextNormalizer {

    private static final Pattern NULL_CHARACTERS = Pattern.compile("\u0000");
    private static final Pattern HORIZONTAL_WHITESPACE = Pattern.compile("[\\t\\x0B\\f\\r ]+");
    private static final Pattern EXCESS_NEWLINES = Pattern.compile("\\n{3,}");

    private TextNormalizer() {
    }

    /**
     * Cleans extracted text. Null characters are removed because PostgreSQL text columns reject them.
     */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String cleaned = NULL_CHARACTERS.matcher(text).replaceAll("");
        cleaned = HORIZONTAL_WHITESPACE.matcher(cleaned).replaceAll(" ");
        cleaned = EXCESS_NEWLINES.matcher(cleaned).replaceAll("\n\n");
        return cleaned.trim();
    }
}
