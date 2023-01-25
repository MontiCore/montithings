// (c) https://github.com/MontiCore/monticore
package network.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import network._ast.ASTNet;

import network._parser.NetworkParser;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Optional;


public class Generator {
    public static String generate(String json) throws Exception {

        try {
            NetworkParser parser = new NetworkParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTNet> result = parser.parse_String(json);
    
            assert result.isPresent();
            return generate(result.get());
    
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Generates a Prolog file containing facts for devices
     * @param devices An AST based on an facts.json file
     */
    public static String generate(ASTNet ast) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setTracing(false);
        ArrayList<File> paths = new ArrayList<File>();
        paths.add(new File("templates/hierarchy/Sink"));
        setup.setAdditionalTemplatePaths(paths);

        GeneratorEngine engine = new GeneratorEngine(setup);
        return engine.generate("PyGenerator.ftl", ast).toString();

    }

}
