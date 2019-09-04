package generation;

import de.montiarcautomaton.generator.MontiThingsGeneratorTool;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class SimpleGenerationTest {

  private static final String MODELPATH = "src/test/resources/models/";
  private static final String HWCPATH = "src/test/resources/hwc/";
  private static final String TARGETPATH = "target/generated-test-sources/";

  @Before
  public void setup() {
    Log.enableFailQuick(false);
    try {
      FileUtils.copyDirectoryToDirectory(Paths.get("src/main/resources/rte/montithings-RTE").toFile(),
              Paths.get("target/").toFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGeneration() throws IOException {
    // FileUtils.cleanDirectory(Paths.get(TARGETPATH).toFile());
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    System.out.println(Paths.get(MODELPATH).toAbsolutePath().toString());
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(HWCPATH).toFile());

  }

}
