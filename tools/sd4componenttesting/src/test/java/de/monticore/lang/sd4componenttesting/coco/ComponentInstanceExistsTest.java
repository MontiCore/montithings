package de.monticore.lang.sd4componenttesting.coco;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import de.monticore.lang.sd4componenttesting._cocos.ComponentInstanceExistsCoCo;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ComponentInstanceExistsTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new ComponentInstanceExistsCoCo());
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
