// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiThingsFullSymbolTableCreator {
  protected MontiThingsScopesGenitorDelegator delegator;
  protected List<MontiThingsTraverser> traversers;

  public MontiThingsFullSymbolTableCreator() {
    delegator = MontiThingsMill.scopesGenitorDelegator();
    traversers = new ArrayList<>();
    MontiThingsTraverser t = MontiThingsMill.traverser();
    t.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCForMontiThings());
    t.add4OCLExpressions(new OCLExpressionsSymbolTableCompleter(null, null));
    t.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traversers.add(t);
  }

  public IMontiThingsArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    IMontiThingsArtifactScope artifactScope = delegator.createFromAST(rootNode);
    for (MontiThingsTraverser traverser : traversers) {
      artifactScope.accept(traverser);
    }
    return artifactScope;
  }
}
