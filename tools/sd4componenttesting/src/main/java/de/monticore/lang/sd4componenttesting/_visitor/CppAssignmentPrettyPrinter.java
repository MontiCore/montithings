// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._visitor;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.prettyprint.IndentPrinter;

public class CppAssignmentPrettyPrinter extends montithings.generator.prettyprinter.CppAssignmentPrettyPrinter {
  public CppAssignmentPrettyPrinter(IndentPrinter printer, boolean isLogTracingEnabled, boolean suppressPostconditionCheck) {
    super(printer, isLogTracingEnabled, suppressPostconditionCheck);
  }

  public void handle(ASTIncSuffixExpression node) {
    this.currentlyNotSupported("IncSuffix");
  }

  public void handle(ASTDecSuffixExpression node) {
    this.currentlyNotSupported("DecSuffix");
  }

  public void handle(ASTIncPrefixExpression node) {
    this.currentlyNotSupported("IncPrefix");
  }

  public void handle(ASTDecPrefixExpression node) {
    this.currentlyNotSupported("DecPrefix");
  }

  public void handle(ASTAssignmentExpression node) {
    this.currentlyNotSupported("Assignment");
  }

  protected void currentlyNotSupported(String type) {
    this.getPrinter().print("LOG(WARNING) << \"" + type + " Expression is currently not supported \"");
  }
}
