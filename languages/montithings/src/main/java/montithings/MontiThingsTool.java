/* (c) https://github.com/MontiCore/monticore */
package montithings;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.*;
import montiarc._symboltable.adapters.Field2CDFieldResolvingDelegate;
import montiarc._symboltable.adapters.Type2CDTypeResolvingDelegate;
import montiarc.util.Modelfinder;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._parser.MontiThingsParser;
import montithings._symboltable.*;
import montithings.cocos.MontiThingsCoCos;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class MontiThingsTool extends MontiArcTool {

  protected MontiThingsLanguage language;

  protected MontiThingsCoCoChecker checker;

  protected boolean isSymTabInitialized;

  private CD4AnalysisGlobalScope cdGlobalScope;

  public MontiThingsTool() {
    this(MontiThingsCoCos.createChecker(), new MontiThingsLanguage());
  }

  public MontiThingsTool(@NotNull MontiThingsCoCoChecker checker, @NotNull MontiThingsLanguage language) {
    Preconditions.checkArgument(checker != null);
    Preconditions.checkArgument(language != null);
    this.checker = checker;
    this.language = language;
    this.isSymTabInitialized = false;
  }

  public Optional<ASTMACompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    MontiThingsParser p = new MontiThingsParser();
    Optional<ASTMACompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();

  }

  public boolean checkCoCos(@NotNull ASTArcBasisNode node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.isSymTabInitialized, "Please initialize symbol-table before "
        + "checking portextensions.cocos.");
    this.checker.checkAll(node);
    if (Log.getErrorCount() != 0) {
      Log.debug("Found " + Log.getErrorCount() + " errors in node " + node + ".", "XX");
      return false;
    }
    return true;
  }

  /**
   * Loads a ComponentSymbol with the passed componentName. The componentName is the full qualified
   * name of the component model. Modelpaths are folders relative to the project path and containing
   * the packages the models are located in. When the ComponentSymbol is resolvable it is returned.
   * Otherwise the optional is empty.
   *
   * @param componentName Name of the component
   * @param modelPaths    Folders containing the packages with models
   * @return an {@code Optional} of the loaded component type
   */
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithoutCocos(String componentName,
      File... modelPaths) {
    IMontiThingsScope s = initSymbolTable(modelPaths);
    return s.resolveComponentType(componentName);
  }

  public Optional<ComponentTypeSymbol> loadComponentSymbolWithCocos(String componentName,
      File... modelPaths) {
    Optional<ComponentTypeSymbol> compSym = loadComponentSymbolWithoutCocos(componentName,
        modelPaths);
    compSym.ifPresent(componentTypeSymbol -> this.checkCoCos(componentTypeSymbol.getAstNode()));
    return compSym;
  }

  /**
   * Loads the AST of the passed model with name componentName. The componentName is the fully
   * qualified. Modelpaths are folders relative to the project path and containing the packages the
   * models are located in. When the ComponentSymbol is resolvable it is returned. Otherwise the
   * optional is empty.
   *
   * @param modelPath The model path containing the package with the model
   * @param model     the fully qualified model name
   * @return the AST node of the model
   */
  public Optional<ASTComponentType> getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    Optional<ComponentTypeSymbol> comp = loadComponentSymbolWithoutCocos(model,
        Paths.get(modelPath).toFile());

    if (!comp.isPresent()) {
      Log.error("Model could not be resolved!");
      return Optional.empty();
    }

    if (!comp.get().isPresentAstNode()) {
      Log.debug("Symbol not linked with node.", "XX");
      return Optional.empty();
    }
    return Optional.of(comp.get().getAstNode());
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
  public IMontiThingsScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    Field2CDFieldResolvingDelegate fieldDelegate;
    Type2CDTypeResolvingDelegate typeDelegate;
    if(this.cdGlobalScope ==null) {
      CD4AnalysisLanguage cd4ALanguage = CD4AnalysisMill.cD4AnalysisLanguageBuilder().build();
      CD4AnalysisGlobalScope cd4AGlobalScope = CD4AnalysisMill.cD4AnalysisGlobalScopeBuilder()
          .setModelPath(mp)
          .setCD4AnalysisLanguage(cd4ALanguage)
          .build();
      fieldDelegate = new Field2CDFieldResolvingDelegate(cd4AGlobalScope);
      typeDelegate =  new Type2CDTypeResolvingDelegate(cd4AGlobalScope);
    }
    else{
      fieldDelegate = new Field2CDFieldResolvingDelegate(this.cdGlobalScope);
      typeDelegate =  new Type2CDTypeResolvingDelegate(this.cdGlobalScope);
    }

    MontiThingsGlobalScope gs = new MontiThingsGlobalScope(mp, language);
    gs.addAdaptedFieldSymbolResolvingDelegate(fieldDelegate);
    gs.addAdaptedTypeSymbolResolvingDelegate(typeDelegate);
    addBasicTypes(gs);

    isSymTabInitialized = true;
    return gs;
  }

  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelPath is src/main/resources.
   *
   * @param modelPath The model path for the symbol table
   * @return the initialized symbol table
   */
  public IMontiThingsScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }

  public IMontiThingsScope addBasicTypes(@NotNull IMontiThingsScope scope) {
    scope.add(new TypeSymbol("String"));
    scope.add(new TypeSymbol("Integer"));
    scope.add(new TypeSymbol("Map"));
    scope.add(new TypeSymbol("Set"));
    scope.add(new TypeSymbol("List"));
    scope.add(new TypeSymbol("Boolean"));
    scope.add(new TypeSymbol("Character"));
    scope.add(new TypeSymbol("Double"));
    scope.add(new TypeSymbol("Float"));

    //primitives
    scope.add(new TypeSymbol("int"));
    scope.add(new TypeSymbol("boolean"));
    scope.add(new TypeSymbol("float"));
    scope.add(new TypeSymbol("double"));
    scope.add(new TypeSymbol("char"));
    scope.add(new TypeSymbol("long"));
    scope.add(new TypeSymbol("short"));
    scope.add(new TypeSymbol("byte"));

    return scope;
  }

  public IMontiArcScope buildSymbolTable(File... modelPaths) {
    MontiThingsGlobalScope gs = (MontiThingsGlobalScope) initSymbolTable(modelPaths);

    final MontiThingsSymbolTableCreatorDelegator symbolTableCreator = MontiThingsMill
        .montiThingsSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(gs)
        .build();

    Set<File> modelFiles = Modelfinder.getModelFiles(MontiThingsLanguage.FILE_ENDING, modelPaths);

    // Build artifacts scopes for all models (automatically added to GlobalScope gs)
    for (File model : modelFiles) {
      ASTMACompilationUnit ast = parse(model.getPath()).get();
      final MontiThingsArtifactScope scope = symbolTableCreator.createFromAST(ast);
    }

    return gs;
  }

  public CD4AnalysisGlobalScope getCdGlobalScope() {
    return cdGlobalScope;
  }

  public void setCdGlobalScope(CD4AnalysisGlobalScope cdGlobalScope) {
    this.cdGlobalScope = cdGlobalScope;
  }
}
