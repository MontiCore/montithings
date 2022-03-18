// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class FieldAccessExpressionsAreResolvableTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new FieldAccessExpressionsAreResolvable());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1837", "0xSD4CPT1838");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("FieldAccessExpressionsAreNotResolvable.sd4c", 1, 1);
  }
}
