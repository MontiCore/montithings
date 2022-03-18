// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class SD4CConnectionMainInputValidTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new SD4CConnectionMainInputValid());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1090", "0xSD4CPT1100");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("SD4CConnectionMainInputComponentGiven.sd4c", 1, 1);
  }

  @Test
  void testCocoViolation2() {
    testCocoViolation("SD4CConnectionMainInputUnknownPort.sd4c", 1, 1);
  }
}
