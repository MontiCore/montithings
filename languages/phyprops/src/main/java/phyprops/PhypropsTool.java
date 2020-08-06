// (c) https://github.com/MontiCore/monticore
package phyprops;

import bindings._symboltable.adapters.MCQualifiedName2ComponentTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import montithings.MontiThingsMill;
import montithings._symboltable.MontiThingsGlobalScope;
import montithings._symboltable.MontiThingsLanguage;
import org.codehaus.commons.nullanalysis.NotNull;
import phyprops._ast.ASTPhypropsUnit;
import phyprops._cocos.PhypropsCoCoChecker;
import phyprops._cocos.PhypropsCoCos;
import phyprops._symboltable.PhypropsArtifactScope;
import phyprops._symboltable.PhypropsGlobalScope;
import phyprops._symboltable.PhypropsLanguage;
import phyprops._symboltable.PhypropsSymbolTableCreatorDelegator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Provides useful methods for handling the Phyprops language.
 */
public class PhypropsTool {

  protected PhypropsLanguage language;

  private PhypropsArtifactScope artifactScope;

  protected PhypropsCoCoChecker checker;

  protected boolean isSymTabInitialized;

  private MontiThingsGlobalScope mtGlobalScope;

  public PhypropsTool() {
    this(PhypropsCoCos.createChecker() ,new PhypropsLanguage());
  }

  public PhypropsTool(@NotNull PhypropsCoCoChecker checker, @NotNull PhypropsLanguage language) {
    Preconditions.checkArgument(checker != null);
    Preconditions.checkArgument(language != null);
    this.checker = checker;
    this.language = language;
    this.isSymTabInitialized = false;
  }

  public PhypropsArtifactScope getArtifactScope() {
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
  public PhypropsGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);


    MCQualifiedName2ComponentTypeResolvingDelegate componentTypeResolvingDelegate;
    if(this.mtGlobalScope ==null) {
      MontiThingsLanguage mtLang = MontiThingsMill.montiThingsLanguageBuilder().build();
      MontiThingsGlobalScope newMtGlobalScope = MontiThingsMill.montiThingsGlobalScopeBuilder()
          .setModelPath(mp)
          .setMontiThingsLanguage(mtLang)
          .build();
      componentTypeResolvingDelegate = new MCQualifiedName2ComponentTypeResolvingDelegate(newMtGlobalScope);
    }
    else{
      componentTypeResolvingDelegate = new MCQualifiedName2ComponentTypeResolvingDelegate(this.mtGlobalScope);
    }

    PhypropsGlobalScope phypropsGlobalScope = new PhypropsGlobalScope(mp, language);
    phypropsGlobalScope.addAdaptedComponentTypeSymbolResolvingDelegate(componentTypeResolvingDelegate);

    isSymTabInitialized = true;
    return phypropsGlobalScope;
  }

  /**
   * Creates a GlobalScope from a given model path and adds the given AST to it.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public PhypropsGlobalScope createSymboltable(ASTPhypropsUnit ast,
      File... modelPaths) {

    PhypropsGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast,globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public PhypropsGlobalScope createSymboltable(ASTPhypropsUnit ast,
      PhypropsGlobalScope globalScope) {

    PhypropsSymbolTableCreatorDelegator stc = language
        .getSymbolTableCreator(globalScope);
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
