package de.monticore.lang.sd4componenttesting._visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.prettyprint.IndentPrinter;

public class CppExpressionPrettyPrinter extends montithings.generator.prettyprinter.CppExpressionPrettyPrinter {
  public CppExpressionPrettyPrinter(IndentPrinter out){
    super(out);
  }

  public void handle(ASTNameExpression node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    String compTypeName = mainComponent.getName();
    String portName = node.getName();
    portName = portName.substring(0,1).toUpperCase() + portName.substring(1);

    this.getPrinter().print(" portSpy" + compTypeName + portName + ".getRecordedMessages().back().value().getPayload().value() ");
  }
}
