// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montithings.MontiThingsTool;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.util.Error;
import montithings.util.MontiThingsError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCoCoTest {

  protected Pattern errorCodePattern;

  @BeforeAll
  public static void cleanUpLog() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    LogStub.init();
  }

  @BeforeEach
  public void setUp() {
    Log.getFindings().clear();
    errorCodePattern = supplyErrorCodePattern();
    assert errorCodePattern != null;
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("validInput")
  void shouldAcceptValidInput(String symbolName) {
    // Accepting means not rejecting when testing against all CoCos
    MontiThingsCoCoChecker checker = MontiThingsCoCos.createChecker();
    shouldRejectInvalidInput(checker, symbolName, 0, new MontiThingsError[] {});
  }

  @ParameterizedTest(name = "[{index}] {1}, {2}")
  @MethodSource("invalidInput")
  public void shouldRejectInvalidInput(
    // Given
    MontiThingsCoCoChecker checker, String symbolName,
    int expectedErrorCount, MontiThingsError[] expectedFindings) {

    // When
    checker.checkAll(getSymbol(symbolName).getAstNode());

    // Then
    Assertions.assertEquals(expectedErrorCount, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), expectedFindings);
  }

  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }

  protected Pattern getErrorCodePattern() {
    return errorCodePattern;
  }

  /* ============================================================ */
  /* ========================== HELPERS ========================= */
  /* ============================================================ */

  protected void checkExpectedErrorsPresent(List<Finding> findings,
    Error[] expErrors) {
    List<String> actualErrorCodes = collectErrorCodes(findings);
    List<String> expErrorCodes = collectErrorCodes(expErrors);

    Assertions.assertTrue(actualErrorCodes.containsAll(expErrorCodes), String.format("Expected "
      + "error codes: " + expErrorCodes.toString() + " Actual error codes: "
      + actualErrorCodes.toString()));
  }

  protected void checkNoAdditionalErrorsPresent(List<Finding> findings,
    Error[] expErrors) {
    List<String> actualErrorCodes = collectErrorCodes(findings);
    List<String> expErrorCodes = collectErrorCodes(expErrors);

    actualErrorCodes.removeAll(expErrorCodes);

    Assertions.assertEquals(0, actualErrorCodes.size());
  }

  protected void checkOnlyExpectedErrorsPresent(List<Finding> findings,
    Error[] expErrors) {
    checkExpectedErrorsPresent(findings, expErrors);
    checkNoAdditionalErrorsPresent(findings, expErrors);
  }

  protected List<String> collectErrorCodes(Error[] errors) {
    return Arrays.stream(errors)
      .map(Error::getErrorCode)
      .collect(Collectors.toList());
  }

  protected List<String> collectErrorCodes(List<Finding> findings) {
    return findings.stream()
      .map(f -> collectErrorCodes(f.getMsg()))
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  protected List<String> collectErrorCodes(String msg) {
    List<String> errorCodes = new ArrayList<>();
    Matcher matcher = getErrorCodePattern().matcher(msg);
    while (matcher.find()) {
      errorCodes.add(matcher.group());
    }
    return errorCodes;
  }

  public ComponentTypeSymbol getSymbol(String componentName) {
    MontiThingsTool tool = new MontiThingsTool(new MontiThingsCoCoChecker(),
      new CD4CodeCoCoChecker());
    Path p = Paths.get("src", "test", "resources", "models");
    IMontiThingsGlobalScope scope = tool.processModels(p);
    ComponentTypeSymbol typeSymbol = scope.resolveComponentType(componentName).get();
    Log.init();
    LogStub.init();
    return typeSymbol;
  }


  /* ============================================================ */
  /* ===================== WARNING SILENCERS ==================== */
  /* ============================================================ */

  // Useless method to silence warnings. Shall be provided by subclasses.
  protected static Stream<Arguments> validInput() {
    return Stream.<Arguments>builder().build();
  }

  // Useless method to silence warnings. Shall be provided by subclasses.
  protected static Stream<Arguments> invalidInput() {
    return Stream.<Arguments>builder().build();
  }
}