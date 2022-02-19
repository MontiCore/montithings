// (c) https://github.com/MontiCore/monticore
package montithings;

import com.google.common.base.Preconditions;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._parser.MontiThingsParser;
import montithings._symboltable.*;
import montithings.cocos.MontiThingsCoCos;
import montithings.trafos.ComponentTypePortsNamingTrafo;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.MontiThingsError;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montithings.util.ClassDiagramUtil.createClassDiagram;
import static montithings.util.LibraryFunctionsUtil.addAllLibraryFunctions;

public class MontiThingsTool implements IMontiThingsTool {
  protected MontiThingsParser parser;

  protected MontiThingsSymbols2Json deSer;

  protected MontiThingsCoCoChecker checker;

  protected List<MontiThingsTrafo> trafos = new ArrayList<>();

  public static final String MT_FILE_EXTENSION = "mt";

  public static final String SYM_FILE_EXTENSION = "sym";

  public static final String TOOL_NAME = "MontiThingsTool";

  public MontiThingsTool() {
    this(MontiThingsCoCos.createChecker());
  }

  protected Set<ASTMACompilationUnit> models;

  public MontiThingsTool(@NotNull MontiThingsCoCoChecker checker) {
    this(checker, new MontiThingsParser(), new MontiThingsSymbols2Json());
  }

  protected MontiThingsTool(@NotNull MontiThingsCoCoChecker checker,
    @NotNull MontiThingsParser parser,
    @NotNull MontiThingsSymbols2Json deSer) {
    Preconditions.checkArgument(checker != null);
    Preconditions.checkArgument(deSer != null);
    Preconditions.checkArgument(parser != null);
    MontiThingsMill.init();
    this.parser = parser;
    this.deSer = deSer;
    this.checker = checker;
    models = new HashSet<>();
    ((MontiThingsDeSer) MontiThingsMill.globalScope().getDeSer())
      .ignoreSymbolKind("de.monticore.cdbasis._symboltable.CDPackageSymbol");
  }

  protected MontiThingsParser getParser() {
    return this.parser;
  }

  protected MontiThingsSymbols2Json getDeSer() {
    return this.deSer;
  }

  protected MontiThingsCoCoChecker getChecker() {
    return this.checker;
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

  protected String getMTFileExtension() {
    return MT_FILE_EXTENSION;
  }

  protected String getSymFileExtension() {
    return SYM_FILE_EXTENSION;
  }

  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(
      FilenameUtils.getExtension(file.getFileName().toString()).equals(this.getMTFileExtension()));
    try {
      return this.getParser().parse(file.toString());
    }
    catch (IOException e) {
      Log.error(String.format(MontiThingsError.TOOL_PARSE_IOEXCEPTION.toString(), file),
        e);
    }
    return Optional.empty();
  }

  @Override
  public IMontiThingsArtifactScope load(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(
      FilenameUtils.getExtension(file.getFileName().toString()).equals(this.getSymFileExtension()));
    return this.getDeSer().load(file.toString());
  }

  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.parse(Paths.get(filename));
  }

  @Override
  public IMontiThingsArtifactScope load(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.load(Paths.get(filename));
  }

  @Override
  public Collection<ASTMACompilationUnit> parseAll(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(this.getMTFileExtension()))
        .map(this::parse)
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }
    catch (IOException e) {
      Log.error(
        String.format(MontiThingsError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory),
        e);
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<IMontiThingsArtifactScope> loadAll(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(this.getSymFileExtension()))
        .map(this::load)
        .collect(Collectors.toSet());
    }
    catch (IOException e) {
      Log.error(
        String.format(MontiThingsError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory),
        e);
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<ASTMACompilationUnit> parseAll(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream()
      .flatMap(path -> this.parseAll(path).stream()).collect(Collectors.toSet());
  }

  @Override
  public Collection<IMontiThingsArtifactScope> loadAll(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream()
      .flatMap(path -> this.loadAll(path).stream())
      .collect(Collectors.toSet());
  }

  @Override
  public IMontiThingsScope createSymbolTable(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    MontiThingsFullSymbolTableCreator symTab = new MontiThingsFullSymbolTableCreator();
    return symTab.createFromAST(ast);
  }

  @Override
  public Collection<IMontiThingsScope> createSymbolTable(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    MontiThingsFullSymbolTableCreator symTab = new MontiThingsFullSymbolTableCreator();
    MontiThingsMill.globalScope();
    this.loadAll(scope).forEach(scope::addSubScope);
    if (models.isEmpty()) {
      models = new HashSet<>(this.parseAll(scope));
    }
    models = applyTrafos(models);
    return models.stream().map(symTab::createFromAST).collect(Collectors.toSet());
  }

  @Override
  public Collection<IMontiThingsScope> createSymbolTable(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    return this.createSymbolTable(this.createMTGlobalScope(directory));
  }

  @Override
  public IMontiThingsGlobalScope createMTGlobalScope(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    return this.createMTGlobalScope(new ModelPath(directories));
  }

  protected Set<ASTMACompilationUnit> applyTrafos(
    @NotNull Collection<ASTMACompilationUnit> models) {
    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();
    Set<ASTMACompilationUnit> result = new HashSet<>(models);

    boolean again = true;
    boolean firstLoop = true;
    while (again) {
      again = false;
      // iterate with an iterator in order to avoid ConcurrentModificationException,
      // as models are transformed
      for (ASTMACompilationUnit ast : models) {
        for (MontiThingsTrafo trafo : trafos) {
          if (firstLoop || trafo instanceof ComponentTypePortsNamingTrafo) {
            try {
              if (trafo instanceof ComponentTypePortsNamingTrafo) {
                ((ComponentTypePortsNamingTrafo) trafo).setChanged(false);
              }
              additionalTrafoModels.addAll(trafo.transform(models, additionalTrafoModels, ast));
              if (trafo instanceof ComponentTypePortsNamingTrafo) {
                if (((ComponentTypePortsNamingTrafo) trafo).isChanged()) {
                  again = true;
                }
              }
            }
            catch (Exception e) {
              Log.error(e.getCause().getMessage());
              e.printStackTrace();
            }
          }
        }
      }
      firstLoop = false;
    }

    result.addAll(additionalTrafoModels);
    return result;
  }

  protected IMontiThingsGlobalScope createMTGlobalScope(@NotNull ModelPath modelPath) {
    Preconditions.checkArgument(modelPath != null);
    IMontiThingsGlobalScope mtScope = MontiThingsMill.globalScope();
    mtScope.clear();
    mtScope.setModelPath(modelPath);
    mtScope.setFileExt(this.getMTFileExtension());
    this.addBasicTypes();
    addAllLibraryFunctions(mtScope);
    return mtScope;
  }

  @Override
  public void addBasicTypes() {
    BasicSymbolsMill.initializePrimitives();
    IMontiThingsArtifactScope artifactScope = MontiThingsMill.artifactScope();
    artifactScope.setEnclosingScope(MontiThingsMill.globalScope());
    artifactScope.setName("java.lang");
    this.add2Scope(artifactScope, MontiThingsMill.oOTypeSymbolBuilder()
      .setName("Object")
      .setEnclosingScope(MontiThingsMill.artifactScope())
      .setSpannedScope(MontiThingsMill.scope()).build());
    this.add2Scope(artifactScope, MontiThingsMill.oOTypeSymbolBuilder()
      .setName("String")
      .setEnclosingScope(MontiThingsMill.artifactScope())
      .setSpannedScope(MontiThingsMill.scope())
      .addSuperTypes(SymTypeExpressionFactory
        .createTypeObject("java.lang.Object", MontiThingsMill.globalScope()))
      .build());
    MontiThingsMill.globalScope().add(MontiThingsMill.typeSymbolBuilder()
      .setName("void")
      .setFullName("void")
      .setEnclosingScope(MontiThingsMill.globalScope())
      .setSpannedScope(MontiThingsMill.scope())
      .build());
    MontiThingsMill.globalScope().add(MontiThingsMill.typeSymbolBuilder()
      .setName("null")
      .setFullName("null")
      .setEnclosingScope(MontiThingsMill.globalScope())
      .setSpannedScope(MontiThingsMill.scope())
      .build());
  }

  protected void add2Scope(@NotNull IBasicSymbolsScope scope, @NotNull TypeSymbol... symbols) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbols);
    Arrays.stream(symbols).forEach(symbol -> {
      symbol.setEnclosingScope(scope);
      scope.add(symbol);
    });
  }

  protected void add2Scope(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol... symbols) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbols);
    Arrays.stream(symbols).forEach(symbol -> {
      symbol.setEnclosingScope(scope);
      scope.add(symbol);
    });
  }

  @Override
  public void checkCoCos(@NotNull ASTMACompilationUnit ast) {
    checkCoCos(ast, false);
  }

  public void checkCoCos(@NotNull ASTMACompilationUnit ast, boolean shouldLog) {
    Preconditions.checkArgument(ast != null);
    Log
      .info("Check model: " + ast.getPackage() + "." + ast.getComponentType().getName(), TOOL_NAME);
    this.checker.checkAll(ast);
  }

  @Override
  public void checkCoCos(@NotNull IMontiThingsGlobalScope scope) {
    checkCoCos(scope, false);
  }

  public void checkCoCos(@NotNull IMontiThingsGlobalScope scope, boolean shouldLog) {
    Preconditions.checkArgument(scope != null);
    scope.getSubScopes().stream()
      .filter(as -> !((MontiThingsArtifactScope) as).getPackageName().equals(""))
      .map(artifactScope -> (ASTMACompilationUnit) artifactScope.getAstNode())
      .forEach(this::checkCoCos);
  }

  @Override
  public void processModels(@NotNull IMontiThingsGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    createSymbolTable(scope);
    checkCoCos(scope);
  }

  @Override
  public IMontiThingsGlobalScope processModels(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    Preconditions.checkArgument(!Arrays.asList(directories).contains(null));
    ModelPath modelPath = new ModelPath(directories);
    IMontiThingsGlobalScope mtScope = this.createMTGlobalScope(modelPath);
    this.processModels(mtScope);
    return mtScope;
  }

  public IMontiThingsGlobalScope processModels(@NotNull ModelPath modelPath) {
    Preconditions.checkArgument(modelPath != null);
    IMontiThingsGlobalScope mtScope = this.createMTGlobalScope(modelPath);
    this.processModels(mtScope);
    return mtScope;
  }

  public CD4CodeGlobalScope createClassDiagrams(@NotNull MontiThingsGlobalScope scope, String symbolPath) {
    Preconditions.checkArgument(scope != null);
    if (models.isEmpty()) {
      models = new HashSet<>(this.parseAll(scope));
    }

    addPortSymbolsToCD4CGlobalScope();

    //create scopes for class diagrams
    Set<CD4CodeArtifactScope> scopes = new HashSet<>();
    for (ASTMACompilationUnit compilationUnit : models) {
      if (!compilationUnit.getComponentType().getPorts().isEmpty()) {
        scopes.add(createClassDiagram(compilationUnit));
      }
    }

    return convertCDScopesToSymbolFiles(symbolPath, scopes);
  }

  protected CD4CodeGlobalScope convertCDScopesToSymbolFiles(String symbolPath,
    Set<CD4CodeArtifactScope> scopes) {
    for (CD4CodeArtifactScope artifactScope : scopes) {
      CD4CodeMill.globalScope().addSubScope(artifactScope);
      artifactScope.setEnclosingScope(CD4CodeMill.globalScope());
      String symbolFileName = symbolPath
          + artifactScope.getName()
          + ".sym";
      final CD4CodeSymbols2Json symbols2Json = new CD4CodeSymbols2Json();
      final String path = symbols2Json.store(artifactScope, symbolFileName);
    }

    return (CD4CodeGlobalScope) CD4CodeMill.globalScope();
  }

  public CD4CodeGlobalScope createMissingClassDiagrams(@NotNull MontiThingsGlobalScope scope, String symbolPath) {
    Preconditions.checkArgument(scope != null);

    //create scopes for class diagrams
    Set<CD4CodeArtifactScope> scopes = new HashSet<>();
    for (ASTMACompilationUnit compilationUnit : ComponentTypePortsNamingTrafo.getChangedCompilationUnits()) {
      scopes.add(createClassDiagram(compilationUnit));
    }

    //convert scopes to symbol files
    return convertCDScopesToSymbolFiles(symbolPath, scopes);
  }

  public static void addPortSymbolsToCD4CGlobalScope() {
    TypeSymbol inPortType = CD4CodeMill.typeSymbolBuilder()
      .setName("InPort")
      .setFullName("InPort")
      .setEnclosingScope(CD4CodeMill.globalScope())
      .setSpannedScope(CD4CodeMill.scope())
      .build();
    inPortType
      .addTypeVarSymbol(CD4CodeMill.typeVarSymbolBuilder()
        .setName("T")
        .setFullName("T").build());
    TypeSymbol outPortType = CD4CodeMill
      .typeSymbolBuilder()
      .setName("OutPort")
      .setFullName("OutPort")
      .setEnclosingScope(CD4CodeMill.globalScope())
      .setSpannedScope(CD4CodeMill.scope())
      .build();
    inPortType.addTypeVarSymbol(CD4CodeMill.typeVarSymbolBuilder()
      .setName("T")
      .setFullName("T")
      .build());
    CD4CodeMill.globalScope().add(inPortType);
    CD4CodeMill.globalScope().add(outPortType);
  }
}
