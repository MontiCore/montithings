package de.rwth.se.iotlab.config.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.rwth.se.iotlab.config._ast.ASTConfig;
import de.rwth.se.iotlab.config._parser.ConfigParser;
import de.rwth.se.iotlab.facts._ast.ASTDevices;
import de.rwth.se.iotlab.facts._parser.FactsParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class QueryGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Specify a file");
            System.exit(1);
        }
        String file = args[0];

        ConfigParser parser = new ConfigParser();
        Optional<ASTConfig> result = parser.parse(file);

        assert result.isPresent();
        generateQuery(result.get());

    }


    /**
     * Generates a Prolog file containing facts for devices
     * @param devices An AST based on an facts.json file
     */
    public static void generateQuery(ASTConfig devices) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setCommentStart("%");
        setup.setCommentEnd("");

        GeneratorEngine engine = new GeneratorEngine(setup);
        engine.generate("templates/facts.ftl", Paths.get("facts" + ".pl"), devices);

    }

}
