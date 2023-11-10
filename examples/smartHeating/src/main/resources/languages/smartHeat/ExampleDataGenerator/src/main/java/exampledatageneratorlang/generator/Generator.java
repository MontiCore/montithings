// (c) https://github.com/MontiCore/monticore
package exampledatageneratorlang.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import exampledatageneratorlang._ast.ASTDefs;

import exampledatageneratorlang._parser.ExampleDataGeneratorLangParser;

import java.io.IOException;
import java.lang.Throwable;
import java.util.ArrayList;
import java.io.File;
import java.util.Optional;
import exampledatageneratorlang._symboltable.ExampleDataGeneratorLangScopesGenitorDelegator;


public class Generator {
    public static String generate(String json) throws Exception {

        try {
            ExampleDataGeneratorLangParser parser = new ExampleDataGeneratorLangParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTDefs> result = parser.parse_String(json);
    
            assert result.isPresent();

            ExampleDataGeneratorLangScopesGenitorDelegator del = new ExampleDataGeneratorLangScopesGenitorDelegator();
            del.createFromAST(result.get());

            return generate(result.get());
    
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        } catch (Throwable e) {
            System.out.println(e);
            throw e;
        }

    }

    /**
     * Generates a Prolog file containing facts for devices
     * @param devices An AST based on an facts.json file
     */
    public static String generate(ASTDefs ast) {
        GeneratorSetup setup = new GeneratorSetup();
        // Prolog Comment
        setup.setTracing(false);
        ArrayList<File> paths = new ArrayList<File>();
        paths.add(new File("templates"));
        setup.setAdditionalTemplatePaths(paths);

        GeneratorEngine engine = new GeneratorEngine(setup);
        return engine.generate("smartHeat/ExampleDataGenerator/PyGenerator.ftl", ast).toString();

    }

}
