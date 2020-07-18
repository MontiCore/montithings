// (c) https://github.com/MontiCore/monticore
package cdlangextension;

import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._symboltable.CDLangExtensionArtifactScope;
import cdlangextension._symboltable.CDLangExtensionGlobalScope;
import cdlangextension._symboltable.CDLangExtensionLanguage;
import cdlangextension._symboltable.CDLangExtensionSymbolTableCreatorDelegator;
import cdlangextension._symboltable.adapters.MCQualifiedName2CDTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.io.paths.ModelPath;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Provides useful methods for handling the CDLangExtension language.
 *
 */
public class CDLangExtensionTool {

  protected CDLangExtensionLanguage language;

  private CDLangExtensionArtifactScope artifactScope;

  protected CDLangExtensionCoCoChecker checker;

  protected boolean isSymTabInitialized;

  private CD4AnalysisGlobalScope cdGlobalScope;

  public CDLangExtensionTool() {
    this(CDLangExtensionCoCos.createChecker() ,new CDLangExtensionLanguage());
  }

  public CDLangExtensionTool(@NotNull CDLangExtensionCoCoChecker checker, @NotNull CDLangExtensionLanguage language) {
    Preconditions.checkArgument(checker != null);
    Preconditions.checkArgument(language != null);
    this.checker = checker;
    this.language = language;
    this.isSymTabInitialized = false;
  }

  public CDLangExtensionArtifactScope getArtifactScope() {
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


    MCQualifiedName2CDTypeResolvingDelegate componentTypeResolvingDelegate;
    if(this.cdGlobalScope ==null) {
      CD4AnalysisLanguage mtLang = CD4AnalysisMill.cD4AnalysisLanguageBuilder().build();
      CD4AnalysisGlobalScope newMtGlobalScope = CD4AnalysisMill.cD4AnalysisGlobalScopeBuilder()
          .setModelPath(mp)
          .setCD4AnalysisLanguage(mtLang)
          .build();
      componentTypeResolvingDelegate = new MCQualifiedName2CDTypeResolvingDelegate(newMtGlobalScope);
    }
    else{
      componentTypeResolvingDelegate = new MCQualifiedName2CDTypeResolvingDelegate(this.cdGlobalScope);
    }

    CDLangExtensionGlobalScope cDLangExtensionGlobalScope = new CDLangExtensionGlobalScope(mp, language);
    cDLangExtensionGlobalScope.addAdaptedCDTypeSymbolResolvingDelegate(componentTypeResolvingDelegate);

    isSymTabInitialized = true;
    return cDLangExtensionGlobalScope;
  }

  /**
   * Creates a GlobalScope that uses CDLangExtension AST and a given model path.
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
   * Creates a GlobalScope that uses CDLangExtension AST and a given model path.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return created global scope
   */
  public CDLangExtensionGlobalScope createSymboltable(ASTCDLangExtensionUnit ast,
      CDLangExtensionGlobalScope globalScope) {

    CDLangExtensionSymbolTableCreatorDelegator stc = language
        .getSymbolTableCreator(globalScope);
    artifactScope = stc.createFromAST(ast);

    return globalScope;
  }

  public CD4AnalysisGlobalScope getCdGlobalScope() {
    return cdGlobalScope;
  }

  public void setCdGlobalScope(CD4AnalysisGlobalScope cdGlobalScope) {
    this.cdGlobalScope = cdGlobalScope;
  }
}
