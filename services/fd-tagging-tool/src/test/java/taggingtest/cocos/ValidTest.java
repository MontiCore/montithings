// (c) https://github.com/MontiCore/monticore
package taggingtest.cocos;

import org.junit.jupiter.api.Test;
import tagging.cocos.FirstNameIsAFeature;
import tagging.cocos.NoComponentIsMentionedTwiceInASingleTag;
import tagging.cocos.NoTagIsMentionedTwice;
import tagging.cocos.SecondNameIsAComponent;
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
  void testCorrect() {
    testCorrectExamples("ValidCombination/BasicValidTest");
  }
}
