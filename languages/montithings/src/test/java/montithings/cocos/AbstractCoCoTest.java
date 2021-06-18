// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montithings.AbstractTest;
import montithings.MontiThingsTool;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.util.MontiThingsError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class AbstractCoCoTest extends AbstractTest {

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

  public ComponentTypeSymbol getSymbol(String componentName) {
    MontiThingsTool tool = new MontiThingsTool(new MontiThingsCoCoChecker());
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