// (c) https://github.com/MontiCore/monticore
package cdlangextension;

import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension._symboltable.*;
import cdlangextension.util.CDLangExtensionError;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeScopesGenitorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides useful methods for handling the CDLangExtension language.
 */
public class CDLangExtensionTool {

  public static final String FILE_ENDING = "cde";

  protected CDLangExtensionCoCoChecker checker;

  protected boolean isSymTabInitialized;

  protected ICD4CodeGlobalScope cdGlobalScope;

  public CDLangExtensionTool() {
    this(CDLangExtensionCoCos.createChecker());
  }

  public CDLangExtensionTool(@NotNull CDLangExtensionCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    CDLangExtensionMill.globalScope().clear();
    CDLangExtensionMill.reset();
    CDLangExtensionMill.init();
    this.checker = checker;
    this.isSymTabInitialized = false;
    ((CDLangExtensionDeSer) CDLangExtensionMill.globalScope().getDeSer())
      .ignoreSymbolKind("de.monticore.cdbasis._symboltable.CDPackageSymbol");
  }

  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelpath is src/main/resources.
   *
   * @param modelPaths paths of all folders containing models
   * @return The initialized symbol table
   */
  public ICDLangExtensionGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    // Load all CD files
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().setModelPath(mp);
    for (File mP : modelPaths) {
      Collection<ICD4CodeArtifactScope> scopes = loadAllCDs(mP.toPath());
      for (ICD4CodeArtifactScope currentScope : scopes) {
        CD4CodeMill.globalScope().addSubScope(currentScope);
      }
    }

    CD4CodeResolver resolver = new CD4CodeResolver(CD4CodeMill.globalScope());

    CDLangExtensionMill.reset();
    CDLangExtensionMill.init();
    ICDLangExtensionGlobalScope cDLangExtensionGlobalScope = CDLangExtensionMill.globalScope();
    cDLangExtensionGlobalScope.setModelPath(mp);
    cDLangExtensionGlobalScope.addAdaptedFieldSymbolResolver(resolver);
    cDLangExtensionGlobalScope.addAdaptedTypeSymbolResolver(resolver);

    // Load add Sym files
    // frontLoadSymFiles(cDLangExtensionGlobalScope, modelPaths);

    isSymTabInitialized = true;
    return cDLangExtensionGlobalScope;
  }

  protected void frontLoadSymFiles(ICDLangExtensionGlobalScope cDLangExtensionGlobalScope,
    File[] modelPaths) {
    for (File mP : modelPaths) {
      Collection<ICDLangExtensionArtifactScope> scopes = loadAll(mP.toPath());
      for (ICDLangExtensionArtifactScope currentScope : scopes) {
        cDLangExtensionGlobalScope.addSubScope(currentScope);
      }
    }
  }

  public ICDLangExtensionArtifactScope load(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(
      FilenameUtils.getExtension(file.getFileName().toString()).endsWith("sym"));
    CDLangExtensionSymbols2Json s2j = new CDLangExtensionSymbols2Json();
    return s2j.load(file.toString());
  }

  public ICDLangExtensionArtifactScope load(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.load(Paths.get(filename));
  }

  public Collection<ICDLangExtensionArtifactScope> loadAll(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith("sym"))
        .map(this::load)
        .collect(Collectors.toSet());
    }
    catch (IOException e) {
      Log.error(
        String.format(CDLangExtensionError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory),
        e);
    }
    return Collections.emptySet();
  }

  public ICD4CodeArtifactScope loadCD(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(
      FilenameUtils.getExtension(file.getFileName().toString()).endsWith("cd"));
    try {
      ASTCDCompilationUnit cdcu = CD4CodeMill.parser().parse(file.toString()).get();
      CD4CodeScopesGenitorDelegator symTab = CD4CodeMill.scopesGenitorDelegator();
      return symTab.createFromAST(cdcu);
    } catch (IOException e) {
      Log.error("Could not load CDE file '" + file + "'");
    }
    return null;
  }

  public ICD4CodeArtifactScope loadCD(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.loadCD(Paths.get(filename));
  }

  public Collection<ICD4CodeArtifactScope> loadAllCDs(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith("cd"))
        .map(this::loadCD)
        .collect(Collectors.toSet());
    }
    catch (IOException e) {
      Log.error(
        String.format(CDLangExtensionError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory), e);
    }
    return Collections.emptySet();
  }

  /**
   * Creates a GlobalScope from a given model path and adds the given AST to it.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public ICDLangExtensionGlobalScope createSymboltable(ASTCDLangExtensionUnit ast,
      File... modelPaths) {

    ICDLangExtensionGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast,globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public ICDLangExtensionGlobalScope createSymboltable(ASTCDLangExtensionUnit ast,
      ICDLangExtensionGlobalScope globalScope) {

    CDLangExtensionScopesGenitorDelegator stc = new CDLangExtensionScopesGenitorDelegator();
    ICDLangExtensionArtifactScope artifactScope = stc.createFromAST(ast);
    globalScope.addSubScope(artifactScope);

    return globalScope;
  }

  public ICD4CodeGlobalScope getCdGlobalScope() {
    return cdGlobalScope;
  }

  /**
   * Setter for the global scope that should be used for resolving non native symbols.
   * @param cdGlobalScope globalScope used for resolving non native symbols
   */
  public void setCdGlobalScope(ICD4CodeGlobalScope cdGlobalScope) {
    this.cdGlobalScope = cdGlobalScope;
  }

  public ASTCDLangExtensionUnit processFile(String file) {
    ASTCDLangExtensionUnit astCDE = null;
    try {
      Path filePath = Paths.get(file);
      astCDE = new CDLangExtensionParser().parseCDLangExtensionUnit(filePath.toFile().getPath()).orElse(null);
    }
    catch (IOException e) {
      Log.error("File '" + file + "' CDE artifact was not found");
    }
    Preconditions.checkArgument(astCDE != null);
    createSymboltable(astCDE, CDLangExtensionMill.globalScope());
    return astCDE;
  }

  public void processFiles(Collection<String> file) {
    file.forEach(this::processFile);
  }
}
