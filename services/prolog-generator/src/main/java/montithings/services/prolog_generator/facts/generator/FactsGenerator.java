// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator.facts.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import montithings.services.prolog_generator.facts._ast.ASTDevices;
import montithings.services.prolog_generator.facts._parser.FactsParser;

import java.io.IOException;
import java.util.Optional;

public class FactsGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Specify a file");
            System.exit(1);
        }
        String file = args[0];

        FactsParser parser = new FactsParser();
        Optional<ASTDevices> result = parser.parse(file);

        assert result.isPresent();
        System.out.println(generateFacts(result.get()));

    }

    public static String generateFacts(String json) throws Exception {

        try {
            FactsParser parser = new FactsParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTDevices> result = parser.parse_String(json);
    
            assert result.isPresent();
            return generateFacts(result.get());
    
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * Generates a Prolog file containing facts for devices
     * @param devices An AST based on an facts.json file
     */
    public static String generateFacts(ASTDevices devices) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setCommentStart("%");
        setup.setCommentEnd("");

        GeneratorEngine engine = new GeneratorEngine(setup);
        return engine.generate("templates/facts.ftl", devices).toString();

    }

}
