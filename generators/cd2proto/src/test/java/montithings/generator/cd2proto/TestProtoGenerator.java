package montithings.generator.cd2proto;

import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class TestProtoGenerator {
  @Test
  public void test() throws IOException {
    Path outDir = Paths.get("target/out/Domain");
    Path modelPath = Paths.get("src/test/resources/models");
    String modelName = "domain.Domain";
    ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
    Set<Path> protoFiles = generator.generate();

    ProtobufRunner pr = new ProtobufRunner();
    pr.setTargetLang(ProtobufRunner.TargetLang.CPP);
    pr.setOutDir(outDir);
    protoFiles.forEach(pr::addInputFiles);
    pr.start();
  }
}
