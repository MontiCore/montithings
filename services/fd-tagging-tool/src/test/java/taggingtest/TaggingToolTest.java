// (c) https://github.com/MontiCore/monticore
package taggingtest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tagging.cocos.FirstNameIsAFeature;
import tagging.cocos.NoComponentIsMentionedTwiceInASingleTag;
import tagging.cocos.NoTagIsMentionedTwice;
import tagging.cocos.SecondNameIsAComponent;

import java.util.Arrays;
import java.util.List;

public class TaggingToolTest extends AbstractTest {
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

  @Disabled
  @Test
  public void testIsPossible() {
    testValidToolCombination("SmartHomeProject/SmartHome", "SmartHomeProject/TestConfiguration.fc");
  }

}
