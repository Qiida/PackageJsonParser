package com.iav.et.data.step;

import com.google.gson.JsonObject;
import com.iav.et.data.expectation.Expectation;
import com.iav.et.data.expectation.ExpectationFactory;
import com.iav.et.data.signal.Signal;
import com.iav.et.data.signal.SignalFactory;
import com.iav.et.data.step.flow.Block;
import com.iav.et.data.step.flow.IfThenElse;
import com.iav.et.data.step.flow.Node;
import com.iav.et.data.step.flow.Loop;
import com.iav.et.data.step.flow.SwitchCase;
import com.iav.et.data.step.flow.Wait;
import com.iav.et.data.step.keyword.KeywordFactory;
import com.iav.et.data.step.signal.Read;
import com.iav.et.data.step.signal.Write;
import com.iav.utils.JsonUtils;
import com.iav.utils.LoggerUtils;
import de.tracetronic.tts.apiClient.stubs.*;
import de.tracetronic.tts.apiClient.stubs.Package;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

final public class StepFactory {

    public StepFactory() {}
    private static  final Logger LOGGER = LoggerUtils.getLOGGER();
    public static LinkedHashMap<String, Step> buildLinkedHashMapFromApiPackage(Package p) {
        LinkedHashMap<String, Step> steps = new LinkedHashMap<>();
        for (TestStep ts : p.GetTestSteps()) {
            try {
                Step step = matchAndBuildStepFromApiTestStep(ts);
                steps.put(step.createKey(), step);
            } catch (Exception exception) {
                LOGGER.severe(exception.getMessage());
                System.out.println("Line "+ts.GetLineNo()+" failed!");
            }
        }
        return steps;
    }

    public static LinkedHashMap<String, Step> buildLinkedHashMapFromJsonObject(JsonObject jsonObject) {
        LinkedHashMap<String, Step> steps = new LinkedHashMap<>();
        for (String key : jsonObject.keySet()) {
            try {
                Step step = matchAndBuildStepFromJsonObject(jsonObject.getAsJsonObject(key));
                assert step != null;
                steps.put(step.createKey(), step);
            } catch (StepInitializationException exception) {
                LOGGER.severe(exception.getMessage());
            }
        }
        return steps;
    }


    public static LinkedHashMap<String, Step> buildLinkedHashMapFromApiTestStepContainer(TestStepContainer tsContainer) {
        LinkedHashMap<String, Step> steps = new LinkedHashMap<>();
        for (TestStep ts : tsContainer.GetTestSteps()) {
            try {
                Step step = matchAndBuildStepFromApiTestStep(ts);
                steps.put(step.createKey(), step);
            } catch (StepInitializationException exception) {
                LOGGER.severe(exception.getMessage());
            } catch (Exception exception) {
                LOGGER.severe(exception.getMessage());
                System.out.println("Line "+ts.GetLineNo()+" failed!");
            }
        }
        return steps;
    }

    private static Step matchAndBuildStepFromApiTestStep(TestStep ts) throws StepInitializationException {
        Step step = null;
        try {
            switch (ts.GetType()) {
                case "TsBlock", "TsPreconditionBlock", "TsPostconditionBlock" -> step = buildBlockFromApi((TestStepContainer) ts);
                case "TsLoop" -> step = buildLoopFromApi((TsLoop) ts);
                case "TsSwitchCase" -> step = buildSwitchCaseFromApi((TsSwitchCase) ts);
                case "TsIfThenElse" -> step = buildIfThenElseFromApi((TsIfThenElse) ts);
                case "TsKeyword" -> step = KeywordFactory.buildKeywordFromApi((TsKeyword) ts);
                case "TsRead" -> step = buildReadFromApi((TsRead) ts);
                case "TsWrite" -> step = buildWriteFromApi((TsWrite) ts);
                case "TsWait" -> step = buildWaitFromApi((TsWait) ts);
                default -> step = buildDefaultStepFromApiTestStepCallAndLog(ts);
            };
        } catch (UnsupportedOperationException e) {
            LOGGER.warning(e.getMessage());
        }
        if (step == null) {
            throw new StepInitializationException("[" + ts.GetLineNo() + "] " + "Error parsing " + ts.GetType());
        } else {
            LOGGER.fine("[" + ts.GetLineNo() + "] " + ts.GetType() + " parsed");
        }
        return step;
    }

    private static Step buildDefaultStepFromApiTestStepCallAndLog(TestStep ts) {
//        LOGGER.warning("TestStep Type not implemented yet: " + ts.GetType());
        return buildDefaultStepFromApiTestStep(ts);
    }

    private static Step matchAndBuildStepFromJsonObject(JsonObject jsonObject) throws StepInitializationException {
        Step step = null;
        String type = JsonUtils.getStringAttribute(jsonObject, "Type");
        switch (type) {
            case "TsBlock", "TsPreconditionBlock", "TsPostconditionBlock" -> step = buildBlockFromJsonObject(jsonObject);
            case "TsLoop" -> step = buildLoopFromJsonObject(jsonObject);
            case "TsSwitchCase" -> step = buildSwitchCaseFromJsonObject(jsonObject);
            case "TsIfThenElse" -> step = buildIfThenElseFromJsonObject(jsonObject);
            case "TsKeyword" -> step = KeywordFactory.buildKeywordFromJsonObject(jsonObject);
            case "TsRead" -> step = buildReadFromJsonObject(jsonObject);
            case "TsWrite" -> step = buildWriteFromJsonObject(jsonObject);
//            case "TsWait" -> step = buildWaitFromJsonObject(jsonObject);
            case null -> {LOGGER.severe("JsonObject has no Type");}
            default -> step = buildDefaultStepFromJsonObject(jsonObject);
        }
        return step;
    }

    private static Block buildBlockFromApi(TestStepContainer tsContainer) {
        return new Block(
                buildDefaultStepFromApiTestStep(tsContainer),
                buildLinkedHashMapFromApiTestStepContainer(tsContainer)
        );
    }

    private static Block buildBlockFromJsonObject(JsonObject jsonObject) {
        return new Block(
                buildDefaultStepFromJsonObject(jsonObject),
                buildLinkedHashMapFromJsonObject(jsonObject.getAsJsonObject("Steps"))
        );
    }

    private static Step buildLoopFromApi(TsLoop tsLoop) {
        String count = tsLoop.GetLoopCountExpression();
        return new Loop(
                tsLoop.GetLineNo(),
                tsLoop.GetType(),
                "Loop(" + count + ")", buildLinkedHashMapFromApiTestStepContainer(tsLoop), count
        );
    }

    private static Step buildLoopFromJsonObject(JsonObject jsonObject) {
        Step step = buildDefaultStepFromJsonObject(jsonObject);
        String count = JsonUtils.getStringAttribute(jsonObject, "Count");
        LinkedHashMap<String, Step> stepLinkedHashMap = buildLinkedHashMapFromJsonObject(jsonObject.getAsJsonObject("Steps"));
        return new Loop(step, stepLinkedHashMap, count);
    }

    private static SwitchCase buildSwitchCaseFromApi(TsSwitchCase tsSwitchCase) {
        LinkedHashMap<String, Node> nodeLinkedHashMap = new LinkedHashMap<>();
        for (TsCaseNode tsCaseNode : tsSwitchCase.GetCaseNodes()) {
            Node nodeOfCase = buildNodeFromApi(tsCaseNode);
            nodeLinkedHashMap.put(nodeOfCase.getName(), nodeOfCase);
        }
        String switchValue = tsSwitchCase.GetSwitchValue();
        return new SwitchCase(
                tsSwitchCase.GetLineNo(), tsSwitchCase.GetType(),
                "Switch(" + switchValue + ")", nodeLinkedHashMap, switchValue
        );
    }

    private static SwitchCase buildSwitchCaseFromJsonObject(JsonObject jsonObject) {
        Step step = buildDefaultStepFromJsonObject(jsonObject);
        LinkedHashMap<String, Node> nodeLinkedHashMap = buildNodeLinkedHashMapFromJsonObject(jsonObject.getAsJsonObject("Cases"));
        String switchValue = JsonUtils.getStringAttribute(jsonObject, "Value");
        return new SwitchCase(step, nodeLinkedHashMap, switchValue);
    }


    private static LinkedHashMap<String, Node> buildNodeLinkedHashMapFromJsonObject(JsonObject jsonObject) {
        LinkedHashMap<String, Node> caseLinkedHashMap = new LinkedHashMap<>();
        for (String key : jsonObject.keySet()) {
            Node node = buildNodeFromJsonObject(jsonObject.getAsJsonObject(key));
            caseLinkedHashMap.put(node.createKey(), node);
        }
        return caseLinkedHashMap;
    }

    private static Node buildNodeFromJsonObject(JsonObject jsonObject) {
        Step step = buildDefaultStepFromJsonObject(jsonObject);
        return new Node(step, buildLinkedHashMapFromJsonObject(jsonObject.getAsJsonObject("Steps")));
    }


    private static IfThenElse buildIfThenElseFromApi(TsIfThenElse tsIfThenElse) {
        Node thenCase = buildNodeFromApi(tsIfThenElse.GetThenNode());
        Node elseCase = buildNodeFromApi(tsIfThenElse.GetElseNode());
        String condition = tsIfThenElse.GetCondition();
        return new IfThenElse(
                tsIfThenElse.GetLineNo(), tsIfThenElse.GetType(),
                "If(" + condition + ")", condition, thenCase, elseCase
        );
    }

    private static IfThenElse buildIfThenElseFromJsonObject(JsonObject jsonObject) {
        Step step = buildDefaultStepFromJsonObject(jsonObject);
        Node thenCase = buildNodeFromJsonObject(jsonObject.getAsJsonObject("[" + (step.getLine()+1) + "] Then"));
        String elseKey = null;
        for (String key : jsonObject.keySet()) {
            if (key.contains("Else")) {
                elseKey = key;
            }
        }
        Node elseCase = buildNodeFromJsonObject(jsonObject.getAsJsonObject(elseKey));
        String condition = JsonUtils.getStringAttribute(jsonObject, "Condition");
        return new IfThenElse(step, condition, thenCase, elseCase);
    }

    private static Node buildNodeFromApi(TestStepContainer tsCaseNode) {
        String name = tsCaseNode.GetActionColumnText().replaceFirst(" ", "");
        return new Node(tsCaseNode.GetLineNo(), tsCaseNode.GetType(), name, buildLinkedHashMapFromApiTestStepContainer(tsCaseNode));
    }

    private static Read buildReadFromApi(TsRead tsRead) throws UnsupportedOperationException {
        Signal signal = SignalFactory.buildSignalFromApi(tsRead);
        if (signal == null) {
            LOGGER.warning("["+tsRead.GetLineNo()+"] Signal "+tsRead.GetMappingItemReferenceName()+" -> parsing failed");
            signal = new Signal(tsRead.GetMappingItemReferenceName(), "*missing*", "*missing*", "*missing*", "*missing*");
        }
        Expectation expectation = null;
        try {
            expectation = ExpectationFactory.buildExpectationFromApi(tsRead.GetExpectation());
        } catch (UnsupportedOperationException e) {
            LOGGER.warning(e.getMessage());
        }
        return new Read(tsRead.GetLineNo(), tsRead.GetType(), "Read(" + signal.getName() + ")", signal, expectation);
    }

    private static Read buildReadFromJsonObject(JsonObject jsonObject) {
        Signal signal = SignalFactory.buildSignalFromJsonObject(jsonObject.getAsJsonObject("Signal"));
        Expectation expectation = null;
        if (jsonObject.has("Expectation")) {
            expectation = ExpectationFactory.buildExpectationFromString(JsonUtils.getStringAttribute(jsonObject, "Expectation"));
        }

//        Step step = buildDefaultStepFromJsonObject(jsonObject);
        int line = Integer.parseInt(jsonObject.get("Line").toString());
        String type = JsonUtils.getStringAttribute(jsonObject, "Type");
        String name = JsonUtils.getStringAttribute(jsonObject, "Name");
        return new Read(line, type, name, signal, expectation);
    }

    private static Write buildWriteFromApi(TsWrite tsWrite) throws UnsupportedOperationException {
        Signal signal = SignalFactory.buildSignalFromApi(tsWrite);
        if (signal == null) {
            LOGGER.warning("["+tsWrite.GetLineNo()+"] Signal "+tsWrite.GetMappingItemReferenceName()+" -> parsing failed");
            signal = new Signal(tsWrite.GetMappingItemReferenceName(), "*missing*", "*missing*", "*missing*", "*missing*");
        }
        return new Write(tsWrite.GetLineNo(), tsWrite.GetType(), "Write(" + signal.getName() + ")", signal);
    }

    private static Write buildWriteFromJsonObject(JsonObject jsonObject) {
        Signal signal = SignalFactory.buildSignalFromJsonObject(jsonObject.getAsJsonObject("Signal"));
        Step step = buildDefaultStepFromJsonObject(jsonObject);
        return new Write(step, signal);
    }

    private static Wait buildWaitFromApi(TsWait tsWait) {
        String duration = tsWait.GetParameterColumnText();
        return new Wait(tsWait.GetLineNo(), tsWait.GetType(), "Wait(" + duration + ")", duration);
    }
    private static Step buildDefaultStepFromApiTestStep(TestStep ts) {
        return new Step(ts.GetLineNo(), ts.GetType(), ts.GetActionColumnText());
    }

    private static Step buildDefaultStepFromJsonObject(JsonObject jsonObject) {
        int line = Integer.parseInt(jsonObject.get("Line").toString());
        String type = JsonUtils.getStringAttribute(jsonObject, "Type");
        String name = JsonUtils.getStringAttribute(jsonObject, "Name");
        return new Step(line, type, name);
    }
}
