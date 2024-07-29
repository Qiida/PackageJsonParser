package com.iav.et.data.signal;

import com.iav.et.api.Api;
import com.iav.et.data.etpackage.EtPackage;
import com.iav.et.data.etpackage.EtPackageFactory;
import com.iav.et.data.step.Step;
import com.iav.et.data.step.StepFactory;
import com.iav.et.data.step.signal.Read;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.LoMa;
import com.iav.et.mapping.VarFile;
import com.iav.et.workspace.Workspace;
import com.iav.et.workspace.WorkspaceType;
import de.tracetronic.tts.apiClient.stubs.BusSignalMappingItem;
import de.tracetronic.tts.apiClient.stubs.LocalMapping;
import de.tracetronic.tts.apiClient.stubs.MappingItem;
import de.tracetronic.tts.apiClient.stubs.Package;
import junit.framework.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.util.LinkedHashMap;

public class SignalFactoryTest {
    @BeforeAll
    public static void setUp() {
        try {
            Workspace.setUp(WorkspaceType.BK25);
            GloMa.setUp();
            VarFile.setUp();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void buildUnmappedSignalTest() throws NoSuchFileException {
        String path = "c:\\src\\PackageJsonParser\\src\\test\\resources\\package\\TestPackage_unmappedSignal.pkg";
        EtPackage p = EtPackageFactory.buildPackageFromPackagePath(path);
        Read read = (Read) p.getStepByLine(1);
        Assert.assertEquals("CurrentFPVState", read.getSignal().getName());
        Assert.assertEquals("BUS", read.getSignal().getAccessType());
        Assert.assertEquals("A_FlexRay", read.getSignal().getSystemIdentifier());
        read = (Read) p.getStepByLine(2);
        Assert.assertEquals("SecocStateRespDiagAddr", read.getSignal().getName());
        Assert.assertEquals("BUS", read.getSignal().getAccessType());
        Assert.assertEquals("ETHERNET-2", read.getSignal().getSystemIdentifier());
    }
}
