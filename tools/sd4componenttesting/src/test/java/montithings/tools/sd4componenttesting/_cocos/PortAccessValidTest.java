// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import montithings.tools.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PortAccessValidTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new PortAccessValid());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1040", "0xSD4CPT1030");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("PortAccessInvalid.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation2() {
    testCocoViolation("PortAccessInvalidUnknownComponentInstance.sd4c", 2, 2);
  }
}
