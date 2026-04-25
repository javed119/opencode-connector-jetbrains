package com.epochbyte.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class OpencodeReferenceBuilderTest {

    @Test
    void shouldConvertFilePathToProjectRelativePath() {
        String relativePath = OpencodeReferenceBuilder.toRelativePath(
            "/workspace/demo",
            "/workspace/demo/src/Main.java"
        );

        assertEquals("src/Main.java", relativePath);
    }

    @Test
    void shouldConvertDirectoryPathToProjectRelativePath() {
        String relativePath = OpencodeReferenceBuilder.toRelativePath(
            "/workspace/demo",
            "/workspace/demo/src/features"
        );

        assertEquals("src/features", relativePath);
    }

    @Test
    void shouldKeepNestedProjectSegments() {
        String relativePath = OpencodeReferenceBuilder.toRelativePath(
            "/workspace/demo",
            "/workspace/demo/modules/core/src/Main.java"
        );

        assertEquals("modules/core/src/Main.java", relativePath);
    }

    @Test
    void shouldKeepOnlyTopLevelSelectionsInStableOrder() {
        List<String> relativePaths = OpencodeReferenceBuilder.normalizeRelativePaths(List.of(
            "README.md",
            "src/features/Feature.java",
            "src"
        ));

        assertIterableEquals(List.of("README.md", "src"), relativePaths);
    }
}
