// (c) https://github.com/MontiCore/monticore
package mtconfig;

import bindings._symboltable.adapters.MCQualifiedName2ComponentTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import montithings.MontiThingsMill;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsGlobalScope;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCoChecker;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig._symboltable.IMTConfigArtifactScope;
import mtconfig._symboltable.IMTConfigGlobalScope;
import mtconfig._symboltable.MTConfigScopesGenitorDelegator;
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

  public static String FILE_ENDING = "mtcfg";

  protected IMTConfigArtifactScope artifactScope;

  protected MTConfigCoCoChecker checker;

  protected boolean isSymTabInitialized;

  protected IMontiThingsGlobalScope mtGlobalScope;

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
  public IMTConfigGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    if(this.mtGlobalScope == null) {
      MontiThingsMill.reset();
      MontiThingsMill.init();
      IMontiThingsGlobalScope newMtGlobalScope = MontiThingsMill.globalScope();
      newMtGlobalScope.setModelPath(mp);
      newMtGlobalScope.setFileExt("mt");
      this.mtGlobalScope = newMtGlobalScope;
      MontiThingsTool tool = new MontiThingsTool();
      tool.processModels(mp);
    }

    MCQualifiedName2ComponentTypeResolvingDelegate componentTypeResolvingDelegate = new MCQualifiedName2ComponentTypeResolvingDelegate(this.mtGlobalScope);
    MCQualifiedName2PortResolvingDelegate portResolvingDelegate = new MCQualifiedName2PortResolvingDelegate(this.mtGlobalScope);

    MTConfigMill.reset();
    MTConfigMill.init();
    MTConfigMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    IMTConfigGlobalScope mtConfigGlobalScope = MTConfigMill.globalScope();
    mtConfigGlobalScope.setModelPath(mp);
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
  public IMTConfigGlobalScope createSymboltable(ASTMTConfigUnit ast,
      File... modelPaths) {

    IMTConfigGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast,globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public IMTConfigGlobalScope createSymboltable(ASTMTConfigUnit ast,
      IMTConfigGlobalScope globalScope) {

    MTConfigScopesGenitorDelegator stc = new MTConfigScopesGenitorDelegator();
    artifactScope = stc.createFromAST(ast);

    return globalScope;
  }

  public IMontiThingsGlobalScope getMtGlobalScope() {
    return mtGlobalScope;
  }

  /**
   * Setter for the global scope that should be used for resolving non native symbols.
   * @param mtGlobalScope globalScope used for resolving non native symbols
   */
  public void setMtGlobalScope(IMontiThingsGlobalScope mtGlobalScope) {
    this.mtGlobalScope = mtGlobalScope;
  }
}
