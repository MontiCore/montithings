package montithings.generator.cd2proto;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CorporateTest {
    @Test
    public void run() throws Exception {
        Path outDir = Paths.get("target/out/");
        Path modelPath = Paths.get("src/test/resources/classdiagrams");
        String modelName = "Corporation";
        ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
        generator.parse();
        generator.generate();
    }
}
