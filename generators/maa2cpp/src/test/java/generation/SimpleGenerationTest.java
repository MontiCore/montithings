package generation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.montiarcautomaton.generator.MAAGeneratorTool;
import de.se_rwth.commons.logging.Log;

public class SimpleGenerationTest {

  private static final String MODELPATH = "src/test/resources/models/";
  private static final String HWCPATH = "src/test/resources/hwc/";
  private static final String TARGETPATH = "target/generated-test-sources/";



  @Before
  public void setup() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGeneration() throws IOException {
  //FileUtils.cleanDirectory(Paths.get(TARGETPATH).toFile());  
    MAAGeneratorTool script = new MAAGeneratorTool();
    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(HWCPATH).toFile());
  }


}
