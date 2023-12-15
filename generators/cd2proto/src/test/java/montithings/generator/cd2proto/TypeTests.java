// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2proto;

import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.se_rwth.commons.logging.Log;
import montithings.generator.cd2proto.helper.ProtobufRunner;
import org.junit.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assume.assumeTrue;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;



public class TypeTests {
    private static ProtobufRunner runner;
    private static final Path outDir = Paths.get("target/out/typetests/");
    private static final Path modelDir = Paths.get("src/test/resources/classdiagrams");

    private static final Class<UnsupportedOperationException> UNSUPPORTED_OPERATION_EXCEPTION_CLASS
      = UnsupportedOperationException.class;
    private static final Class<MontiCoreFreeMarkerException> MONTICORE_FREE_MARKER_EXCEPTION_CLASS
      = MontiCoreFreeMarkerException.class;

    //<editor-fold desc="Class setup and utils">
    @BeforeClass
    public static void prepareClass() throws IOException {
        if(Files.exists(outDir) && !Files.isDirectory(outDir)) {
            throw new IOException("Target file exists but is not a directory: " + outDir);
        } else if(!Files.exists(outDir)) {
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
            if(f.getFileName().toString().endsWith(".proto")) {
                runner.addInputFiles(f);
            }
        });
        runner.start();
    }

    private void generateProto(String name) throws IOException, ParseException {
        ProtoGenerator generator = new ProtoGenerator(outDir, modelDir, name);
        generator.generate();
    }
    //</editor-fold>



    @Test
    public void WHEN_NestedListPresent_THEN_WrapperIsGenerated() throws IOException, ParseException {
        generateProto("ListWrapper");
        assumeTrue(ProtobufRunner.isProtocInPATH());
        runProtobuf();
    }

    @Test
    public void WHEN_MapPresent_THEN_MapIsTranslated() throws IOException, ParseException {
        generateProto("Map");
        assumeTrue(ProtobufRunner.isProtocInPATH());
        runProtobuf();
    }

    @Test
    public void WHEN_MapIsRepeated_THEN_ExceptionIsThrown() throws IOException, ParseException {
        assertThatExceptionOfType(MONTICORE_FREE_MARKER_EXCEPTION_CLASS)
          .isThrownBy(() -> generateProto("IllegalMap"))
          .withMessageContaining("Protobuf does not support repeating the map type and no wrapper is currently implemented")
        ;
    }

    @Test
    public void WHEN_PrimitivePresent_THEN_PrimitiveIsTranslated() throws IOException, ParseException {
        generateProto("PrimitiveTypes");
        assumeTrue(ProtobufRunner.isProtocInPATH());
        runProtobuf();
    }

    @Test
    public void WHEN_EnumPresent_THEN_EnumIsTranslated() throws IOException, ParseException {
        generateProto("Enums");
        assumeTrue(ProtobufRunner.isProtocInPATH());
        runProtobuf();
    }

    @Test
    public void WHEN_SimpleGenericPresent_THEN_SimpleGenericIsTranslated() throws IOException, ParseException {
        generateProto("ArraysAndCollections");
        assumeTrue(ProtobufRunner.isProtocInPATH());
        runProtobuf();
    }
}
