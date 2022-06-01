package montithings.services.prolog_generator.oclquery.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings._visitor.MontiThingsTraverser;

import java.util.HashSet;
import java.util.Set;

public class FindNameExpressionsVisitor implements MontiThingsTraverser {

  public Set<String> getNames() {
    return names;
  }

  private Set<String> names;

  public FindNameExpressionsVisitor() {
    names = new HashSet<>();
  }

  @Override
  public void handle (ASTNameExpression node) {
    names.add(node.getName());
  }
}
