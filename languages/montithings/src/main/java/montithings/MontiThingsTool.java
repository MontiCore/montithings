/* (c) https://github.com/MontiCore/monticore */
package montithings;

import com.google.common.base.Preconditions;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.DefsTypeBasic;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._parser.MontiThingsParser;
import montithings._symboltable.IMontiThingsArtifactScope;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.IMontiThingsScope;
import montithings._symboltable.MontiThingsSymbolTableCreatorDelegator;
import montithings.cocos.MontiThingsCoCos;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.ParserUtil;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import static montithings.util.SymbolUtil.addParam;
import static montithings.util.SymbolUtil.createFunction;


public class MontiThingsTool {

  protected MontiThingsCoCoChecker mtChecker;

  protected CD4CodeCoCoChecker cdChecker;

  protected boolean isSymTabInitialized;

  public static final String MT_FILE_EXTENSION = "mt";

  public static final String CD_FILE_EXTENSION = "cd";

  protected static final String TOOL_NAME = "MontiThingsTool";

  private List<MontiThingsTrafo> trafos;

  public MontiThingsTool() {
    this(MontiThingsCoCos.createChecker(), new CD4CodeCoCos().createNewChecker());
  }

  public MontiThingsTool(@NotNull MontiThingsCoCoChecker mtChecker,
    @NotNull CD4CodeCoCoChecker cdChecker) {
    Preconditions.checkArgument(mtChecker != null);
    Preconditions.checkArgument(cdChecker != null);
    this.mtChecker = mtChecker;
    this.cdChecker = cdChecker;
    this.isSymTabInitialized = false;

        this.trafos = new ArrayList<>();
    }

    public List<MontiThingsTrafo> getTrafos() {
        return trafos;
    }

    public void setTrafos(List<MontiThingsTrafo> trafos) {
        this.trafos = trafos;
    }

    public void addTrafo(MontiThingsTrafo trafo) {
        this.trafos.add(trafo);
    }

  protected MontiThingsCoCoChecker getMTChecker() {
    return this.mtChecker;
  }

  protected CD4CodeCoCoChecker getCdChecker() {
    return this.cdChecker;
  }

  public IMontiThingsGlobalScope processModels(@NotNull Path... modelPaths) {
    Preconditions.checkArgument(modelPaths != null);
    Preconditions.checkArgument(!Arrays.asList(modelPaths).contains(null));
    ModelPath mp = new ModelPath(Arrays.asList(modelPaths));
    ICD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension(CD_FILE_EXTENSION)
      .build();
    IMontiThingsGlobalScope montiThingsGlobalScope = MontiThingsMill.montiThingsGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension(MT_FILE_EXTENSION)
      .build();
    resolvingDelegates(montiThingsGlobalScope, cd4CGlobalScope);
    addBasicTypes(montiThingsGlobalScope);
    addLibraryFunctions(montiThingsGlobalScope);
    this.processModels(cd4CGlobalScope);
    this.processModels(montiThingsGlobalScope);
    return montiThingsGlobalScope;
  }

  protected void resolvingDelegates(@NotNull IMontiThingsGlobalScope montiThingsGlobalScope,
    @NotNull ICD4CodeGlobalScope cd4CGlobalScope) {
    CD4CodeResolver cd4CodeResolver = new CD4CodeResolver(cd4CGlobalScope);
    montiThingsGlobalScope.addAdaptedFieldSymbolResolver(cd4CodeResolver);
    montiThingsGlobalScope.addAdaptedTypeSymbolResolver(cd4CodeResolver);
  }

  public void processModels(@NotNull IMontiThingsGlobalScope scope) {
    processModels(scope, false);
  }

  public void processModels(@NotNull IMontiThingsGlobalScope scope, boolean shouldLog) {
    Preconditions.checkArgument(scope != null);
    for (IMontiThingsArtifactScope as : this.createSymbolTable(scope)) {
      ASTMACompilationUnit a = (ASTMACompilationUnit) as.getAstNode();
      if (shouldLog) {
        Log.info("Check model: " + a.getComponentType().getSymbol().getFullName(), TOOL_NAME);
      }
      a.accept(this.getMTChecker());
    }
  }

  public void processModels(@NotNull ICD4CodeGlobalScope scope) {
    processModels(scope, false);
  }

  public void processModels(@NotNull ICD4CodeGlobalScope scope, boolean shouldLog) {
    Preconditions.checkArgument(scope != null);
    for (ICD4CodeArtifactScope a : this.createSymbolTable(scope)) {
      if (shouldLog) {
        Log.info("Check model: " + a.getName(), TOOL_NAME);
      }
      for (ICD4CodeScope as : a.getSubScopes()) {
        ASTCDPackage astNode = (ASTCDPackage) as.getSpanningSymbol().getAstNode();
        astNode.accept(this.getCdChecker());
      }
    }
  }

  public Collection<IMontiThingsArtifactScope> createSymbolTable(
    @NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    Collection<IMontiThingsArtifactScope> result = new HashSet<>();
    Collection<ASTMACompilationUnit> models = parseModels(scope);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        // iterate with an iterator in order to avoid ConcurrentModificationException, as models are transformed
        for (Iterator<ASTMACompilationUnit> iterator = models.iterator(); iterator.hasNext(); ) {
            ASTMACompilationUnit ast = iterator.next();

            for (MontiThingsTrafo trafo : trafos) {
                try {
                    additionalTrafoModels.addAll(trafo.transform(models, additionalTrafoModels, ast));
                } catch (Exception e) {
                    Log.error(e.getCause().getMessage());
                    e.printStackTrace();
                }
            }
        }
        models.addAll(additionalTrafoModels);

        for (ASTMACompilationUnit ast : models) {
      MontiThingsSymbolTableCreatorDelegator symTab = new MontiThingsSymbolTableCreatorDelegator(
        scope);
      result.add(symTab.createFromAST(ast));
    }
    
    return result;
  }

  public Collection<ICD4CodeArtifactScope> createSymbolTable(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    Collection<ICD4CodeArtifactScope> result = new HashSet<>();
    for (ASTCDCompilationUnit ast : parseModels(scope)) {
      CD4CodeSymbolTableCreatorDelegator symTab = new CD4CodeSymbolTableCreatorDelegator(scope);
      result.add(symTab.createFromAST(ast));
    }
    return result;
  }

  public Collection<ASTMACompilationUnit> parseModels(@NotNull IMontiThingsGlobalScope scope) {
    return (Collection<ASTMACompilationUnit>) ParserUtil
      .parseModels(scope, MT_FILE_EXTENSION, new MontiThingsParser());
  }

  public Collection<ASTCDCompilationUnit> parseModels(@NotNull ICD4CodeGlobalScope scope) {
    return (Collection<ASTCDCompilationUnit>) ParserUtil
      .parseModels(scope, CD_FILE_EXTENSION, new CD4CodeParser());
  }

  public Optional<ASTMACompilationUnit> parseMT(@NotNull String filename) {
    return (Optional<ASTMACompilationUnit>) ParserUtil.parse(filename, new MontiThingsParser());
  }

  public Optional<ASTCDCompilationUnit> parseCD(@NotNull String filename) {
    return (Optional<ASTCDCompilationUnit>) ParserUtil.parse(filename, new CD4CodeParser());
  }

  public Collection<ASTMACompilationUnit> parseMT(@NotNull Path path) {
    return (Collection<ASTMACompilationUnit>) ParserUtil
      .parse(path, MT_FILE_EXTENSION, new MontiThingsParser());
  }

  public Collection<ASTCDCompilationUnit> parseCD(@NotNull Path path) {
    return (Collection<ASTCDCompilationUnit>) ParserUtil
      .parse(path, CD_FILE_EXTENSION, new CD4CodeParser());
  }

  public void addBasicTypes(@NotNull IMontiThingsScope scope) {
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._boolean);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._char);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._short);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._String);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._int);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._long);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._float);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._double);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._null);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._Object);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._array);
  }

  public void addLibraryFunctions(@NotNull IMontiThingsScope scope) {
    FunctionSymbol log = createFunction("log", scope);
    addParam(log, "message", SymTypeExpressionFactory.createTypeObject("String", scope));

    FunctionSymbol delay = createFunction("delay", scope);
    addParam(delay, "milliseconds", SymTypeExpressionFactory.createTypeConstant("int"));

    createFunction("now_ns", SymTypeExpressionFactory.createTypeObject("String", scope), scope);

    createFunction("now", SymTypeExpressionFactory.createTypeObject("long", scope), scope);

    // Library functions added by dds/replayer/MTReplayLibrary
    // assume long = unsigned long long since Monticore does not support all types
    createFunction("getNanoTimestamp", SymTypeExpressionFactory.createTypeObject("long", scope), scope);
  
    FunctionSymbol delayNanoseconds = createFunction("delayNanoseconds", scope);
    addParam(delayNanoseconds, "nanoseconds", SymTypeExpressionFactory.createTypeConstant("long"));

    FunctionSymbol subtract = createFunction("subtract", scope);
    addParam(subtract, "v1", SymTypeExpressionFactory.createTypeConstant("long"));
    addParam(subtract, "v2", SymTypeExpressionFactory.createTypeConstant("long"));

    FunctionSymbol getNsFromMap = createFunction("getNsFromMap", scope);
    addParam(getNsFromMap, "index", SymTypeExpressionFactory.createTypeConstant("int"));

    FunctionSymbol storeNsInMap = createFunction("storeNsInMap", scope);
    addParam(storeNsInMap, "index", SymTypeExpressionFactory.createTypeConstant("int"));
    addParam(storeNsInMap, "ts", SymTypeExpressionFactory.createTypeConstant("long"));

    // TODO: Allow method overloading
    FunctionSymbol nondeterminismLong = createFunction("nd", SymTypeExpressionFactory.createTypeObject("long", scope), scope);
    addParam(nondeterminismLong, "value", SymTypeExpressionFactory.createTypeConstant("long"));
  }
}
