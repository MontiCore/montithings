// (c) https://github.com/MontiCore/monticore
package printerlang.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import printerlang._ast.ASTText;

import printerlang._parser.PrinterLangParser;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Optional;


public class Generator {
    public static String generate(String json) throws Exception {

        try {
            PrinterLangParser parser = new PrinterLangParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTText> result = parser.parse_String(json);
    
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
    public static String generate(ASTText ast) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setTracing(false);
        ArrayList<File> paths = new ArrayList<File>();
        paths.add(new File("templates"));
        setup.setAdditionalTemplatePaths(paths);

        GeneratorEngine engine = new GeneratorEngine(setup);
        return engine.generate("calculationMachine/Printer/PyGenerator.ftl", ast).toString();

    }

}
