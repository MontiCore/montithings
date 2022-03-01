// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import montithings.MontiThingsTool;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Tool to process CD4Code files and convert them to sym files
 */
public class CD4MTTool {

  public static final String TOOL_NAME = "CD4MontiThingsTool";

  /**
   * Takes CD files and converts them to sym files. 
   * The folder within the symbolPath will be the same as the folder structure of the CD files.
   * 
   * @param modelPath model path of the given CD files
   * @param cdFiles pathes of the CD files to be converted
   * @param symbolPath path to place the symbol files in
   */
  public static void convertToSymFile(File modelPath, Collection<String> cdFiles, String symbolPath) {
    Log.info("==== Start Serialize Class Diagrams ====", TOOL_NAME);

    for (String cd : cdFiles) {
      try {
        CD4CodeMill.reset();
        CD4CodeMill.init();
        
        final ASTCDCompilationUnit ast = parse(cd);
        ICD4CodeArtifactScope artifact = createSymbolTable(modelPath, ast);
        applyRoleNameTrafo(ast);
        applyFieldsFromNavigableRolesTrafo(ast);
        checkCoCos(ast);
        createSymbolFile(modelPath, symbolPath, cd, ast, artifact);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    Log.info("==== End Serialize Class Diagrams ====", TOOL_NAME);
  }

  protected static ASTCDCompilationUnit parse(String cd) throws IOException {
    final ASTCDCompilationUnit ast = CD4CodeMill.parser().parse(cd).get();
    new CD4CodeDirectCompositionTrafo().transform(ast);
    Log.info("Successfully parsed " + ast.getCDDefinition().getName(), TOOL_NAME);
    return ast;
  }

  protected static ICD4CodeArtifactScope createSymbolTable(File modelPath,
    ASTCDCompilationUnit ast) {
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().setModelPath(new ModelPath(modelPath.toPath()));
    ((CD4CodeGlobalScope) CD4CodeMill.globalScope()).addBuiltInTypes();
    CD4CodeMill.globalScope().add(CD4CodeMill.typeSymbolBuilder()
            .setName("String")
            .setFullName("String")
            .setEnclosingScope(CD4CodeMill.globalScope())
            .setSpannedScope(CD4CodeMill.scope())
            .build());
    MontiThingsTool.addPortSymbolsToCD4CGlobalScope();
    ICD4CodeArtifactScope artifact = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    return artifact;
  }

  protected static void applyRoleNameTrafo(ASTCDCompilationUnit ast) {
    final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
    final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
    traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
    cdAssociationRoleNameTrafo.transform(ast);
  }

  protected static void applyFieldsFromNavigableRolesTrafo(ASTCDCompilationUnit ast) {
    final CDAssociationCreateFieldsFromNavigableRoles cdAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
    final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDAssociation(cdAssociationCreateFieldsFromNavigableRoles);
    traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromNavigableRoles);
    cdAssociationCreateFieldsFromNavigableRoles.transform(ast);
  }

  protected static void checkCoCos(ASTCDCompilationUnit ast) {
    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(ast);
    Log.info("Checked cocos for '" + ast.getCDDefinition().getName() + "'", TOOL_NAME);
  }

  protected static void createSymbolFile(File modelPath, String symbolPath, String cd,
    ASTCDCompilationUnit ast, ICD4CodeArtifactScope artifact) {
    String symbolFileName = symbolPath
      + cd.substring(modelPath.toString().length() + 1, cd.length() - 2)
      + "sym";
    final CD4CodeSymbols2Json symbols2Json = new CD4CodeSymbols2Json();
    final String path = symbols2Json.store(artifact, symbolFileName);
    Log.info("Created symbol file '" + symbolFileName + "'", TOOL_NAME);
  }

}
