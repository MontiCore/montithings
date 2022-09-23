// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;

import java.util.Optional;

public class FieldAccessExprNameCompleter implements CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected CommonExpressionsTraverser traverser;
  protected String qualifiedName = "";
  protected boolean skip = true;
  protected Optional<ASTNameExpression> nameExpression;

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (skip) {
      skip = false;
    } else {
      qualifiedName = "." + node.getName() + qualifiedName;
    }
  }

  @Override
  public void visit(ASTNameExpression node) {
    qualifiedName = node.getName() + qualifiedName;
    nameExpression = Optional.of(node);
  }

  public String getName() {
    return qualifiedName;
  }

  public void clear() {
    qualifiedName = "";
    skip = true;
    nameExpression = Optional.empty();
  }

  public Optional<ASTNameExpression> getNameExpression () {
    return nameExpression;
  }
}
