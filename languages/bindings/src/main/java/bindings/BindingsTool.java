// (c) https://github.com/MontiCore/monticore
package bindings;

import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCoChecker;
import bindings._cocos.BindingsCoCos;
import bindings._parser.BindingsParser;
import bindings._symboltable.BindingsSymbolTableCreatorDelegator;
import bindings._symboltable.IBindingsGlobalScope;
import bindings._symboltable.IBindingsScope;
import bindings._symboltable.adapters.MCQualifiedName2ComponentInstanceResolvingDelegate;
import bindings._symboltable.adapters.MCQualifiedName2ComponentTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import montithings.MontiThingsMill;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

/**
 * Provides useful methods for handling the Bindings language.
 */
public class BindingsTool {

  public static final String FILE_ENDING = "mtb";

  protected BindingsCoCoChecker checker;

  protected boolean isSymTabInitialized;

  protected IMontiThingsGlobalScope mtGlobalScope;

  public BindingsTool() {
    this(BindingsCoCos.createChecker());
  }

  public BindingsTool(@NotNull BindingsCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
  }

  public Optional<ASTBindingsCompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    BindingsParser p = new BindingsParser();
    Optional<ASTBindingsCompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
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
  public IBindingsGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    MCQualifiedName2ComponentTypeResolvingDelegate componentTypeResolvingDelegate;
    MCQualifiedName2ComponentInstanceResolvingDelegate componentInstanceResolvingDelegate;
    if (this.mtGlobalScope == null) {
      this.mtGlobalScope = MontiThingsMill
        .montiThingsGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("mt")
        .build();
      MontiThingsTool tool = new MontiThingsTool();
      tool.addBasicTypes(mtGlobalScope);
      tool.processModels(this.mtGlobalScope);
    }
    componentTypeResolvingDelegate =
      new MCQualifiedName2ComponentTypeResolvingDelegate(this.mtGlobalScope);
    componentInstanceResolvingDelegate =
      new MCQualifiedName2ComponentInstanceResolvingDelegate(this.mtGlobalScope);

    IBindingsGlobalScope bindingsGlobalScope = BindingsMill
      .bindingsGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension("mtb")
      .build();
    bindingsGlobalScope
      .addAdaptedComponentTypeSymbolResolver(componentTypeResolvingDelegate);
    bindingsGlobalScope
      .addAdaptedComponentInstanceSymbolResolver(componentInstanceResolvingDelegate);

    isSymTabInitialized = true;
    return bindingsGlobalScope;
  }

  /**
   * Creates a GlobalScope from a given model path and adds the given AST to it.
   *
   * @param ast        node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public IBindingsGlobalScope createSymboltable(ASTBindingsCompilationUnit ast,
    File... modelPaths) {

    IBindingsGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast, globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast         node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public IBindingsGlobalScope createSymboltable(ASTBindingsCompilationUnit ast,
    IBindingsGlobalScope globalScope) {

    BindingsSymbolTableCreatorDelegator stc = new BindingsSymbolTableCreatorDelegator(globalScope);
    stc.createFromAST(ast);

    return globalScope;
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
  public IBindingsScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }

  public IMontiThingsGlobalScope getMtGlobalScope() {
    return mtGlobalScope;
  }

  /**
   * Setter for the global scope that should be used for resolving non native symbols.
   *
   * @param mtGlobalScope globalScope used for resolving non native symbols
   */
  public void setMtGlobalScope(IMontiThingsGlobalScope mtGlobalScope) {
    this.mtGlobalScope = mtGlobalScope;
  }
}
