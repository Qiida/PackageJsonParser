package com.iav.et.data.etpackage;

import com.google.gson.JsonObject;
import com.iav.et.api.Api;
import com.iav.et.data.step.Step;
import com.iav.et.data.step.StepFactory;
import com.iav.et.data.step.keyword.KeywordType;
import com.iav.et.mapping.LoMa;
import com.iav.et.workspace.Workspace;
import com.iav.jira.JiraIntegration;
import com.iav.jira.JiraIssue;
import com.iav.mafi.Mafi;
import com.iav.utils.FileUtils;
import com.iav.utils.JsonUtils;
import com.iav.utils.LoggerUtils;
import com.iav.utils.StringCombinations;
import de.tracetronic.tts.apiClient.stubs.Package;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.logging.Logger;


/**
 * The EtPackageFactory provides static methods to build EtPackage Instances
 * @author Qida
 */
final public class EtPackageFactory {
    private EtPackageFactory() {}
    private static  final Logger LOGGER = LoggerUtils.getLOGGER();

    public static EtPackage buildPackageFromPackagePath(String path) throws NoSuchFileException {
        Package p = Api.openPackage(path);
        return buildPackageFromApi(p);
    }

    public static EtPackage buildPackageFromApi(Package apiPackage) {
        LoMa.setUp(apiPackage);
        LinkedHashMap<String, Step> steps = StepFactory.buildLinkedHashMapFromApiPackage(apiPackage);
        EtPackage etPackage = null;
        try {
            String name = apiPackage.GetName();
            String id = apiPackage.GetTestScriptId();
            String path = FileUtils.getRelativePath(Workspace.getINSTANCE().getPath(), apiPackage.GetFilename());
            if (isKeywordImplementation(apiPackage)) {
                Mafi mafi = Mafi.open(Workspace.getINSTANCE().getMafiPath());
                String key = path.replace("\\", "/").replace("Packages/", "");
                BigInteger bigInteger = mafi.pathIdHashMap().get(key);
                if (bigInteger != null) {
                    id = bigInteger.toString();
                }
            }
            if (Objects.equals(id, "") || id == null) {
                LOGGER.warning(name+" missing ID");
            }
            String version = apiPackage.GetVersion();
            etPackage = new EtPackage(name, id, version, path , steps);
        } catch (Exception exception) {
            LOGGER.severe(exception.getMessage());
        }
        if (etPackage != null) {
            LOGGER.info(etPackage.getName() + " parsed");
        } else {
            LOGGER.severe(apiPackage.GetName() + " failed");
        }
        findAndAttachJiraIssueKey(etPackage);
        return etPackage;
    }

    private static void findAndAttachJiraIssueKey(EtPackage etPackage) {
        if (JiraIntegration.getINSTANCE() != null) {
            if (etPackage != null) {
                if (etPackage.isKeywordImplementation(Workspace.getType())) {
                    switch (etPackage.getKeywordType(Workspace.getType())) {
                        case CONTROL -> findAndAttachJiraIssueKeyForKeyword(etPackage, JiraIntegration.getControlSummaryIssueHashMap());
                        case STATUS ->  findAndAttachJiraIssueKeyForKeyword(etPackage, JiraIntegration.getStatusSummaryIssueHashMap());
                        case ESTABLISH -> findAndAttachJiraIssueKeyForKeyword(etPackage, JiraIntegration.getEstablishSummaryIssueHashMap());
                    }
                } else {
                    findAndAttachJiraIssueKeyForTestcase(etPackage);
                }
                if (etPackage.getJiraIssueKey() == null) {
                    LOGGER.warning("JiraIssueKey not found!");
                }
            }
        }
    }

    private static void findAndAttachJiraIssueKeyForKeyword(EtPackage etPackage, HashMap<String, JiraIssue> summaryIssueHashMap) {
        for (String summary : summaryIssueHashMap.keySet()) {
            String[] splits = summary.split("_");
            String ending = splits[splits.length-1];
            String[] splitsOf2 = summary.split("_", 2);
            String summaryWithoutID = splitsOf2[1];
            String summaryWithoutIdAndEnding = summaryWithoutID.replace("_" + ending, "");
            String lowerEtPackageName = etPackage.getName().toLowerCase();
            String lowerEtPackageNameWithoutEnding = "";
            if (lowerEtPackageName.endsWith("_tsp_ees25")) {
                lowerEtPackageNameWithoutEnding = lowerEtPackageName.replace("_tsp_ees25", "");
            } else if (lowerEtPackageName.endsWith("_ees25")) {
                lowerEtPackageNameWithoutEnding = lowerEtPackageName.replace("_ees25", "");
            }
            if (Objects.equals(lowerEtPackageNameWithoutEnding, summaryWithoutIdAndEnding.toLowerCase())) {
                JiraIssue jiraIssue = summaryIssueHashMap.get(summary);
                LOGGER.info("0 JiraIssueKey found: "+jiraIssue.getKey());
                etPackage.setJiraIssueKey(jiraIssue.getKey());
            } else {
                String[] summaryWithoutIdAndEndingSplits = summaryWithoutIdAndEnding.split("_");
                List<String> combos = StringCombinations.getCombinations(summaryWithoutIdAndEndingSplits);
                for (String combo : combos) {
                    if (Objects.equals(lowerEtPackageNameWithoutEnding, combo.toLowerCase())) {
                        JiraIssue jiraIssue = summaryIssueHashMap.get(summary);
                        LOGGER.info("1 JiraIssueKey found: "+jiraIssue.getKey());
                        etPackage.setJiraIssueKey(jiraIssue.getKey());
                        break;
                    }
                }
            }
        }
    }

    private static void findAndAttachJiraIssueKeyForTestcase(EtPackage etPackage) {
        HashMap<String, JiraIssue> summaryIssueHashMap = JiraIntegration.getTestcaseSummaryIssueHashMap();
        for (String summary : summaryIssueHashMap.keySet()) {
            String[] splits = summary.split("_", 2);
            String summaryID = splits[0];
            if (Objects.equals(summaryID, etPackage.getId())) {
                JiraIssue jiraIssue = summaryIssueHashMap.get(summary);
                LOGGER.info("JiraIssueKey found: "+jiraIssue.getKey());
                etPackage.setJiraIssueKey(jiraIssue.getKey());
            }
        }
    }

    private static boolean isKeywordImplementation(Package apiPackage) {
        String lowerFileName = apiPackage.GetFilename().toLowerCase();
        return switch (Workspace.getType()) {
            case BK25 -> (lowerFileName.contains("kwi")
                    && (lowerFileName.contains("control")
                    || lowerFileName.contains("status")
                    || lowerFileName.contains("establish")));
            case SP21 -> (lowerFileName.contains("03_schluesselwoerter")
                    && (lowerFileName.contains("steuern")
                    || lowerFileName.contains("status")
                    || lowerFileName.contains("herstellen")));
        };
    }

    public static EtPackage buildFromJsonFile(String path) throws FileNotFoundException {
        return buildPackageFromJsonObject(JsonUtils.parseJsonFile(path));
    }

    public static EtPackage buildPackageFromJsonObject(JsonObject jsonObject) {
        String name = JsonUtils.getStringAttribute(jsonObject, "Name");
        String id = JsonUtils.getStringAttribute(jsonObject, "Id");
        String version = JsonUtils.getStringAttribute(jsonObject, "Version");
        String path = JsonUtils.getStringAttribute(jsonObject, "Path");
        LinkedHashMap<String, Step> steps = StepFactory.buildLinkedHashMapFromJsonObject(jsonObject.getAsJsonObject("Steps"));
        return new EtPackage(name, id, version, path, steps);
    }

    public static List<EtPackage> buildListFromDirectoryWithJsonFiles(String path) throws FileNotFoundException {
        List<EtPackage> packages = new ArrayList<>();
        List<JsonObject> jsonObjects = new ArrayList<>();
        List<String> jsonFilePaths = FileUtils.findFilesByTypeRecursively(path, ".json");
        for (String jsonFilePath : jsonFilePaths) {
            JsonObject parsed = JsonUtils.parseJsonFile(jsonFilePath);
            jsonObjects.add(parsed);
        }
        for (JsonObject jsonObject : jsonObjects) {
            packages.add(buildPackageFromJsonObject(jsonObject));
        }
        return packages;
    }

//    public static HashMap<String, EtPackage> buildHashMapFromDirectoryWithJsonFiles(String path) throws FileNotFoundException {
//        List<EtPackage> packages = buildListFromDirectoryWithJsonFiles(path);
//        HashMap<String, EtPackage> hashMap = new HashMap<>();
//        for (EtPackage etPackage : packages) {
//            if (!hashMap.containsKey(etPackage.getPath())) {
//                hashMap.put(etPackage.getPath(), etPackage);
//            } else {
//                System.out.println("There you are!");
//                // TODO: EtPackageFactory must implement multiple methods to enable to parse single file, from a directory and recursive from directory
//            }
//        }
//        return hashMap;
//    }

}
