// (c) https://github.com/MontiCore/monticore
package mtconfig;

import bindings._symboltable.adapters.MCQualifiedName2ComponentTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import montithings.MontiThingsMill;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.MontiThingsGlobalScope;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCoChecker;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig._symboltable.IMTConfigArtifactScope;
import mtconfig._symboltable.MTConfigArtifactScope;
import mtconfig._symboltable.MTConfigGlobalScope;
import mtconfig._symboltable.MTConfigSymbolTableCreatorDelegator;
import mtconfig._symboltable.adapters.MCQualifiedName2PortResolvingDelegate;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Provides useful methods for handling the MTConfig language.
 */
public class MTConfigTool {

  protected static String FILE_ENDING = "mtcfg";

  private IMTConfigArtifactScope artifactScope;

  protected MTConfigCoCoChecker checker;

  protected boolean isSymTabInitialized;

  private MontiThingsGlobalScope mtGlobalScope;

  public MTConfigTool() {
    this(MTConfigCoCos.createChecker());
  }

  public MTConfigTool(@NotNull MTConfigCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
  }

  public IMTConfigArtifactScope getArtifactScope() {
    return artifactScope;
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
  public MTConfigGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);


    MCQualifiedName2ComponentTypeResolvingDelegate componentTypeResolvingDelegate;
    MCQualifiedName2PortResolvingDelegate portResolvingDelegate;
    if(this.mtGlobalScope ==null) {
      IMontiThingsGlobalScope newMtGlobalScope = MontiThingsMill.montiThingsGlobalScopeBuilder()
          .setModelPath(mp)
          .setModelFileExtension("mt")
          .build();
      MontiThingsTool tool = new MontiThingsTool();
      tool.processModels(this.mtGlobalScope);
      componentTypeResolvingDelegate = new MCQualifiedName2ComponentTypeResolvingDelegate(newMtGlobalScope);
      portResolvingDelegate = new MCQualifiedName2PortResolvingDelegate(newMtGlobalScope);
    }
    else{
      componentTypeResolvingDelegate = new MCQualifiedName2ComponentTypeResolvingDelegate(this.mtGlobalScope);
      portResolvingDelegate = new MCQualifiedName2PortResolvingDelegate(this.mtGlobalScope);
    }

    MTConfigGlobalScope mtConfigGlobalScope = new MTConfigGlobalScope(mp, FILE_ENDING);
    mtConfigGlobalScope.addAdaptedComponentTypeSymbolResolver(componentTypeResolvingDelegate);
    mtConfigGlobalScope.addAdaptedPortSymbolResolver(portResolvingDelegate);

    isSymTabInitialized = true;
    return mtConfigGlobalScope;
  }

  /**
   * Creates a GlobalScope from a given model path and adds the given AST to it.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public MTConfigGlobalScope createSymboltable(ASTMTConfigUnit ast,
      File... modelPaths) {

    MTConfigGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast,globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public MTConfigGlobalScope createSymboltable(ASTMTConfigUnit ast,
      MTConfigGlobalScope globalScope) {

    MTConfigSymbolTableCreatorDelegator stc = new MTConfigSymbolTableCreatorDelegator(globalScope);
    artifactScope = stc.createFromAST(ast);

    return globalScope;
  }

  public MontiThingsGlobalScope getMtGlobalScope() {
    return mtGlobalScope;
  }

  /**
   * Setter for the global scope that should be used for resolving non native symbols.
   * @param mtGlobalScope globalScope used for resolving non native symbols
   */
  public void setMtGlobalScope(MontiThingsGlobalScope mtGlobalScope) {
    this.mtGlobalScope = mtGlobalScope;
  }
}
