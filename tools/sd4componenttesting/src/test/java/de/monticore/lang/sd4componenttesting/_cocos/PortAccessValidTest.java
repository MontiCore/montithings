package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import de.monticore.lang.sd4componenttesting._cocos.PortAccessValid;
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
    return Arrays.asList("0xSD4CPT1040");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("PortAccessInvalid.sd4c", 1, 1);
  }
}
