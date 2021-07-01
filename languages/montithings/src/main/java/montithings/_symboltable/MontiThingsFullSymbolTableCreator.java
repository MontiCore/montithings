// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiThingsFullSymbolTableCreator {
  protected MontiThingsScopesGenitorDelegator delegator;
  protected List<MontiThingsTraverser> traversers;

  public MontiThingsFullSymbolTableCreator() {
    delegator = MontiThingsMill.scopesGenitorDelegator();
    traversers = new ArrayList<>();
    DeriveSymTypeOfMontiThingsCombine typeVisitor = new DeriveSymTypeOfMontiThingsCombine();
    OCLExpressionsSymbolTableCompleterForMontiThings ocl = new OCLExpressionsSymbolTableCompleterForMontiThings(null, null);
    ocl.setTypeVisitor(typeVisitor);
    SetExpressionsSymbolTableCompleterForMontiThings set = new SetExpressionsSymbolTableCompleterForMontiThings(null, null);
    set.setTypeVisitor(typeVisitor);
    MontiThingsTraverser t = MontiThingsMill.traverser();
    t.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCForMontiThings());
    t.add4OCLExpressions(ocl);
    t.add4BasicSymbols(ocl);
    t.setOCLExpressionsHandler(ocl);
    t.add4SetExpressions(set);
    t.add4BasicSymbols(set);
    t.setSetExpressionsHandler(set);
    t.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traversers.add(t);
  }

  public IMontiThingsArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    IMontiThingsArtifactScope artifactScope = delegator.createFromAST(rootNode);
    for (MontiThingsTraverser traverser : traversers) {
      rootNode.accept(traverser);
    }
    return artifactScope;
  }
}
