package montithings.generator.cd2proto;

import de.se_rwth.commons.logging.Log;
import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ListWrapperTest {

    @Test
    public void run() throws Exception {
        Path outDir = Paths.get("target/out/");
        Files.list(outDir).forEach(f -> {
            try {
                Files.delete(f);
            } catch(IOException ex) {
                Log.error("Failed to clean up output directory: " + ex.getMessage(), ex);
            }
        });
        if(Files.list(outDir).findAny().isPresent()) {
            throw new IllegalStateException("Output directory is not empty");
        }

        Path modelPath = Paths.get("src/test/resources/classdiagrams");
        String modelName = "ListWrapper";
        ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
        generator.parse();
        generator.generate();

        ProtobufRunner pr = new ProtobufRunner();
        pr.setTargetLang(ProtobufRunner.TargetLang.JAVA);
        pr.setOutDir(outDir);
        for(File f : outDir.toFile().listFiles()) {
            if(!f.getName().endsWith(".proto")) continue;
            pr.addInputFiles(f.toPath());
        }
        pr.start();
    }
}
