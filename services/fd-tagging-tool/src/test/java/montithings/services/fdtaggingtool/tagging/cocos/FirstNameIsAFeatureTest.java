// (c) https://github.com/MontiCore/monticore
package montithings.services.fdtaggingtool.tagging.cocos;

import montithings.services.fdtaggingtool.tagging.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class FirstNameIsAFeatureTest extends AbstractTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new FirstNameIsAFeature());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xTAG0002");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("NoFeaturePresent/NoFeaturePresent", 1, 1);
  }

}
