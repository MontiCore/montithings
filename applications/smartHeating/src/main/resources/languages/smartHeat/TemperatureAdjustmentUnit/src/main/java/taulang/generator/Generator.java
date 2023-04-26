// (c) https://github.com/MontiCore/monticore
package taulang.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import taulang._ast.ASTDefs;

import taulang._parser.TAULangParser;

import java.io.IOException;
import java.lang.Throwable;
import java.util.ArrayList;
import java.io.File;
import java.util.Optional;
import taulang.TAULangMill;
import taulang._symboltable.TAULangScopesGenitorDelegator;


public class Generator {
    public static String generate(String json) throws Exception {

        try {
            TAULangParser parser = new TAULangParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTDefs> result = parser.parse_String(json);
    
            assert result.isPresent();

            TAULangScopesGenitorDelegator del = new TAULangScopesGenitorDelegator();
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
        return engine.generate("smartHeat/TemperatureAdjustmentUnit/PyGenerator.ftl", ast).toString();

    }

}
