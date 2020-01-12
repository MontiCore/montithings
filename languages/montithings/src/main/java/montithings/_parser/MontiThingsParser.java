// (c) https://github.com/MontiCore/monticore
package montithings._parser;

import com.google.common.io.Files;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTCompilationUnit;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author (last commit) Joshua Fürste
 */
public class MontiThingsParser extends MontiThingsParserTOP {

  /**
   * Besides parsing, this also checks that the filename equals the model name and the package
   * declaration equals the suffix of the package name of the model.
   */
  @SuppressWarnings("DuplicatedCode")
  @Override
  public Optional<ASTMTCompilationUnit> parseMTCompilationUnit(String filename)
      throws IOException {
    Optional<ASTMTCompilationUnit> ast = super.parseMTCompilationUnit(filename);
    if (ast.isPresent()) {
      String simpleFileName = Files.getNameWithoutExtension(filename);
      String modelName = ast.get().getComponent().getName();
      String filePathString = Paths.get(filename).toString();
      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(filePathString));
      String packageDeclaration = Names.getQualifiedName(ast.get().getPackageList());
      if (!modelName.equals(simpleFileName)) {
        Log.error("0xMA256 The name of the component " + modelName
            + " is not identical to the name of the file in file '" + filename
            + "' (without its file extension).");
      }
      if (!packageName.endsWith(packageDeclaration)) {
        Log.error("0xMA257 The package declaration " + packageDeclaration
            + " of the component must not differ from the "
            + "package of the component file in file '" + filename + "'.");
      }

    }
    return ast;
  }

}

