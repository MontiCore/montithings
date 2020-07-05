// (c) https://github.com/MontiCore/monticore
package cdlangextension;

import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._symboltable.CDLangExtensionArtifactScope;
import cdlangextension._symboltable.CDLangExtensionGlobalScope;
import cdlangextension._symboltable.CDLangExtensionLanguage;
import cdlangextension._symboltable.CDLangExtensionSymbolTableCreatorDelegator;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.io.paths.ModelPath;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides useful methods for handling the CDLangExtension language.
 *
 */
public class CDLangExtensionTool {

  protected CDLangExtensionLanguage language;

  private CDLangExtensionArtifactScope artifactScope;


  public CDLangExtensionTool() {
    this(new CDLangExtensionLanguage());
  }

  public CDLangExtensionTool(CDLangExtensionLanguage language) {
    this.language = language;
  }

  /**
   * Creates a GlobalScope that uses CDLangExtension AST and a given model path.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public CDLangExtensionGlobalScope createCDESymboltable(ASTCDLangExtensionUnit ast,
      List<File> modelPaths, CD4AnalysisArtifactScope cd4AnalysisArtifactScope) {
    CDLangExtensionGlobalScope globalScope = createCDESymboltable(ast, modelPaths);
    addCDSymbols(cd4AnalysisArtifactScope, globalScope);

    return globalScope;
  }

  /**
   * Creates a GlobalScope that uses CDLangExtension AST and a given model path.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public CDLangExtensionGlobalScope createCDESymboltable(ASTCDLangExtensionUnit ast,
      List<File> modelPaths) {
    ModelPath modelPath = new ModelPath(modelPaths.stream().map(mp -> Paths.get(mp.getAbsolutePath())).collect(Collectors.toList()));

    CDLangExtensionGlobalScope globalScope = new CDLangExtensionGlobalScope(modelPath, language);
    CDLangExtensionSymbolTableCreatorDelegator stc = language
        .getSymbolTableCreator(globalScope);
    this.artifactScope = stc.createFromAST(ast);

    return globalScope;
  }

  public void addCDSymbols(CD4AnalysisArtifactScope cd4AnalysisArtifactScope, CDLangExtensionGlobalScope globalScope) {
    for (CDDefinitionSymbol cdDefinitionSymbol:cd4AnalysisArtifactScope.getCDDefinitionSymbols().values()) {
      for (CDTypeSymbol s:
          cdDefinitionSymbol.getTypes()) {
        globalScope.add(s);
      }
    }
  }

  /**
   * Creates a GlobalScope that uses CD4Analysis AST and a given model path.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public CD4AnalysisArtifactScope createCDSymboltable(ASTCDCompilationUnit ast,
      List<File> modelPaths) {
    ModelPath modelPath = new ModelPath(modelPaths.stream().map(mp -> Paths.get(mp.getAbsolutePath())).collect(Collectors.toList()));
    CD4AnalysisLanguage language = new CD4AnalysisLanguage();

    CD4AnalysisGlobalScope globalScope = new CD4AnalysisGlobalScope(modelPath, language);
    CD4AnalysisSymbolTableCreatorDelegator stc = language
        .getSymbolTableCreator(globalScope);
    return stc.createFromAST(ast);
  }

  public CDLangExtensionArtifactScope getArtifactScope() {
    return artifactScope;
  }
}
