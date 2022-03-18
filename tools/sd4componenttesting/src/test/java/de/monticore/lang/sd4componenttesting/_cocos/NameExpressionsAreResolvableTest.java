// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class NameExpressionsAreResolvableTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new NameExpressionsAreResolvable());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1510");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("NameExpressionsAreNotResolvable.sd4c", 1, 1);
  }
}
