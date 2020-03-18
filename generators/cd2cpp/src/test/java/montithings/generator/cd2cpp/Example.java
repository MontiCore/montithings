/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.cd2cpp;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class Example {
  @Test
  public void test() {
    Path outDir = Paths.get("target/out/");
    Path modelPath = Paths.get("src/test/resources/models");
    String modelName = "domain.Domain";
    new CppGenerator(outDir, modelPath, modelName).generate();
    String targetPackage = "some.custom._package";
    new CppGenerator(outDir, modelPath, modelName, targetPackage).generate();
  }
}
