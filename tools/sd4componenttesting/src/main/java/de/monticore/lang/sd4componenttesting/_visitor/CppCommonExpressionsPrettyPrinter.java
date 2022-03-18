// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTInfixExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.prettyprint.IndentPrinter;

public class CppCommonExpressionsPrettyPrinter extends montithings.generator.prettyprinter.CppCommonExpressionsPrettyPrinter {

  public CppCommonExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  protected void handleInfix(ASTInfixExpression node, String infix) {
    node.getLeft().accept(this.getTraverser());
    this.getPrinter().print(" " + infix + " ");
    node.getRight().accept(this.getTraverser());
  }

  public void handle(ASTFieldAccessExpression node) {
    String portName = node.getName();
    String compName = ((ASTNameExpression)node.getExpression()).getName();

    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();
    String compTypeName = mainComponent.getSubComponent(compName).get().getType().getName();

    portName = portName.substring(0,1).toUpperCase() + portName.substring(1);
    compName = compName.substring(0,1).toUpperCase() + compName.substring(1);

    this.getPrinter().print(" portSpy" + compTypeName + compName + portName + ".getRecordedMessages().back().value().getPayload().value() ");
  }
}
