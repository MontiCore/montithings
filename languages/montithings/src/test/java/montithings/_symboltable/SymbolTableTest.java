// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montithings.AbstractTest;
import montithings.MontiThingsTool;
import montithings.util.MontiThingsError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SymbolTableTest extends AbstractTest {

  protected static final String TEST_PATH = Paths.get("models", "symbolTableTest").toString();

  protected static Stream<Arguments> validModelPathAndExpectedValuesProvider() {
    return Stream.of(
      Arguments.of(Paths.get("cd").toString(), "cd.Example", 6),
      Arguments.of(Paths.get("ocl").toString(), "ocl.Example", 6)
    );
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("validModelPathAndExpectedValuesProvider")
  public void shouldProcessValidModels(@NotNull String modelPathName, @NotNull String componentName,
    int expNumModels) {
    //Given
    Log.init();
    MontiThingsTool tool = new MontiThingsTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    IMontiThingsGlobalScope scope = tool.processModels(modelPath);

    //Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(scope.resolveComponentType(componentName)).isPresent();
    assertThat(scope.getSubScopes().size()).isEqualTo(expNumModels);
    assertThat(scope.getSubScopes()).allMatch(s -> s instanceof MontiThingsArtifactScope);
  }

  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }
}
