// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import de.se_rwth.commons.logging.Log;
import montithings.AbstractTest;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings.util.MontiThingsError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TrafosTest extends AbstractTest {

  protected static final String TEST_PATH = Paths.get("models", "trafoTest").toString();

  protected static Path getModelPath(String modelPathName) {
    return Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName);
  }

  protected static Path getRecordingPath(String modelPathName) {
    return Paths.get(RELATIVE_MODEL_PATH, TEST_PATH, modelPathName, "Recording.json");
  }

  protected static Stream<Arguments> validExternalPortMockTrafoProvider() {

    return Stream.of(
      Arguments.of(
        Paths.get("sourceSensor").toString(),
        new ExternalPortMockTrafo(getModelPath("sourceSensor").toFile(),
          getRecordingPath("sourceSensor").toFile(), "sourceSensor.Example"),
        7,
        new HashSet<>(Arrays.asList("sourceSensor.SinkActuatorMock",
          "sourceSensor.SourceSensorMock"))
      ),
      Arguments.of(
        Paths.get("sourceSensor").toString(),
        new DelayedChannelTrafo(getModelPath("sourceSensor").toFile(),
          getRecordingPath("sourceSensor").toFile()),
        6,
        new HashSet<>(Arrays.asList("sourceSensor.SourceSensorExampleSourceSourceSensorExampleSinkDelay"))
      ),
      Arguments.of(
        Paths.get("sourceSensor").toString(),
        new DelayedComputationTrafo(getModelPath("sourceSensor").toFile(),
          getRecordingPath("sourceSensor").toFile()),
        9,
        new HashSet<>(Arrays.asList("sourceSensor.SourceSensorExampleSinkWrapper",
          "sourceSensor.SourceSensorExampleSourceWrapper",
          "sourceSensor.SourceSensorExampleSinkWrapperComputationDelay",
          "sourceSensor.SourceSensorExampleSourceWrapperComputationDelay"
          ))
      )
    );
  }

  @ParameterizedTest
  @MethodSource("validExternalPortMockTrafoProvider")
  public void shouldApplyExternalPortMockTrafo(@NotNull String modelPathName,
    MontiThingsTrafo trafo, int expNumModels, Set<String> addedCompsFqns) {
    //Given
    Log.init();
    MontiThingsTool tool = new MontiThingsTool();
    tool.addTrafo(trafo);

    //When
    IMontiThingsGlobalScope scope = tool.processModels(getModelPath(modelPathName));

    //Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(scope.getSubScopes().size()).isEqualTo(expNumModels);
    assertThat(scope.getSubScopes()).allMatch(s -> s instanceof MontiThingsArtifactScope);
    for (String compname : addedCompsFqns) {
      assertThat(scope.resolveComponentType(compname)).isPresent();
    }
  }

  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }
}
