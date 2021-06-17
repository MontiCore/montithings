// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montithings.AbstractTest;
import montithings.MontiThingsMill;
import montithings.MontiThingsTool;
import montithings.MontiThingsTool2;
import montithings._parser.MontiThingsParser;
import montithings.util.MontiThingsError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SymbolTableTest extends AbstractTest {

  protected static final String TEST_PATH = Paths.get("models", "symbolTableTest").toString();

  protected static Stream<Arguments> validModelPathAndExpectedValuesProvider() {
    return Stream.of(
      Arguments.of(Paths.get("cd").toString(), 3),
      Arguments.of(Paths.get("ocl").toString(), 4)
    );
  }

  @Test
  public void toolTest() throws IOException {
    // Given
    MontiThingsMill.init();
    MontiThingsTool2 tool = new MontiThingsTool2();
    Path path = Paths.get(RELATIVE_MODEL_PATH, "montithings", "tool", "validExample", "valid");
    IMontiThingsGlobalScope scope = tool.processModels(path);
    ComponentTypeSymbol rootComp = scope.resolveComponentType("valid.Composed").get();
    System.out.println(rootComp.getFullName());
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @Test
  public void testest() throws IOException {
    //Given
    Log.init();
    MontiThingsMill.init();
    MontiThingsMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiThingsDeSer deser = new MontiThingsDeSer();
    String path = Paths.get("models", "symbolTableTest", "cd").toString();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, path, "Source.mt");
    MontiThingsFullSymbolTableCreator symTab = new MontiThingsFullSymbolTableCreator();

    //When
    MontiThingsParser parser = new MontiThingsParser();
    ASTMACompilationUnit compilationUnit = parser.parse(modelPath.toString()).get();
    IMontiThingsArtifactScope scope = symTab.createFromAST(compilationUnit);
    String result = deser.serialize(scope);
    System.out.println(result);
    //scope.resolveComponentType("valid.Source");
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("validModelPathAndExpectedValuesProvider")
  public void shouldProcessValidModels(@NotNull String modelPathName, int expNumModels) {
    //Given
    Log.init();
    MontiThingsTool2 tool = new MontiThingsTool2();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    IMontiThingsGlobalScope scope = tool.processModels(modelPath);
    scope.resolveComponentType("cd.Example");

    //Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(expNumModels).isEqualTo(scope.getSubScopes().size());
    assertThat(scope.getSubScopes()).allMatch(s -> s instanceof MontiThingsArtifactScope);
  }

  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }
}
