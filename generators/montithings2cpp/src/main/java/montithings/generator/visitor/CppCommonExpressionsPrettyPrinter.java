// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import behavior._ast.ASTAgoQualification;
import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.StringTransformations;
import montithings.generator.helper.ASTNoData;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import javax.measure.converter.UnitConverter;

import static montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter.*;

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
    boolean isStringConcat =
      tc.typeOf(node.getLeft()).getTypeInfo().getName().equals("String") && infix.equals("+");
    CppExpressionPrettyPrinter expressionPP = new CppExpressionPrettyPrinter(getPrinter());
    CppBehaviorPrettyPrinter behaviorPP = new CppBehaviorPrettyPrinter(getPrinter());
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if ((node.getLeft() instanceof ASTNameExpression ||
      node.getLeft() instanceof ASTAgoQualification) &&
      node.getRight() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      if(node.getLeft() instanceof ASTNameExpression){
        expressionPP.handle((ASTNameExpression) node.getLeft(), true);
      }
      else {
        behaviorPP.handle((ASTAgoQualification) node.getLeft(), true);
      }
    }
    else if(tc.typeOf(node.getLeft()) instanceof SymTypeOfNumericWithSIUnit &&
            tc.typeOf(node.getRight()) instanceof SymTypeOfNumericWithSIUnit) {
      //SIUnit Types are used - conversion might be necessary
      node.getLeft().accept(getRealThis());
      getPrinter().print(infix);
      UnitConverter converter = getSIConverter(tc.typeOf(node.getLeft()),
        tc.typeOf(node.getRight()));
      getPrinter().print(factorStart(converter));
      node.getRight().accept(getRealThis());
      getPrinter().print(factorEnd(converter));
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
      return;
    }
    else if (isStringConcat) {
      getPrinter().print("concat(");
      node.getLeft().accept(getRealThis());
    }
    else {
      node.getLeft().accept(getRealThis());
    }
    if (isStringConcat) {
      getPrinter().print(", ");
    }
    else {
      getPrinter().print(infix);
    }
    if ((node.getRight() instanceof ASTNameExpression ||
      node.getRight() instanceof ASTAgoQualification) &&
      node.getLeft() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      if (node.getRight() instanceof ASTNameExpression) {
        expressionPP.handle((ASTNameExpression) node.getRight(), true);
      }
      else {
        behaviorPP.handle((ASTAgoQualification) node.getRight(), true);
      }
    }
    else {
      node.getRight().accept(getRealThis());
    }

    if (isStringConcat) {
      getPrinter().print(")");
    }

    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTFieldAccessExpression node){
    if (node.getExpression() instanceof ASTAgoQualification) {
      CommentPrettyPrinter.printPreComments(node, this.getPrinter());
      CppBehaviorPrettyPrinter behaviorPP = new CppBehaviorPrettyPrinter(getPrinter());
      behaviorPP.handle((ASTAgoQualification) node.getExpression(), true);
      this.getPrinter().print("." + node.getName());
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
    }
    else {
      super.handle(node);
    }
  }

  @Override
  public void handle(ASTCallExpression node) {
    if (node.getName().equals("behavior")) {
      if(node.getArguments().getExpression(0) instanceof ASTLiteralExpression){
        ASTLiteralExpression e = (ASTLiteralExpression) node.getArguments().getExpression(0);
        CommentPrettyPrinter.printPreComments(node, getPrinter());
        getPrinter().print("compute");
        String portNames = new MCCommonLiteralsPrettyPrinter(new IndentPrinter()).
                prettyprint((ASTMCCommonLiteralsNode) e.getLiteral());
        portNames = portNames.replace("\"", "");
        String[] portNameArray = portNames.split(",");
        for (String s : portNameArray) {
          getPrinter().print("__");
          s = s.replace(" ", "");
          getPrinter().print(StringTransformations.capitalize(s));
        }
        getPrinter().print("(input)");
        CommentPrettyPrinter.printPostComments(node, getPrinter());
      } else {
        super.handle(node);
      }
    } else {
      super.handle(node);
    }
  }
}
