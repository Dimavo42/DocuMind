package com.documind.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TextChunkerTest {

    @Test
    void returnsNoChunksForBlankText() {
        assertThat(TextChunker.split("   ", 100, 10)).isEmpty();
    }

    @Test
    void returnsSingleChunkForShortText() {
        assertThat(TextChunker.split("Short text", 100, 10)).containsExactly("Short text");
    }

    @Test
    void splitsLongTextWithOverlapAndWithoutCuttingWords() {
        String text = "word ".repeat(100).trim();

        List<String> chunks = TextChunker.split(text, 100, 20);

        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks).allSatisfy(chunk -> {
            assertThat(chunk.length()).isLessThanOrEqualTo(100);
            assertThat(chunk).matches("(word ?)+");
        });
    }
}
