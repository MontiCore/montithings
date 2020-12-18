// (c) https://github.com/MontiCore/monticore
package montithings;

import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.IMontiArcGlobalScope;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.MontiThingsArtifactScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static montithings.AbstractTest.RELATIVE_MODEL_PATH;

/**
 * @author kirchhof
 */
class MontiThingsToolTest {
  protected static final String TEST_PATH = Paths.get("montithings", "tool").toString();

  protected static Stream<Arguments> validModelPathAndExpectedValuesProvider() {
    return Stream.of(Arguments.of(Paths.get("validExample").toString(), 3));
  }

  /**
   * Method under test {@link MontiArcTool#processModels(Path...)}.
   */
  @ParameterizedTest
  @MethodSource("validModelPathAndExpectedValuesProvider")
  public void shouldProcessValidModels(@NotNull String modelPathName, int expNumModels) {
    //Given
    MontiThingsTool tool = new MontiThingsTool();
    Path modelPath = Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);

    //When
    IMontiThingsGlobalScope scope = tool.processModels(modelPath);
    scope.resolveComponentType("valid.Composed");

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(expNumModels, scope.getSubScopes().size());
    Assertions.assertTrue(scope.getSubScopes().stream().allMatch(s -> s instanceof MontiThingsArtifactScope));
  }
}