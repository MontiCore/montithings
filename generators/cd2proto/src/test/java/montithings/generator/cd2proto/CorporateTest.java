package montithings.generator.cd2proto;

import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class CorporateTest {
    @Test
    public void run() throws Exception {
        Path outDir = Paths.get("target/out/Corporate");
        Path modelPath = Paths.get("src/test/resources/classdiagrams");
        String modelName = "Corporation";
        ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
        generator.parse();
        Set<Path> protoFiles = generator.generate();

        ProtobufRunner pr = new ProtobufRunner();
        pr.setTargetLang(ProtobufRunner.TargetLang.CPP);
        pr.setOutDir(outDir);
        protoFiles.forEach(pr::addInputFiles);
        pr.start();
    }
}
