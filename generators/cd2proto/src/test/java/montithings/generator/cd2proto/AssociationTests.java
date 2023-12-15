// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2proto;

import de.se_rwth.commons.logging.Log;
import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assume.assumeTrue;

public class AssociationTests {
    private static final Path outDir = Paths.get("target/out/associations/");
    private static final Path modelDir = Paths.get("src/test/resources/classdiagrams");
    private static ProtobufRunner runner;

    //<editor-fold desc="Class setup and utils">
    @BeforeClass
    public static void prepareClass() throws IOException {
        if (Files.exists(outDir) && !Files.isDirectory(outDir)) {
            throw new IOException("Target file exists but is not a directory: " + outDir);
        } else if (!Files.exists(outDir)) {
            Files.createDirectories(outDir);
        }
        runner = new ProtobufRunner();
        runner.setTargetLang(ProtobufRunner.TargetLang.JAVA);
        runner.setOutDir(outDir);
        Log.enableFailQuick(false);
    }

    @BeforeClass
    public static void cleanOutDir() throws Exception {
        if (Files.exists(outDir)) {
            Files.walkFileTree(outDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    @Before
    public void resetErrorCount() {
        Log.getFindings().clear();
    }

    @Before
    public void clearInputFiles() {
        runner.clearInputFiles();
    }

    private void runProtobuf() throws IOException {
        Files.list(outDir).forEach(f -> {
            if (f.getFileName().toString().endsWith(".proto")) {
                runner.addInputFiles(f);
            }
        });
        runner.start();
    }

    private void generateProto(String name) throws Exception {
        ProtoGenerator generator = new ProtoGenerator(outDir, modelDir, name);
        generator.generate();
    }
    //</editor-fold>


    @Test
    public void WHEN_NestedListPresent_THEN_WrapperIsGenerated() throws Exception {
        generateProto("Associations");
        assumeTrue(ProtobufRunner.isProtocInPATH());
        runProtobuf();
    }
}
