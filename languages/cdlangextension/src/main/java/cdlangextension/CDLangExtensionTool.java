// (c) https://github.com/MontiCore/monticore
package cdlangextension;

import cd4montithings.CD4MontiThingsMill;
import cd4montithings._symboltable.CD4MontiThingsScopesGenitorDelegator;
import cd4montithings._symboltable.ICD4MontiThingsArtifactScope;
import cd4montithings._symboltable.ICD4MontiThingsGlobalScope;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension._symboltable.*;
import cdlangextension.util.CDLangExtensionError;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
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

  protected ICD4MontiThingsGlobalScope cdGlobalScope;

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
    CD4MontiThingsMill.globalScope().clear();
    CD4MontiThingsMill.reset();
    CD4MontiThingsMill.init();
    CD4MontiThingsMill.globalScope().setModelPath(mp);
    for (File mP : modelPaths) {
      Collection<ICD4MontiThingsArtifactScope> scopes = loadAllCDs(mP.toPath());
      for (ICD4MontiThingsArtifactScope currentScope : scopes) {
        CD4MontiThingsMill.globalScope().addSubScope(currentScope);
      }
    }

    //TODO: introduce CD4MontiThingsResolver?
    CD4CodeResolver resolver = new CD4CodeResolver(CD4MontiThingsMill.globalScope());

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
        String.format(CDLangExtensionError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()),
        e);
    }
    return Collections.emptySet();
  }

  public ICD4MontiThingsArtifactScope loadCD(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(
      FilenameUtils.getExtension(file.getFileName().toString()).endsWith("cd"));
    try {
      ASTCDCompilationUnit cdcu = CD4MontiThingsMill.parser().parse(file.toString()).get();
      CD4MontiThingsScopesGenitorDelegator symTab = CD4MontiThingsMill.scopesGenitorDelegator();
      return symTab.createFromAST(cdcu);
    } catch (IOException e) {
      Log.error("Could not load CDE file '" + file + "'");
    }
    return null;
  }

  public ICD4MontiThingsArtifactScope loadCD(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.loadCD(Paths.get(filename));
  }

  public Collection<ICD4MontiThingsArtifactScope> loadAllCDs(@NotNull Path directory) {
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
        String.format(CDLangExtensionError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()),
        e);
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

  public ICD4MontiThingsGlobalScope getCdGlobalScope() {
    return cdGlobalScope;
  }

  /**
   * Setter for the global scope that should be used for resolving non native symbols.
   * @param cdGlobalScope globalScope used for resolving non native symbols
   */
  public void setCdGlobalScope(ICD4MontiThingsGlobalScope cdGlobalScope) {
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
