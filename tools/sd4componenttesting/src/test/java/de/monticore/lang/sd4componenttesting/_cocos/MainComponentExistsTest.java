package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import de.monticore.lang.sd4componenttesting._cocos.MainComponentExists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MainComponentExistsTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new MainComponentExists());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1010");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("NotExistingMainComponent.sd4c", 1, 1);
  }
}
