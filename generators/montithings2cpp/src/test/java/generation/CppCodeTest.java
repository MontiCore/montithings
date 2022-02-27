// (c) https://github.com/MontiCore/monticore
package generation;

import de.se_rwth.commons.logging.Log;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.MTGenerator;
import montithings.generator.config.ConfigParams;
import montithings.generator.config.SplittingMode;
import montithings.generator.config.TargetPlatform;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CppCodeTest {

  protected static final Path MODELPATH = Paths.get("src/test/resources/models/CPPTests/");

  protected static final Path HWCPATH = Paths.get("src/test/resources/hwc/CPPTests/");

  protected static final Path TARGETPATH = Paths.get("target/generated-test-sources/");

  protected static final Path RTEPATH = Paths.get("src/main/resources/rte/montithings-RTE");

  protected static final Path TESTLIBPATH = Paths.get("src/main/resources/test");

  protected static final Path TESTPATH = Paths.get("src/test/resources/gtests/");

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
    params.setTargetPlatform(TargetPlatform.GENERIC);
    params.setSplittingMode(SplittingMode.OFF);
    params.setHwcTemplatePath(Paths.get(HWCPATH.toString(), packageName));
    params.setHwcPath(HWCPATH.toFile());
    params.setProjectVersion("unspecified");
    params.setMainComponent("hierarchy.Example");
    return params;
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "basicInputOutputTest",
    "additionalPortTest",
    "behaviorTest",
    "classDiagramsTest",
    "siunitsTest",
    "prePostConditionsTest",
    "interfaceComponentsTest",
    "interfaceComponentsBindingTest"
    })
  public void CPPTests(String testName) {
    File models = Paths.get(MODELPATH.toString(), testName).toFile();
    File target = Paths.get(TARGETPATH.toString(), testName, "generated-test-sources/").toFile();
    File hwc = Paths.get(HWCPATH.toString(), testName).toFile();
    File test = Paths.get(TESTPATH.toString(), testName).toFile();

    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    script.generate(models, target, hwc, test, setup(testName));
    MTGenerator mtg = new MTGenerator(target, hwc, setup(testName));
    mtg.generateTestScript(Paths.get(TARGETPATH.toString()).toFile());
  }

}
