package com.iav.et.data.expectation;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpectationFactory {
    public static Expectation buildExpectationFromApi(de.tracetronic.tts.apiClient.stubs.Expectation e) throws UnsupportedOperationException {
        Expectation expectation = null;
        if (e != null) {
            switch (e) {
                case de.tracetronic.tts.apiClient.stubs.StringExpectation stringExpectation ->
                        expectation = new StringExpectation(stringExpectation.GetExpression());
                case de.tracetronic.tts.apiClient.stubs.NumericExpectation numericExpectation ->
                        expectation = new NumericExpectation(
                                numericExpectation.GetExpression(),
                                numericExpectation.GetRelation(),
                                numericExpectation.GetToleranceValue(),
                                numericExpectation.GetToleranceType()
                        );
                case de.tracetronic.tts.apiClient.stubs.NumericExpressionExpectation numericExpectation ->
                        expectation = new NumericExpectation(
                                numericExpectation.GetExpression(),
                                null,
                                null,
                                null
                        );
                case de.tracetronic.tts.apiClient.stubs.StringListExpectation stringListExpectation ->
                        expectation = new StringListExpectation(
                                stringListExpectation.GetStringList()
                        );
                case de.tracetronic.tts.apiClient.stubs.PresentExpectation ignored ->
                        expectation = new PresentExpectation();
                case de.tracetronic.tts.apiClient.stubs.AllExpectation defaultExpectation ->
                        expectation = new Expectation(defaultExpectation.GetExpectationExpression());
                default -> {
                }
            }
            if (expectation == null) {
                throw new UnsupportedOperationException("Expectation Type not implemented yet: " + e.getClass().getSimpleName());
            }

        }
        return expectation;
    }

    public static Expectation buildExpectationFromString(String expectation) {
        // TODO: this method is quite messy and could cause some trouble. Implement this cleaner.
        if (Objects.equals(expectation, "value == ~Present~")) {
            return new PresentExpectation();
        }
        Pattern stringListPattern = Pattern.compile("^value in \\[(.+)]");
        Matcher stringListMatcher = stringListPattern.matcher(expectation);
        if (stringListMatcher.find()) {
            String[] strings = stringListMatcher.group(1).split(",");
            for (int i=0; i<strings.length; i++) {
                strings[i] = strings[i].strip().replace("'", "");
            }
            return new StringListExpectation(strings);
        }
        Pattern stringPattern = Pattern.compile("^value == ('.*')");
        Matcher stringMatcher = stringPattern.matcher(expectation);
        if (stringMatcher.find()) {
            return new StringExpectation(stringMatcher.group(1));
        }
        Pattern numericTolerancePattern = Pattern.compile("(\\w+-value)");
        Matcher numericToleranceMatcher = numericTolerancePattern.matcher(expectation);
        String toleranceType = null;
        String tolerance = null;
        if (numericToleranceMatcher.find()) {
            toleranceType = numericToleranceMatcher.group(1);
            String[] splits = expectation.split("\\+/-");
            tolerance = splits[1].split(" ")[0];
            expectation = splits[0];
        }
        Pattern numericPattern = Pattern.compile("^value ([=<>]{1,2}) (.*)");
        Matcher numericMatcher = numericPattern.matcher(expectation);
        if (numericMatcher.find()) {
            return new NumericExpectation(
                    numericMatcher.group(2),
                    numericMatcher.group(1),
                    tolerance,
                    toleranceType
            );
        }
        return null;
    }


}
