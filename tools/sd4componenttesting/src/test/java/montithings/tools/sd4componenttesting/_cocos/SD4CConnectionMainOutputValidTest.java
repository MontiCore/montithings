// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import montithings.tools.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class SD4CConnectionMainOutputValidTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new SD4CConnectionMainOutputValid());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1070", "0xSD4CPT1080");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("SD4CConnectionMainOutputComponentGiven.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation2() {
    testCocoViolation("SD4CConnectionMainOutputUnknownPort.sd4c", 1, 1);
  }
}
