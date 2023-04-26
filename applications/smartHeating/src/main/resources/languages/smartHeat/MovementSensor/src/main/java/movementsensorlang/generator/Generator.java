// (c) https://github.com/MontiCore/monticore
package movementsensorlang.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import movementsensorlang._ast.ASTDefs;

import movementsensorlang._parser.MovementSensorLangParser;

import java.io.IOException;
import java.lang.Throwable;
import java.util.ArrayList;
import java.io.File;
import java.util.Optional;
import movementsensorlang._symboltable.MovementSensorLangScopesGenitorDelegator;


public class Generator {
    public static String generate(String json) throws Exception {

        try {
            MovementSensorLangParser parser = new MovementSensorLangParser();
            // This can crash for invalid input, despite try catch. Ist that intentional?
            Optional<ASTDefs> result = parser.parse_String(json);
    
            assert result.isPresent();

            MovementSensorLangScopesGenitorDelegator del = new MovementSensorLangScopesGenitorDelegator();
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
        return engine.generate("smartHeat/MovementSensor/PyGenerator.ftl", ast).toString();

    }

}
