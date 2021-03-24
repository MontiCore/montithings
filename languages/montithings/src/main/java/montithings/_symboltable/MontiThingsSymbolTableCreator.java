// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc._ast.ASTMACompilationUnit;
import montithings._ast.ASTMTComponentType;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Symbol table creator. Does pretty much nothing right now. Only forwards calls to MontiArc
 * that MontiCore is not advanced enough to forward automatically.
 */
public class MontiThingsSymbolTableCreator extends MontiThingsSymbolTableCreatorTOP {

  public MontiThingsSymbolTableCreator() {
  }

  public MontiThingsSymbolTableCreator(IMontiThingsScope enclosingScope) {
    super(enclosingScope);
  }

  public MontiThingsSymbolTableCreator(Deque<? extends IMontiThingsScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public IMontiThingsArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : rootNode.getImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }
    IMontiThingsArtifactScope artifactScope = montithings.MontiThingsMill.montiThingsArtifactScopeBuilder().setPackageName(rootNode.getPackage().getQName()).setImportsList(imports).build();
    putOnStack(artifactScope);

    // for some reason the setLinkBetweenSpannedScopeAndNode doesn't accept
    // rootNode as an argument of type ASTMTComponentType.
    // Maybe a MontiCore bug? Maybe MontiCore looks for the first non-terminal
    // in our grammar instead of the "start" non-terminal? I dont know. Anyway,
    // that's why the following two lines fake the behavior that should have
    // been provided by setLinkBetweenSpannedScopeAndNode
    artifactScope.setAstNode(rootNode);
    rootNode.setSpannedScope(artifactScope);

    rootNode.accept(getRealThis());
    removeCurrentScope();
    return artifactScope;
  }

  @Override
  public void visit(ASTMTComponentType node) {
    getRealThis().visit((ASTComponentType) node);
  }

  @Override
  public void endVisit(ASTMTComponentType node) {
    getRealThis().endVisit((ASTComponentType) node);
  }
}
