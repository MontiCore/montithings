package de.monticore.lang.sd4componenttesting.coco;

import arcbasis._cocos.ComponentInstanceTypeExists;
import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ComponentInstanceExists extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new ComponentInstanceTypeExists());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return null;
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("MainTest.sd4c", 0, 0);
  }
}
