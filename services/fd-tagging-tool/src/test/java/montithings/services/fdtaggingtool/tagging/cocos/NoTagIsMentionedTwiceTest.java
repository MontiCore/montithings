// (c) https://github.com/MontiCore/monticore
package montithings.services.fdtaggingtool.tagging.cocos;

import montithings.services.fdtaggingtool.tagging.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class NoTagIsMentionedTwiceTest extends AbstractTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new NoTagIsMentionedTwice());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xTAG0005");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("TwoSameTags/TwoSameTags", 1, 1);
  }

}
