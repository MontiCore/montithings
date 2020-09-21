// (c) https://github.com/MontiCore/monticore
package generation;

import de.se_rwth.commons.logging.Log;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.ConfigParams;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleGenerationTest {

  private static final Path MODELPATH = Paths.get("src/test/resources/models/hierarchy/");

  private static final Path HWCPATH = Paths.get("src/test/resources/hwc/hierarchy/");

  private static final Path TARGETPATH = Paths.get("target/generated-test-sources/");

  private static final Path RTEPATH = Paths.get("src/main/resources/rte/montithings-RTE");

  @Before
  public void setup() {
    Log.enableFailQuick(false);
    try {
      FileUtils.copyDirectoryToDirectory(RTEPATH.toFile(), Paths.get("target/").toFile());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void generatorDoesNotCrash() throws IOException {
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    System.out.println(MODELPATH.toAbsolutePath().toString());
    ConfigParams params = new ConfigParams();
    params.setTargetPlatform(ConfigParams.TargetPlatform.GENERIC);
    script.generate(MODELPATH.toFile(), TARGETPATH.toFile(), HWCPATH.toFile(), null,
        params);
  }
}
