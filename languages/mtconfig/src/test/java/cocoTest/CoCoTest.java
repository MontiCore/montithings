// (c) https://github.com/MontiCore/monticore
package cocoTest;

import mtconfig.util.MTConfigError;

import java.util.regex.Pattern;

/**
 * Tests CoCos of MTConfig.
 *
 * @author Julian Krebber
 */
public class CoCoTest extends AbstractTest {
  private static final String PACKAGE = "cocoTest";

  private static final String MODEL_PATH = "src/test/resources/models/";

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MTConfigError.ERROR_CODE_PATTERN;
  }
}

