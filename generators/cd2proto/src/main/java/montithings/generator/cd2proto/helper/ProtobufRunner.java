package montithings.generator.cd2proto.helper;

import de.se_rwth.commons.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ProtobufRunner {

    public enum TargetLang {
        CPP,
        JAVA,
        PYTHON,
        //GO, TODO: make this available. proto-gen-go provides --go_out
        PYI,
        CSHARP,
        KOTLIN,
        OBJC,
        PHP,
        RUBY
    }

    private Set<TargetLang> targetLangSet = new HashSet<TargetLang>();
    private Path outDir;
    private List<Path> inputFiles = new ArrayList<>();

    public ProtobufRunner() {

    }

    public void setOutDir(Path outDir) {
        this.outDir = outDir;
    }

    public ProtobufRunner setTargetLang(TargetLang targetLang) {
        this.targetLangSet.clear();
        this.addTargetLang(targetLang);
        return this;
    }

    public ProtobufRunner addTargetLang(TargetLang targetLang) {
        this.targetLangSet.add(targetLang);
        return this;
    }

    public void addInputFiles(Path... inputFile) {
        this.inputFiles.addAll(Arrays.asList(inputFile));
    }

    public void clearInputFiles() {
        this.inputFiles.clear();
    }

    public void start() {
        {
            Log.info("Checking protoc version", "CD2Proto");
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("protoc", "--version");
            try {
                Process p = pb.start();
                p.waitFor(10, TimeUnit.SECONDS);
                if (p.isAlive()) {
                    RuntimeException ex = new IllegalStateException("protoc invocation timed out");
                    Log.error("protoc invocation timed out", ex);
                    throw ex;
                }
                String version = new BufferedReader(new InputStreamReader(p.getInputStream())).lines().collect(Collectors.joining("\n"));
                Log.info(version + " (please use v21.0 or newer)", "CD2Proto");
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.error("Failed to start protoc, is the executable on your PATH?");
                throw new UncheckedIOException(ex);
            } catch (InterruptedException ignored) {
            }
        }

        if(targetLangSet.size() == 0) {
            throw new IllegalArgumentException("Target language is not set");
        }
        if(outDir == null) {
            throw new IllegalArgumentException("Output directory is not set");
        }
        if(inputFiles.size() == 0) {
            throw new IllegalArgumentException("No input files specified");
        }

        List<String> args = new ArrayList<>();
        args.add("protoc");
        for (TargetLang lang : targetLangSet) {
            args.add(String.format("--%s_out=%s", lang.name().toLowerCase(), outDir));
        }

        /* Adding the parent directories of the protobuf files is necessary to
        ensure that the protocol buffer files are relocatable. Otherwise the
        cpp source files will try to include the corresponding headers with a
        relative path starting at the _current_ working directory, e.g.
        `#include "target/out/Domain.pb.h"` instead of the desired
        `#include "Domain.pb.h"`. */
        args.addAll(inputFiles.stream()
            .map(p -> "--proto_path=" + p.getParent().toString())
            .collect(Collectors.toList()));
        args.addAll(inputFiles.stream()
            .map(Path::toString)
            .collect(Collectors.toList()));

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(args);
        Log.info("Invoking protoc: " + String.join(" ", pb.command()), "CD2Proto");
        pb.inheritIO();

        try {
            Process proc = pb.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if(proc.isAlive()) {
                    proc.destroy();
                }
            }));
            int exit = proc.waitFor();
            if(exit != 0) {
                throw new IllegalStateException("protoc terminated abnormally: " + exit);
            }
        } catch(InterruptedException ignored) {
        } catch(IOException | TimeoutException ex) {
            Log.error("Protobuf execution failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * @return true iff the "protoc" executable is in PATH
     */
    public static boolean isProtocInPATH() {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("protoc", "--version");
        try {
            Process p = pb.start();
            return p.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
