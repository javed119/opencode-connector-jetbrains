package com.epochbyte.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortDetectorTest {

    @Test
    void samePathTreatsDifferentSeparatorsAsEqual() {
        assertTrue(PortDetector.samePath(
            "E:/Develop/Epochbyte/fe/agriculture-products-ui",
            "E:\\Develop\\Epochbyte\\fe\\agriculture-products-ui"
        ));
    }

    @Test
    void samePathIgnoresWindowsCase() {
        assertTrue(PortDetector.samePath(
            "E:/Develop/Epochbyte/fe/agriculture-products-ui",
            "e:\\develop\\epochbyte\\fe\\AGRICULTURE-PRODUCTS-UI"
        ));
    }

    @Test
    void samePathDetectsDifferentDirectories() {
        assertFalse(PortDetector.samePath(
            "E:/Develop/Epochbyte/fe/agriculture-products-ui",
            "E:/Develop/Epochbyte/fe/another-project"
        ));
    }
}
