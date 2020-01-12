// (c) https://github.com/MontiCore/monticore
package montithings.visitor;

import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import montithings._visitor.MontiThingsVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author (last commit)
 */
public class GuardExpressionVisitor implements MCExpressionsVisitor, MontiThingsVisitor {

  List<ASTNameExpression> expressions = new ArrayList<>();

  public void visit(de.monticore.mcexpressions._ast.ASTNameExpression node) {
    expressions.add(node);
  }

  public List<ASTNameExpression> getExpressions(){
    return expressions;
  }
}
