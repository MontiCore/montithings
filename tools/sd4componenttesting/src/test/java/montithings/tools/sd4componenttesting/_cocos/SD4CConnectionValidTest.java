// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import montithings.tools.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SD4CConnectionValidTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new SD4CConnectionValid());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1050", "0xSD4CPT1060");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("SD4CConnectionNotValid.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation2() {
    testCocoViolation("SD4CConnectionWrongValueAmount.sd4c", 1, 1);
  }
}
