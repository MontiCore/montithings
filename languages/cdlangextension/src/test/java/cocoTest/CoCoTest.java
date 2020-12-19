// (c) https://github.com/MontiCore/monticore
package cocoTest;

import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension.util.CDLangExtensionError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Tests CoCos of CDLangExtension.
 */
public class CoCoTest extends AbstractTest {

  private static final String MODEL_PATH = "src/test/resources/models/cocoTest/";

  @Override
  protected Pattern supplyErrorCodePattern() {
    return CDLangExtensionError.ERROR_CODE_PATTERN;
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("ImportValid/ImportValid.cde")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(
        "ImportNameNotEmpty/ImportNameNotEmpty.cde",
        new CDLangExtensionError[] { CDLangExtensionError.EMPTY_IMPORT_FIELD }),
      Arguments.of(
        "ImportNameUnique/ImportNameUnique.cde",
        new CDLangExtensionError[] { CDLangExtensionError.AMBIGUOUS_IMPORT_NAME }),
      Arguments.of(
        "ImportLanguageUnique/ImportLanguageUnique.cde",
        new CDLangExtensionError[] { CDLangExtensionError.AMBIGUOUS_LANGUAGE_NAME }),
      Arguments.of(
        "ImportNameExists/ImportNameExists.cde",
        new CDLangExtensionError[] { CDLangExtensionError.MISSING_IMPORT_NAME })
    );
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("validInput")
  public void shouldAcceptValidInput(String fileName) {
    // Accepting means not finding errors
    shouldRejectInvalidInput(fileName, new CDLangExtensionError[]{});
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidInput")
  public void shouldRejectInvalidInput(String fileName, CDLangExtensionError[] expectedFindings) {
    // Given
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();

    // When
    checker.checkAll(getAST(MODEL_PATH, fileName));

    // Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), expectedFindings);
  }
}