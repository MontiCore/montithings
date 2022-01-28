package taggingtest.java.cocos;

import org.junit.jupiter.api.Test;
import tagging.cocos.*;
import taggingtest.AbstractTest;

import java.util.Arrays;
import java.util.List;

public class ValidTest extends AbstractTest {
    @Override
    protected void initCoCoChecker() {
        this.checker.addCoCo(new FirstNameIsAFeature());
        this.checker.addCoCo(new SecondNameIsAComponent());
        this.checker.addCoCo(new NoTagIsMentionedTwice());
        this.checker.addCoCo(new NoComponentIsMentionedTwiceInASingleTag());
    }

    @Override
    protected List<String> getErrorCodeOfCocoUnderTest() {
        return Arrays.asList("0xTAG0000");
    }
    @Test
    /*void testCorrect() {
        testCorrectExamples("ValidCombination/BasicValidTest");
    }*/
    void testCorrect() {
        testCorrectExamples("SmartHomeProject/SmartHome");
    }
}
