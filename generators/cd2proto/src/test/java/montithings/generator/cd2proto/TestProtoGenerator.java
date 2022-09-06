package montithings.generator.cd2proto;

import de.se_rwth.commons.logging.Log;
import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assume.assumeTrue;

public class TestProtoGenerator {

  @BeforeClass
  public static void prepareClass() {
    Log.enableFailQuick(false);
  }

  @Test
  public void test() throws IOException, ParseException {
    Path outDir = Paths.get("target/out/Domain");
    Path modelPath = Paths.get("src/test/resources/models");
    String modelName = "domain.Domain";
    ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
    Set<Path> protoFiles = generator.generate();

    assumeTrue(ProtobufRunner.isProtocInPATH());
    ProtobufRunner pr = new ProtobufRunner();
    pr.setTargetLang(ProtobufRunner.TargetLang.CPP);
    pr.setOutDir(outDir);
    protoFiles.forEach(pr::addInputFiles);
    pr.start();
  }

  @Test
  public void GIVEN_a_false_model_WHEN_generating_THEN_generator_throws_exception() {
    Path outDir = Paths.get("target/out/Domain");
    Path modelPath = Paths.get("src/test/resources/models");
    ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, "nomodel");
    Assert.assertThrows(NoSuchFileException.class, generator::generate);

    generator = new ProtoGenerator(outDir, modelPath, "domain.BrokenModel");
    Assert.assertThrows(ParseException.class, generator::generate);
  }
}
