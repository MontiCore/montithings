package montithings.generator.cd2proto;

import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ListWrapperTest {

    final static Path outDir = Paths.get("target/out/ListWrapperTest");

    private static void deleteRecursive(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
                for (Path p : paths) {
                    deleteRecursive(p);
                }
            }
        }
        Files.delete(path);
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        if (Files.exists(outDir)) {
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(outDir)) {
                for (Path p: paths) {
                    deleteRecursive(p);
                }
            }
            try (Stream<Path> paths = Files.list(outDir)) {
                if (paths.findAny().isPresent()) {
                    throw new IllegalStateException("Output directory is not empty");
                }
            }
        } else {
            Files.createDirectory(outDir);
        }
    }

    @Test
    public void run() throws Exception {
        Path modelPath = Paths.get("src/test/resources/classdiagrams");
        String modelName = "ListWrapper";
        ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
        generator.parse();
        generator.generate();

        ProtobufRunner pr = new ProtobufRunner();
        pr.setTargetLang(ProtobufRunner.TargetLang.JAVA);
        pr.setOutDir(outDir);
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(outDir)) {
            for (Path p: paths) {
                if (p.getFileName().toString().endsWith(".proto")) {
                    pr.addInputFiles(p);
                }
            }
        }
        pr.start();
    }
}
