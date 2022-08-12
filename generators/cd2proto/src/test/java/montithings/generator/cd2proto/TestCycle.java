package montithings.generator.cd2proto;

import de.se_rwth.commons.logging.Log;
import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class TestCycle {
  @Test
  public void test() throws IOException {
    Log.init();
    Log.enableFailQuick(false);
    Log.getFindings().clear();

    Path outDir = Paths.get("target/out/Domain");
    Path modelPath = Paths.get("src/test/resources/classdiagrams");
    String modelName = "CycleTest";
    ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
    Set<Path> protoFiles = generator.generate();

    assertEquals(1, Log.getErrorCount());
  }
}
