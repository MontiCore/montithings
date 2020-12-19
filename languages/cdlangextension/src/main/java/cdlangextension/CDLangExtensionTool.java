// (c) https://github.com/MontiCore/monticore
package cdlangextension;

import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._symboltable.CDLangExtensionGlobalScope;
import cdlangextension._symboltable.CDLangExtensionSymbolTableCreatorDelegator;
import cdlangextension._symboltable.ICDLangExtensionArtifactScope;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.io.paths.ModelPath;
import montithings.MontiThingsTool;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Provides useful methods for handling the CDLangExtension language.
 */
public class CDLangExtensionTool {

  public static final String FILE_ENDING = "cde";

  private ICDLangExtensionArtifactScope artifactScope;

  protected CDLangExtensionCoCoChecker checker;

  protected boolean isSymTabInitialized;

  private ICD4CodeGlobalScope cdGlobalScope;

  public CDLangExtensionTool() {
    this(CDLangExtensionCoCos.createChecker());
  }

  public CDLangExtensionTool(@NotNull CDLangExtensionCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
  }

  public ICDLangExtensionArtifactScope getArtifactScope() {
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
  public CDLangExtensionGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);


    CD4CodeResolver cd4aResolver;
    if(this.cdGlobalScope ==null) {
      ICD4CodeGlobalScope cd4aGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder()
          .setModelPath(mp)
          .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
          .build();
      cd4aResolver = new CD4CodeResolver(cd4aGlobalScope);
      this.cdGlobalScope = cd4aGlobalScope;
    }
    else{
      cd4aResolver = new CD4CodeResolver(this.cdGlobalScope);
    }

    MontiThingsTool tool = new MontiThingsTool();
    tool.processModels(this.cdGlobalScope);

    CDLangExtensionGlobalScope cDLangExtensionGlobalScope = new CDLangExtensionGlobalScope(mp, FILE_ENDING);
    cDLangExtensionGlobalScope.addAdaptedFieldSymbolResolver(cd4aResolver);
    cDLangExtensionGlobalScope.addAdaptedTypeSymbolResolver(cd4aResolver);
    cDLangExtensionGlobalScope.addAdaptedCDTypeSymbolResolver(cd4aResolver);

    isSymTabInitialized = true;
    return cDLangExtensionGlobalScope;
  }

  /**
   * Creates a GlobalScope from a given model path and adds the given AST to it.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public CDLangExtensionGlobalScope createSymboltable(ASTCDLangExtensionUnit ast,
      File... modelPaths) {

    CDLangExtensionGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast,globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public CDLangExtensionGlobalScope createSymboltable(ASTCDLangExtensionUnit ast,
      CDLangExtensionGlobalScope globalScope) {

    CDLangExtensionSymbolTableCreatorDelegator stc = new CDLangExtensionSymbolTableCreatorDelegator(globalScope);
    artifactScope = stc.createFromAST(ast);

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
}
