// (c) https://github.com/MontiCore/monticore
package calculatorlang.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import calculatorlang._ast.ASTCalculation;

import calculatorlang._parser.CalculatorLangParser;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Optional;


public class Generator {
    public static String generate(String json) throws Exception {

        try {
            CalculatorLangParser parser = new CalculatorLangParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTCalculation> result = parser.parse_String(json);
    
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
    public static String generate(ASTCalculation ast) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setTracing(false);
        ArrayList<File> paths = new ArrayList<File>();
        paths.add(new File("templates"));
        setup.setAdditionalTemplatePaths(paths);

        GeneratorEngine engine = new GeneratorEngine(setup);
        return engine.generate("calculationMachine/Calculator/PyGenerator.ftl", ast).toString();

    }

}
