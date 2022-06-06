package montithings.generator.cd2proto;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestProtoGenerator {
  @Test
  public void test() throws IOException {
    Path outDir = Paths.get("target/out/");
    Path modelPath = Paths.get("src/test/resources/models");
    String modelName = "domain.Domain";
    ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
    generator.parse();
    generator.generate();
  }
}
