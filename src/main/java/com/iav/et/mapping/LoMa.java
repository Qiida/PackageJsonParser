package com.iav.et.mapping;


import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.Mapping;
import de.tracetronic.tts.apiClient.stubs.Package;

import java.util.logging.Logger;

public class LoMa {
    private static LoMa INSTANCE = null;
    public Mapping api;
    private static  final Logger LOGGER = LoggerUtils.getLOGGER();
    private LoMa(Package p) {
        api = p.GetMapping();
    }

    public static void setUp(Package p) {
        INSTANCE = new LoMa(p);
    }

    public static LoMa getINSTANCE() {
        if (INSTANCE == null) {
            throw new IllegalStateException("LoMa not initialized. Call setUp() first.");
        }
        return INSTANCE;
    }
}
