package taggingtest.cocos;

import org.junit.jupiter.api.Test;
import tagging.cocos.SecondNameIsAComponent;
import taggingtest.AbstractTest;

import java.util.Arrays;
import java.util.List;

public class SecondNameIsAComponentTest extends AbstractTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new SecondNameIsAComponent());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xTAG0004");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("NoComponentPresent/NoComponentPresent", 1, 1);
  }

}
