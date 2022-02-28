package montithings._visitor;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings.MontiThingsMill;
import montithings._symboltable.IMontiThingsScope;

import java.util.List;

public class NameExpression2TypeValidator {

  public boolean isTypeReference(ASTNameExpression node) {
    if (node.getEnclosingScope().isPresentAstNode()) {
      ASTNode encNode = node.getEnclosingScope().getAstNode();

      // collect all top level field expressions
      TopLevelFieldAccessExprCollector exprCollector = new TopLevelFieldAccessExprCollector();
      MontiThingsTraverser t1 = MontiThingsMill.traverser();
      t1.add4CommonExpressions(exprCollector);
      t1.setCommonExpressionsHandler(exprCollector);

      encNode.accept(t1);
      List<ASTFieldAccessExpression> expr = exprCollector.getExpr();

      // compute a qualified name based on the field access and name expressions
      FieldAccessExprNameCompleter nameCompleter = new FieldAccessExprNameCompleter();
      MontiThingsTraverser t2 = MontiThingsMill.traverser();
      t2.add4CommonExpressions(nameCompleter);
      t2.add4ExpressionsBasis(nameCompleter);

      for (ASTFieldAccessExpression e : expr) {
        e.accept(t2);
        if (nameCompleter.getNameExpression().isPresent() &&
                nameCompleter.getNameExpression().get().equals(node)) {
          // if the qualified name for the matching AST node is found, check if it is a type reference
          return ((IMontiThingsScope) node.getEnclosingScope())
            .resolveTypeMany(nameCompleter.getName()).size() == 1;
        }
        nameCompleter.clear();
      }
    }
    return false;
  }
}
