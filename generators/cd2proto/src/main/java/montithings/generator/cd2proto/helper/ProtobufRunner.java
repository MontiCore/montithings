package montithings.generator.cd2proto.helper;

import de.se_rwth.commons.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ProtobufRunner {

    private TargetLang targetLang;

    public enum TargetLang {
        CPP,
        JAVA,
        PYTHON,
        GO
    }

    private Path outDir;

    private List<Path> inputFiles = new ArrayList<>();

    public ProtobufRunner() {
        Log.info("Checking protoc version", "CD2Proto");
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("protoc", "--version");
        try {
            Process p = pb.start();
            p.waitFor(10, TimeUnit.SECONDS);
            if(p.isAlive()) {
                RuntimeException ex = new IllegalStateException("protoc invocation timed out");
                Log.error("protoc invocation timed out", ex);
                throw ex;
            }
            String version = new BufferedReader(new InputStreamReader(p.getInputStream())).lines().collect(Collectors.joining("\n"));
            Log.info(version, "CD2Proto");
        } catch(IOException ex) {
            ex.printStackTrace();
            Log.error("Failed to start protoc, is the executable in your path?");
            throw new UncheckedIOException(ex);
        }  catch(InterruptedException ignored) {}
    }

    public void setOutDir(Path outDir) {
        this.outDir = outDir;
    }

    public void setTargetLang(TargetLang targetLang) {
        this.targetLang = targetLang;
    }

    public void addInputFiles(Path... inputFile) {
        this.inputFiles.addAll(Arrays.asList(inputFile));
    }

    public void start() {
        if(targetLang == null) {
            throw new IllegalArgumentException("Target language is not set");
        }
        if(outDir == null) {
            throw new IllegalArgumentException("Output directory is not set");
        }
        if(inputFiles.size() == 0) {
            throw new IllegalArgumentException("No input files specified");
        }
        ProcessBuilder pb = new ProcessBuilder();
        String inFiles = inputFiles.stream().map(Path::toString).collect(Collectors.joining(" "));
        pb.command("protoc", "--" + targetLang.name().toLowerCase() + "_out="+outDir.toString(), inFiles);
        Log.info("Invoking protoc: " + String.join(" ", pb.command()), "CD2Proto");
        pb.inheritIO();

        //TODO: Should probably make this timeout configurable
        try {
            Process proc = pb.start();
            if(!proc.waitFor(60, TimeUnit.SECONDS)) {
                throw new TimeoutException("Protobuf timed out");
            }
            int exit = proc.waitFor();
            if(exit != 0) {
                throw new IllegalStateException("protoc terminated abnormally: " + exit);
            }
        } catch(InterruptedException ignored) {
        } catch(IOException | TimeoutException ex) {
            Log.error("Protobuf execution failed: " + ex.getMessage(), ex);
        }
    }
}
