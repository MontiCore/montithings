package montithings.services.prolog_generator.oclquery.generator;

import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import montithings.services.prolog_generator.Utils;

public class OCLExpressionsToPrologPrettyPrinter extends OCLExpressionsPrettyPrinter {
  public OCLExpressionsToPrologPrettyPrinter(IndentPrinter indentPrinter) {
    super(indentPrinter);
  }

  @Override
  public void handle(ASTOCLVariableDeclaration node) {
    getPrinter().print(Utils.capitalize(node.getName()));
    if (node.isPresentExpression()) {
      getPrinter().print(" is ");
      node.getExpression().accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTTypeIfExpression node) {
    //call custom prolog function instanceOf
    getPrinter().print("(instanceOf(");
    getPrinter().print(node.getName() + ", ");
    node.getMCType().accept(getTraverser());
    getPrinter().print(") -> ");
    node.getThenExpression().accept(getTraverser());
    getPrinter().print("; ");
    node.getElseExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTIfThenElseExpression node) {
    getPrinter().print("(");
    node.getCondition().accept(getTraverser());
    getPrinter().print(" -> ");
    node.getThenExpression().accept(getTraverser());
    getPrinter().print("; ");
    node.getElseExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTImpliesExpression node) {
    getPrinter().print("( \\+ ");
    node.getLeft().accept(getTraverser());
    getPrinter().print(" ; ");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTEquivalentExpression node) {
    getPrinter().print("(( \\+ ");
    node.getLeft().accept(getTraverser());
    getPrinter().print(" ; ");
    node.getRight().accept(getTraverser());
    getPrinter().print("), (\\+ ");
    node.getRight().accept(getTraverser());
    getPrinter().print(" ; ");
    node.getLeft().accept(getTraverser());
    getPrinter().print("))");
  }

  @Override
  public void handle(ASTForallExpression node) {
    getPrinter().print("forall(");
    for (ASTInDeclaration inDeclaration : node.getInDeclarationList()) {
      inDeclaration.accept(getTraverser());
      getPrinter().print(", ");
    }
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTExistsExpression node) {
    getPrinter().print("(\\+ forall(");
    for (ASTInDeclaration inDeclaration : node.getInDeclarationList()) {
      inDeclaration.accept(getTraverser());
      getPrinter().print(", ");
    }
    getPrinter().print("\\+ ");
    node.getExpression().accept(getTraverser());
    getPrinter().print("))");
  }

  @Override
  public void handle(ASTLetinExpression node) {
    getPrinter().print("(");
    for (ASTOCLVariableDeclaration oclVariableDeclaration : node.getOCLVariableDeclarationList()) {
      oclVariableDeclaration.accept(getTraverser());
      getPrinter().print(", ");
    }
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTInDeclaration node) {
    for (int i = 0; i < node.sizeInDeclarationVariables(); i++) {
      node.getInDeclarationVariable(i).accept(getTraverser());
      if (node.isPresentExpression()) {
        getPrinter().print(" = ");
        node.getExpression().accept(getTraverser());
      }
      if (i != node.sizeInDeclarationVariables() - 1) {
        getPrinter().print(", ");
      }
    }
  }

  @Override
  public void handle(ASTInDeclarationVariable node) {
    getPrinter().print(Utils.capitalize(node.getName()));
  }

  @Override
  public void handle(ASTInstanceOfExpression node) {
    //call custom prolog function instanceOf
    getPrinter().print("instanceOf(");
    node.getExpression().accept(getTraverser());
    getPrinter().print(", ");
    node.getMCType().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTOCLAtPreQualification node) {
    Log.error("Cannot use OCLAtPreQualification in OCL queries translated to Prolog");
  }

  //TODO: TypeCastExpression, AnyExpression, OCLArrayQualification, OCLTransitiveQualification, IterateExpression
}