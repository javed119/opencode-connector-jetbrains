package com.epochbyte.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessScannerTest {

    @Test
    void extractsPortsFromMixedCommandLines() {
        List<Integer> ports = ProcessScanner.extractPorts(List.of(
            "opencode.exe --port 39910",
            "opencode --port=22459",
            "opencode.exe",
                "E:\\Develop\\Nodejs\\node_global\\node_modules\\opencode-ai\\node_modules\\opencode-windows-x64\\bin\\opencode.exe --port 31983, E:\\Develop\\Nodejs\\node_global\\node_modules\\opencode-ai\\node_modules\\opencode-windows-x64\\bin\\opencode.exe --port 33434"
        ));

        assertEquals(List.of(39910, 22459, 31983, 33434), ports);
    }

    @Test
    void identifiesTargetProcessFromNameOrCommandLine() {
        assertTrue(ProcessScanner.isTargetProcess(
            "opencode.exe",
            "E:\\tools\\opencode.exe --port 30000"
        ));
        assertTrue(ProcessScanner.isTargetProcess(
            "node.exe",
            "node C:\\Users\\appdata\\npm\\opencode --port 30000"
        ));
        assertFalse(ProcessScanner.isTargetProcess("node.exe", ""));
        assertFalse(ProcessScanner.isTargetProcess("java.exe", "java -jar app.jar"));
    }
}
