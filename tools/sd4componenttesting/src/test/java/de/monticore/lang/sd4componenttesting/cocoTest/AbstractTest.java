// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.cocoTest;

import de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
import de.monticore.lang.sd4componenttesting._parser.SD4ComponentTestingParser;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingGlobalScope;
import de.monticore.lang.sd4componenttesting.util.Error;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractTest {

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

  public ASTSD4Artifact getAST(String modelPath, String fileName) {
    ASTSD4Artifact SD4ArtifactAST = null;
    try {
      SD4ArtifactAST = new SD4ComponentTestingParser().parseSD4Artifact(modelPath + fileName).orElse(null);
    }
    catch (IOException e) {
      Log.error("File '" + modelPath + fileName + "' SD4Artifact artifact was not found");
    }
    Assertions.assertNotNull(SD4ArtifactAST);
    SD4ComponentTestingTool tool = new SD4ComponentTestingTool();

    System.out.println("jhgjhgjhg");
    System.out.println(SD4ArtifactAST);
    System.out.println("cbeehdebwde");
    ISD4ComponentTestingGlobalScope sc = tool.createSymboltable(SD4ArtifactAST, new File(modelPath));
    System.out.println(sc);
    return SD4ArtifactAST;
  }
}
