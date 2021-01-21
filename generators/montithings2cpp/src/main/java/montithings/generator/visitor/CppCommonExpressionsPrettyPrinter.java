// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import montithings.generator.helper.ASTNoData;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import javax.measure.converter.UnitConverter;

import static montithings.generator.visitor.MySIUnitLiteralsPrettyPrinter.*;

public class CppCommonExpressionsPrettyPrinter extends CommonExpressionsPrettyPrinter {

  TypeCheck tc;

  public CppCommonExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    this.realThis = this;
  }

  @Override public void handle(ASTLessEqualExpression node) {
    handleInfix(node, "<=");
  }

  @Override public void handle(ASTGreaterEqualExpression node) {
    handleInfix(node, ">=");
  }

  @Override public void handle(ASTLessThanExpression node) {
    handleInfix(node, "<");
  }

  @Override public void handle(ASTGreaterThanExpression node) {
    handleInfix(node, ">");
  }

  @Override
  public void handle(ASTEqualsExpression node) {
    handleInfix(node, "==");
  }

  @Override public void handle(ASTNotEqualsExpression node) {
    handleInfix(node, "!=");
  }

  @Override
  public void handle(ASTMultExpression node){
    handleInfix(node, "*");
  }

  @Override
  public void handle(ASTDivideExpression node){
    handleInfix(node, "/");
  }

  @Override
  public void handle(ASTModuloExpression node){
    handleInfix(node, "%");
  }

  @Override
  public void handle(ASTPlusExpression node){
    handleInfix(node, "+");
  }

  @Override
  public void handle(ASTMinusExpression node){
    handleInfix(node, "-");
  }

  protected void handleInfix(ASTInfixExpression node, String infix) {
    CppExpressionPrettyPrinter expressionPP = new CppExpressionPrettyPrinter(getPrinter());
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.getLeft() instanceof ASTNameExpression &&
      node.getRight() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      expressionPP.handle((ASTNameExpression) node.getLeft(), true);
    }
    else if(tc.typeOf(node.getLeft()) instanceof SymTypeOfNumericWithSIUnit &&
            tc.typeOf(node.getRight()) instanceof SymTypeOfNumericWithSIUnit) {
      //SIUnit Types are used - conversion might be necessary
      node.getLeft().accept(getRealThis());
      getPrinter().print(infix);
      UnitConverter converter = getSIConverter(tc.typeOf(node.getLeft()), tc.typeOf(node.getRight()));
      getPrinter().print(factorStart(converter));
      node.getRight().accept(getRealThis());
      getPrinter().print(factorEnd(converter));
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
      return;
    }
    else {
      node.getLeft().accept(getRealThis());
    }
    getPrinter().print(infix);
    if (node.getRight() instanceof ASTNameExpression &&
      node.getLeft() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      expressionPP.handle((ASTNameExpression) node.getRight(), true);
    }
    else {
      node.getRight().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
