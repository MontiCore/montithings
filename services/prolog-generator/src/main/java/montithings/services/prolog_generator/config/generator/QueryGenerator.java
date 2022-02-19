// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator.config.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import montithings.services.prolog_generator.config._ast.ASTConfig;
import montithings.services.prolog_generator.config._parser.ConfigParser;

import java.io.IOException;
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
        System.out.println(generateQuery(result.get()));

    }

    public static String generateQuery(String json) throws IOException {

        ConfigParser parser = new ConfigParser();
        Optional<ASTConfig> result = parser.parse_String(json);

        assert result.isPresent();
        return generateQuery(result.get());

    }


    /**
     * Generates a Prolog file containing a query based on a config
     * @param config An AST based on an config.json file
     */
    public static String generateQuery(ASTConfig config) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setCommentStart("%");
        setup.setCommentEnd("");

        GeneratorEngine engine = new GeneratorEngine(setup);
        return engine.generate("templates/query.ftl", config).toString();

    }

}
