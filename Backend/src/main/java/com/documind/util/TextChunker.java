package com.documind.util;

import java.util.ArrayList;
import java.util.List;

public final class TextChunker {

    private TextChunker() {
    }

    /**
     * Splits text into chunks of roughly {@code chunkSize} characters with {@code overlap} characters
     * shared between neighbours. Chunks end on whitespace where possible so words are not cut in half.
     */
    public static List<String> split(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }

        int length = text.length();
        int start = 0;
        while (start < length) {
            int end = findChunkEnd(text, start, chunkSize);
            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end >= length) {
                break;
            }
            int nextStart = end - overlap;
            start = nextStart > start ? nextStart : end;
        }
        return chunks;
    }

    private static int findChunkEnd(String text, int start, int chunkSize) {
        int hardEnd = Math.min(start + chunkSize, text.length());
        if (hardEnd == text.length()) {
            return hardEnd;
        }
        int minimumEnd = start + chunkSize / 2;
        for (int i = hardEnd; i > minimumEnd; i--) {
            if (Character.isWhitespace(text.charAt(i - 1))) {
                return i;
            }
        }
        return hardEnd;
    }
}
