package com.epochbyte.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpencodeSettingsTest {

    @Test
    void shouldAllowEmptyServerUrlForLegacyMode() {
        assertEquals("", OpencodeSettings.normalizeServerUrl("  "));
    }

    @Test
    void shouldRemoveTrailingSlashFromServerUrl() {
        assertEquals(
            "http://127.0.0.1:4096",
            OpencodeSettings.normalizeServerUrl("http://127.0.0.1:4096/")
        );
    }

    @Test
    void shouldRejectServerUrlWithPath() {
        assertThrows(
            IllegalArgumentException.class,
            () -> OpencodeSettings.normalizeServerUrl("http://127.0.0.1:4096/opencode")
        );
    }
}
