// (c) https://github.com/MontiCore/monticore
package montithings._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsTool;
import montithings._parser.MontiThingsParser;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.util.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractTest {

  private static final String MODEL_PATH = "src/test/resources/models/";
  private Pattern errorCodePattern;

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

  protected abstract Pattern supplyErrorCodePattern();

  protected Pattern getErrorCodePattern() {
    return errorCodePattern;
  }

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
    MontiThingsTool tool = new MontiThingsTool(new MontiThingsCoCoChecker(), new CD4CodeCoCoChecker());
    Path p = Paths.get("src", "test", "resources", "models");
    IMontiThingsGlobalScope scope = tool.processModels(p);
    ComponentTypeSymbol typeSymbol = scope.resolveComponentType(componentName).get();
    Log.init();
    LogStub.init();
    return typeSymbol;
  }
}