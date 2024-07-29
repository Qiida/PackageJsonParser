package com.iav.et.data.signal;

import com.google.gson.JsonObject;
import com.iav.et.mapping.GloMa;
import com.iav.et.mapping.LoMa;
import com.iav.et.mapping.VarFile;
import com.iav.utils.JsonUtils;
import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.*;

import java.util.logging.Logger;

public class SignalFactory {
    private static final Logger LOGGER = LoggerUtils.getLOGGER();
    public static Signal buildSignalFromApi(MappingTestStep mappingTestStep) throws UnsupportedOperationException {
        String mappingItemReferenceName = mappingTestStep.GetMappingItemReferenceName();

        GloMa gloma = GloMa.getINSTANCE();
        MappingItem gloMaMappingItem = null;
        if (gloma.path != null) {
            GlobalMapping gloMa = GloMa.getINSTANCE().api;
            gloMaMappingItem = gloMa.GetItemByName(mappingItemReferenceName);
        }
        if (gloMaMappingItem == null) {
            GlobalMapping varFile = VarFile.getINSTANCE().api;
            MappingItem varFileMappingItem = varFile.GetItemByName(mappingItemReferenceName);
            if (varFileMappingItem == null) {
                return buildFromLocalMapping(mappingTestStep);
            } else {
                return buildModelFromMappingItem((ModelMappingItem) varFileMappingItem);
            }
        } else {
            String accessType = gloMaMappingItem.GetAccessType();
            return switch (accessType) {
                case "BUS" -> buildBusFromMappingItem((BusSignalMappingItem) gloMaMappingItem);
                case "SERVICE_EVENT" -> buildEthernetFromMappingItem((ServiceEventLeafMappingItem) gloMaMappingItem);
                default -> throw new UnsupportedOperationException("AccessType not implemented yet: " + accessType);
            };
        }
    }

    public static Signal buildSignalFromJsonObject(JsonObject jsonObject) {
        String accessType = JsonUtils.getStringAttribute(jsonObject, "AccessType");
        if (accessType != null) {
            return switch (accessType) {
                case "BUS" -> buildBusFromJsonObject(jsonObject);
                case "SERVICE_EVENT" -> buildEthernetFromJsonObject(jsonObject);
                case "MODEL" -> buildModelFromJsonObject(jsonObject);
                default -> throw new IllegalStateException("Unexpected value: " + accessType);
            };
        } else {
            String name = JsonUtils.getStringAttribute(jsonObject, "Name");
            String path = JsonUtils.getStringAttribute(jsonObject, "Path");
            String category = JsonUtils.getStringAttribute(jsonObject, "Category");
            String systemIdentifier = JsonUtils.getStringAttribute(jsonObject, "SystemIdentifier");
            return new Signal(name, path, category, null, systemIdentifier);
        }
    }


    private static Signal buildFromLocalMapping(MappingTestStep mappingTestStep) {
        String mappingItemReferenceName = mappingTestStep.GetMappingItemReferenceName();
        Mapping localMapping = LoMa.getINSTANCE().api;
        MappingItem mappingItem = localMapping.GetItemByName(mappingItemReferenceName);
        if (mappingItem instanceof BusSignalMappingItem busSignalMappingItem) {
            return new Signal(
                    busSignalMappingItem.GetSignalName(),
                    busSignalMappingItem.GetTargetPath(),
                    busSignalMappingItem.GetCategory(),
                    busSignalMappingItem.GetAccessType(),
                    busSignalMappingItem.GetSystemIdentifier()
            );
        } else if (mappingItem instanceof ModelMappingItem modelMappingItem) {
            return new Signal(
                    modelMappingItem.GetReferenceName(),
                    modelMappingItem.GetTargetPath(),
                    modelMappingItem.GetCategory(),
                    modelMappingItem.GetAccessType(),
                    modelMappingItem.GetSystemIdentifier()
            );
        } else if (mappingItem instanceof ServiceEventLeafMappingItem serviceEventLeafMappingItem) {
            return new Signal(
                    serviceEventLeafMappingItem.GetReferenceName(),
                    serviceEventLeafMappingItem.GetTargetPath(),
                    serviceEventLeafMappingItem.GetCategory(),
                    serviceEventLeafMappingItem.GetAccessType(),
                    serviceEventLeafMappingItem.GetSystemIdentifier()
            );
        }
//        TODO: implement text analyser to determine which signal type from mappingItemReferenceName
        return new Signal(mappingItemReferenceName, null, null, null, null);
    }

    private static Model buildModelFromMappingItem(ModelMappingItem mappingItem) {
        return new Model(mappingItem.GetReferenceName(), mappingItem.GetTargetPath(), mappingItem.GetCategory(),
                mappingItem.GetAccessType(), mappingItem.GetSystemIdentifier());
    }

    private static Model buildModelFromJsonObject(JsonObject jsonObject) {
        return new Model(
                JsonUtils.getStringAttribute(jsonObject, "Name"),
                JsonUtils.getStringAttribute(jsonObject, "Path"),
                JsonUtils.getStringAttribute(jsonObject, "Category"),
                JsonUtils.getStringAttribute(jsonObject, "AccessType"),
                JsonUtils.getStringAttribute(jsonObject, "SystemIdentifier")
        );
    }

    private static Bus buildBusFromMappingItem(BusSignalMappingItem mappingItem) {
        return new Bus(mappingItem.GetSignalName(), mappingItem.GetTargetPath(), mappingItem.GetCategory(),
                mappingItem.GetAccessType(), mappingItem.GetSystemIdentifier(), mappingItem.GetPduName(),
                mappingItem.GetNodeName(), mappingItem.GetFrameName());
    }

    private static Bus buildBusFromJsonObject(JsonObject jsonObject) {
        return new Bus(
                JsonUtils.getStringAttribute(jsonObject, "Name"),
                JsonUtils.getStringAttribute(jsonObject, "Path"),
                JsonUtils.getStringAttribute(jsonObject, "Category"),
                JsonUtils.getStringAttribute(jsonObject, "AccessType"),
                JsonUtils.getStringAttribute(jsonObject, "SystemIdentifier"),
                JsonUtils.getStringAttribute(jsonObject, "Pdu"),
                JsonUtils.getStringAttribute(jsonObject, "Node"),
                JsonUtils.getStringAttribute(jsonObject, "Frame")
        );
    }

    private static Ethernet buildEthernetFromMappingItem(ServiceEventLeafMappingItem mappingItem) {
        return new Ethernet(mappingItem.GetReferenceName(), mappingItem.GetTargetPath(), mappingItem.GetCategory(),
                mappingItem.GetAccessType(), mappingItem.GetSystemIdentifier());
    }
    private static Ethernet buildEthernetFromJsonObject(JsonObject jsonObject) {
        return new Ethernet(
                JsonUtils.getStringAttribute(jsonObject, "Name"),
                JsonUtils.getStringAttribute(jsonObject, "Path"),
                JsonUtils.getStringAttribute(jsonObject, "Category"),
                JsonUtils.getStringAttribute(jsonObject, "AccessType"),
                JsonUtils.getStringAttribute(jsonObject, "SystemIdentifier")
        );
    }
}

