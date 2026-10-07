package com.documind.util;

public final class VectorUtils {

    private VectorUtils() {
    }

    /**
     * Converts an embedding to pgvector's text literal format: {@code [0.1,0.2,0.3]}.
     */
    public static String toPgVectorLiteral(float[] vector) {
        StringBuilder builder = new StringBuilder(vector.length * 10).append('[');
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(vector[i]);
        }
        return builder.append(']').toString();
    }
}
