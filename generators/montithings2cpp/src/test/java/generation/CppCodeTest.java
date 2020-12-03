// (c) https://github.com/MontiCore/monticore
package generation;

import de.se_rwth.commons.logging.Log;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.codegen.MTGenerator;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

  public ConfigParams setup(String packageName) {
    Log.enableFailQuick(false);
    try {
      FileUtils.copyDirectoryToDirectory(RTEPATH.toFile(),
        Paths.get("target/generated-test-sources/",
          packageName, "generated-test-sources/").toFile());
      FileUtils.copyDirectoryToDirectory(TESTLIBPATH.toFile(),
        Paths.get("target/generated-test-sources/",
          packageName, "generated-test-sources/").toFile());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    ConfigParams params = new ConfigParams();
    params.setTargetPlatform(ConfigParams.TargetPlatform.GENERIC);
    params.setSplittingMode(ConfigParams.SplittingMode.OFF);
    params.setHwcTemplatePath(Paths.get(HWCPATH.toString(),packageName));
    return params;
  }

  @ParameterizedTest
  @ValueSource(strings = {
    //"basicInputOutputTest",
    "additionalPortTest"//,
    /*"behaviorTest",
    "classDiagramsTest",
    "prePostConditionsTest",
    "interfaceComponentsTest",
    "interfaceComponentsBindingTest"*/ })
  public void CPPTests(String testName) {
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    script.generate(
      Paths.get(MODELPATH.toString(), testName).toFile(),
      Paths.get(TARGETPATH.toString(), testName, "generated-test-sources/").toFile(),
      Paths.get(HWCPATH.toString(), testName).toFile(),
      Paths.get(TESTPATH.toString(), testName).toFile(),
      setup(testName));
    MTGenerator.generateTestScript(Paths.get(TARGETPATH.toString()).toFile(), setup(testName));
  }

}
