// (c) https://github.com/MontiCore/monticore
package montithings.services.fdtaggingtool.tagging;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import montithings.services.fdtaggingtool.tagging.cocos.FirstNameIsAFeature;
import montithings.services.fdtaggingtool.tagging.cocos.NoComponentIsMentionedTwiceInASingleTag;
import montithings.services.fdtaggingtool.tagging.cocos.NoTagIsMentionedTwice;
import montithings.services.fdtaggingtool.tagging.cocos.SecondNameIsAComponent;

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
