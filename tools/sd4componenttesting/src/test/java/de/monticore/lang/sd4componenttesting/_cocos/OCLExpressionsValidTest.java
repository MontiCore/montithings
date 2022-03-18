// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting.AbstractCoCoTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class OCLExpressionsValidTest extends AbstractCoCoTest {
  @Override
  protected void initCoCoChecker() {
    this.checker.addCoCo(new OCLExpressionsValid());
  }

  @Override
  protected List<String> getErrorCodeOfCocoUnderTest() {
    return Arrays.asList("0xSD4CPT1820", "0xSD4CPT1821", "0xSD4CPT1822", "0xSD4CPT1824", "0xSD4CPT1825", "0xSD4CPT1826", "0xSD4CPT1827", "0xSD4CPT1828", "0xSD4CPT1829", "0xSD4CPT1830", "0xSD4CPT1831", "0xSD4CPT1832", "0xSD4CPT1833", "0xSD4CPT1834", "0xSD4CPT1835", "0xSD4CPT1836");
  }

  @Test
  void testCocoViolation() {
    testCocoViolation("OCLExpressionsInvalid.sd4c", 0, 0);
  }
}
