// (c) https://github.com/MontiCore/monticore
package montithings._parser;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static com.google.common.collect.Iterables.transform;

/**
 * Copy-Paste from MontiArcParser that uses MontiThingsParserTOP instead of MontiArcParserTOP
 */
public class MontiThingsParser extends MontiThingsParserTOP {

  /**
   * Parses the file behind the string argument as a MontiArc compilation unit. The string argument
   * must conform to the relative path, consisting a model path, package, and filename. The file
   * content must conform to the concrete syntax of MontiArc, as defined by the MontiArc grammar.
   * The parsed file content is returned as an abstract syntax tree with an {@code
   * ASTMACompilationUnit} as its root wrapped by an {@code Optional}. An empty {@code Optional} is
   * returned if a parse error is raised.
   * <p> An exception of type {@code IOException} is thrown if an I/O exception of some sort
   * occurs. Error {@code COMPONENT_AND_FILE_NAME_DIFFER} is raised if component name and file root
   * differ. Error {@code COMPONENT_AND_FILE_PACKAGE_DIFFER} is raised if component package and file
   * package differ.
   *
   * @param relativeFilePath the {@code String} contained the relative path of the file to be parsed
   * @return an {@code Optional} of an abstract syntax tree representing the parsed file
   * @throws IOException if an I/O exception of some sort occurs
   */
  @Override
  public Optional<ASTMACompilationUnit> parseMACompilationUnit(@NotNull String relativeFilePath)
    throws IOException {
    Preconditions.checkNotNull(relativeFilePath);
    Optional<ASTMACompilationUnit> optAst = super.parseMACompilationUnit(relativeFilePath);
    if (optAst.isPresent()) {
      String fileRoot = Files.getNameWithoutExtension(relativeFilePath);
      String modelName = optAst.get().getComponentType().getName();
      String packageOfFile;
      if (relativeFilePath.lastIndexOf(File.separator) != -1) {
        packageOfFile = Names.getPackageFromPath(Names.getPathFromFilename(relativeFilePath));
      }
      else {
        packageOfFile = Names
          .getPackageFromPath(Names.getPathFromFilename(relativeFilePath, "/"), "/");
      }

      String packageOfModel = constructQualifiedName(optAst.get().isPresentPackage() ?
        optAst.get().getPackage().getPartsList() : new ArrayList<>());
      if (!modelName.equals(fileRoot)) {
        Log.error(String.format(MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER.toString(),
            modelName, fileRoot),
          optAst.get().isPresentPackage() ? optAst.get().getPackage().get_SourcePositionStart()
            : optAst.get().get_SourcePositionStart());
        setError(true);
      }
      if (!Names.getPackageFromPath(packageOfFile).endsWith(packageOfModel)) {
        Log.error(String.format(MontiArcError.COMPONENT_AND_FILE_PACKAGE_DIFFER.toString(),
            packageOfModel, optAst.get().getComponentType().getName(), packageOfFile),
          optAst.get().isPresentPackage() ? optAst.get().getPackage().get_SourcePositionStart()
            : optAst.get().get_SourcePositionStart());
        setError(true);
      }
    }
    if (hasErrors()) {
      return Optional.empty();
    }
    return optAst;
  }


  /**
   * @see MontiArcParser#parseMACompilationUnit(String)
   */
  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull String relativeFilePath) throws IOException {
    Preconditions.checkArgument(relativeFilePath != null);
    return parseMACompilationUnit(relativeFilePath);
  }

  protected String constructQualifiedName(Iterable<String> parts) {
    return Joiners.DOT.join(
      transform(transform(parts,
          StringTransformations.TRIM_WHITESPACE),
        StringTransformations.TRIM_DOT));
  }

}

