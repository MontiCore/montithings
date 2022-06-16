package montithings.services.prolog_generator.oclquery.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import montithings._visitor.MontiThingsTraverser;

import java.util.HashSet;
import java.util.Set;

public class FindNameExpressionsVisitor implements MontiThingsTraverser {

  public Set<String> getNames() {
    names.removeAll(varNames);
    return names;
  }

  private Set<String> names;
  private Set<String> varNames;

  public FindNameExpressionsVisitor() {
    names = new HashSet<>();
    varNames = new HashSet<>();
  }

  @Override
  public void handle (ASTNameExpression node) {
    names.add(node.getName());
  }

  @Override
  public void visit (ASTOCLVariableDeclaration node) {
    varNames.add(node.getName());
  }

  @Override
  public void visit (ASTInDeclarationVariable node) {
    varNames.add(node.getName());
  }
}
