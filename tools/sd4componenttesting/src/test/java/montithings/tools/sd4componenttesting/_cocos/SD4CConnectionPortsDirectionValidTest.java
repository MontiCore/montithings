// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import montithings.tools.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class SD4CConnectionPortsDirectionValidTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new SD4CConnectionPortsDirectionValid());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1100", "0xSD4CPT1110", "0xSD4CPT1080", "0xSD4CPT1120");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("SD4CConnectionMainInputUnknownPort.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation2() {
    testCocoViolation("SD4CConnectionSourceUnknownPort.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation3() {
    testCocoViolation("SD4CConnectionMainOutputUnknownPort.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation4() {
    testCocoViolation("SD4CConnectionTargetUnknownPort.sd4c", 1, 1);
  }
}
