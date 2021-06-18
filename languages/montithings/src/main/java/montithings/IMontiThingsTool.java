// (c) https://github.com/MontiCore/monticore
package montithings;

import montiarc._ast.ASTMACompilationUnit;
import montithings._symboltable.IMontiThingsArtifactScope;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.IMontiThingsScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface IMontiThingsTool {
  /**
   * Parses the provided file as a MontiThings model. Returns the compilation unit of that model if the file is parsed
   * successfully.
   *
   * @param file the file to parse as MontiThings model
   * @return the compilation unit of the file if parsed successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of MontiThings model files.
   */
  Optional<ASTMACompilationUnit> parse(@NotNull Path file);

  /**
   * Loads the provided file as a serialized MontiThings model. Returns the artifact scope of that model if the file is
   * deserialized successfully.
   *
   * @param file the file to load as a serialized MontiThings model
   * @return the artifact scope of the file if deserialized successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of serialized model files.
   */
  IMontiThingsArtifactScope load(@NotNull Path file);

  /**
   * Parses the file to the provided file name as a MontiThings model. Returns the compilation unit of that model if the
   * file is parsed successfully.
   *
   * @param fileName the name of the file to parse as MontiThings model
   * @return the compilation of the file if parsed successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of MontiThings model files.
   */
  Optional<ASTMACompilationUnit> parse(@NotNull String fileName);

  /**
   * Loads the file to the provided file name as a serialized MontiThings model. Returns the artifact scope of that model
   * if the file is deserialized successfully.
   *
   * @param fileName the name of the file to load as serialized MontiThings model
   * @return the artifact scope of the file if deserialized successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of serialized model files.
   */
  IMontiThingsArtifactScope load(@NotNull String fileName);

  /**
   * Parses all MontiThings model files in the provided directory and its subdirectories as MontiThings models. Returns a
   * collection of compilation units of the successfully parsed models.
   *
   * @param directory the directory containing the files to parse
   * @return a possibly empty collection of compilation units of the successfully parsed MontiThings model files
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<ASTMACompilationUnit> parseAll(@NotNull Path directory);

  /**
   * Loads all serialized model files in the provided directory and its subdirectories as serialized MontiThings models.
   * Returns a collection of artifact scopes of the successfully deserialized models.
   *
   * @param directory the directory containing the files to load
   * @return a possibly empty collection of artifact scopes of the successfully deserialized model files
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<IMontiThingsArtifactScope> loadAll(@NotNull Path directory);

  /**
   * Parses all models in the provided scope as MontiThings models. Returns a collection of compilation units of the
   * successfully parsed models.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of compilation units of the successfully parsed models in the provided scope
   */
  Collection<ASTMACompilationUnit> parseAll(@NotNull IMontiThingsGlobalScope scope);

  /**
   * Loads all models in the provided scope as serialized MontiThings models. Returns a collection of artifact scopes of
   * the successfully deserialized models.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of artifact scopes the successfully deserialized models in the provided scope
   */
  Collection<IMontiThingsArtifactScope> loadAll(@NotNull IMontiThingsGlobalScope scope);

  /**
   * Creates the symbol table to the provided compilation unit and returns its artifact scope.
   *
   * @param ast the compilation unit whose symbol table is to be created
   * @return the created artifact scope to the provided compilation unit
   */
  IMontiThingsScope createSymbolTable(@NotNull ASTMACompilationUnit ast);

  /**
   * Loads all models in the provided scope, creates their symbol table, and returns a collection of their artifact
   * scopes.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of artifact scopes of models in the provided global scope
   */
  Collection<IMontiThingsScope> createSymbolTable(@NotNull IMontiThingsGlobalScope scope);

  /**
   * Loads all models in the provided scope, creates their symbol table, and returns a collection of their artifact
   * scopes.
   *
   * @param directory the directory containing the files to load
   * @return a possibly empty collection of artifact scopes of models in the provided directory
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<IMontiThingsScope> createSymbolTable(@NotNull Path directory);

  /**
   * Creates a MontiThings global scope with the provided directories as model path and default file ending ('arc').
   *
   * @param directories the directories that compose the model path
   * @return the constructed global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiThingsGlobalScope createMTGlobalScope(@NotNull Path... directories);

  /**
   * Adds primitive types to the global scope.
   */
  void addBasicTypes();

  /**
   * Checks default context conditions for the provided MontiThings compilation unit and logs findings.
   *
   * @param ast the compilation unit whose context conditions are to be checked
   */
  void checkCoCos(@NotNull ASTMACompilationUnit ast);

  /**
   * Loads all model files in the provided scope, creates their symbol table, checks default context conditions for
   * these and logs findings.
   *
   * @param scope the scope under consideration
   */
  void processModels(@NotNull IMontiThingsGlobalScope scope);

  /**
   * Creates a MontiThings global scope with the provided directories as model path and default file ending ('arc), loads
   * all model files in that scope, creates their symbol table, checks default context conditions for these and logs
   * findings. Also loads serialized MontiThings model files in the same model path.
   *
   * @param directories the directories that compose the model path
   * @return the created global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiThingsGlobalScope processModels(@NotNull Path... directories);
}
