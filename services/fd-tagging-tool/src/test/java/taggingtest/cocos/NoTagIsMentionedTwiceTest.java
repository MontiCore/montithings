// (c) https://github.com/MontiCore/monticore
package taggingtest.cocos;

import org.junit.jupiter.api.Test;
import tagging.cocos.NoTagIsMentionedTwice;

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
