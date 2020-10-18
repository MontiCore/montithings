// (c) https://github.com/MontiCore/monticore
package generation;

import de.se_rwth.commons.logging.Log;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.ConfigParams;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CppCodeTest {

  private static final Path MODELPATH = Paths.get("src/test/resources/models/CPPTests/");

  private static final Path HWCPATH = Paths.get("src/test/resources/hwc/CPPTests/");

  private static final Path TARGETPATH = Paths.get("target/generated-test-sources/");

  private static final Path RTEPATH = Paths.get("src/main/resources/rte/montithings-RTE");

  private static final Path TESTLIBPATH = Paths.get("src/main/resources/test");

  private static final Path TESTPATH = Paths.get("src/test/resources/gtests/");

  public ConfigParams setup(TestInfo testInfo) {
    Log.enableFailQuick(false);
    try {
      FileUtils.copyDirectoryToDirectory(RTEPATH.toFile(), Paths.get("target/generated-test-sources/",testInfo.getTestMethod().get().getName(),"generated-test-sources/").toFile());
      FileUtils.copyDirectoryToDirectory(TESTLIBPATH.toFile(), Paths.get("target/generated-test-sources/",testInfo.getTestMethod().get().getName(),"generated-test-sources/").toFile());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    ConfigParams params = new ConfigParams();
    params.setTargetPlatform(ConfigParams.TargetPlatform.GENERIC);
    params.setSplittingMode(ConfigParams.SplittingMode.OFF);
    return params;
  }

  @Test
  public void basicInputOutputTest(TestInfo testInfo) throws IOException {
    String packageName = "basicInputOutputTest";
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    script.generate(Paths.get(MODELPATH.toString(),packageName).toFile(), Paths.get(TARGETPATH.toString(),testInfo.getTestMethod().get().getName(),"generated-test-sources/").toFile(), Paths.get(HWCPATH.toString(),packageName).toFile(), Paths.get(TESTPATH.toString(),packageName).toFile(), setup(testInfo));
  }

  @Test
  public void classDiagramsTest(TestInfo testInfo) throws IOException {
    String packageName = "classDiagramsTest";
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    script.generate(Paths.get(MODELPATH.toString(), packageName).toFile(), Paths.get(TARGETPATH.toString(), testInfo.getTestMethod().get().getName(), "generated-test-sources/").toFile(), Paths.get(HWCPATH.toString(), packageName).toFile(), Paths.get(TESTPATH.toString(), packageName).toFile(), setup(testInfo));
  }

    @Test
  public void behaviorTest(TestInfo testInfo) throws IOException {
    String packageName = "behaviorTest";
    String reuseFiles = "basicInputOutputTest";
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    script.generate(Paths.get(MODELPATH.toString(),packageName).toFile(), Paths.get(TARGETPATH.toString(),testInfo.getTestMethod().get().getName(),"generated-test-sources/").toFile(), Paths.get(HWCPATH.toString(),reuseFiles).toFile(), Paths.get(TESTPATH.toString(),reuseFiles).toFile(), setup(testInfo));
  }

}
