package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import de.monticore.lang.sd4componenttesting._cocos.SD4CConnectionValid;
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
    return Arrays.asList("0xSD4CPT1050");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("SD4CConnectionInvalid.sd4c", 1, 1);
  }
}
