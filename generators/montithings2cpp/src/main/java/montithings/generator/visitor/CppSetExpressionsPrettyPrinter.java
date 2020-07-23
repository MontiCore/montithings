// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.setexpressions._ast.*;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montithings._visitor.MontiThingsPrettyPrinter;
import montithings._visitor.MontiThingsVisitor;
import net.sourceforge.plantuml.Log;
import setdefinitions._ast.ASTSetDefinition;
import setdefinitions._ast.ASTSetValueList;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsVisitor;

import java.util.Stack;

/**
 * TODO
 *
 * @since 23.07.20
 */
public class CppSetExpressionsPrettyPrinter extends MontiThingsPrettyPrinter {
  private MontiThingsVisitor realThis;

  public CppSetExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  Stack<ASTExpression> expressions = new Stack<>();

  @Override
  public void handle(ASTIsInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    expressions.push(node.getElem());
    node.getSet().accept(getRealThis());
    expressions.pop();
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(montithings._ast.ASTSetInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    expressions.push(node.getElem());
    node.getSet().accept(getRealThis());
    expressions.pop();
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSetInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    expressions.push(node.getElem());
    node.getSet().accept(getRealThis());
    expressions.pop();
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTUnionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" || ");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIntersectionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" && ");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSetAndExpression node) {
    Log.error("0xMT820 SetAndExpression is not supported.");
  }

  @Override
  public void handle(ASTSetOrExpression node) {
    Log.error("0xMT821 SetOrExpression is not supported.");
  }

  @Override
  public void handle(ASTUnionExpressionPrefix node) {
    Log.error("0xMT822 UnionExpression is not supported.");
  }

  @Override
  public void handle(ASTIntersectionExpressionPrefix node) {
    Log.error("0xMT823 IntersectionExpression is not supported.");
  }

  @Override
  public void handle(ASTSetValueList node){
    for (int i = 0; i < node.sizeExpressions(); i++) {
      getPrinter().print("(");
      expressions.peek().accept(getRealThis());
      getPrinter().print(" == ");
      node.getExpression(i).accept(getRealThis());
      getPrinter().print(")");

      if (i < node.sizeExpressions()-1) {
        getPrinter().print(" || ");
      }
    }
  }

  @Override
  public void handle(ASTSetValueRange node){
    getPrinter().print("(");
		expressions.peek().accept(getRealThis());
    getPrinter().print(" >= ");
    node.getLowerBound().accept(getRealThis());
    getPrinter().print(" && ");

		if (node.isPresentStepsize()) {
      getPrinter().print("(");
      expressions.peek().accept(getRealThis());
      getPrinter().print(" - ");
      node.getLowerBound().accept(getRealThis());
      getPrinter().print(")");
      getPrinter().print(" % ");
      node.getStepsize().accept(getRealThis());
      getPrinter().print(" == 0 ");
      getPrinter().print(") && ");
    }
    expressions.peek().accept(getRealThis());
    getPrinter().print(" <= ");
    node.getUpperBound().accept(getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void visit(ASTSetValueRegEx node){
    getPrinter().print("(");
    getPrinter().print("std::regex_match(");
    getPrinter().print("((std::ostringstream&)(std::ostringstream(\"\") << ");
    expressions.peek().accept(getRealThis());
    getPrinter().print(")).str(), ");

    getPrinter().print("std::regex(");
    node.getFormat().accept(getRealThis());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTSetDefinition node){
    for (int i = 0; i < node.sizeSetAllowedValuess(); i++) {
      node.getSetAllowedValues(i).accept(getRealThis());
      if (i < node.sizeSetAllowedValuess()-1) {
        getPrinter().print(" && ");
      }
    }
  }

  @Override public MontiThingsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MontiThingsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void setRealThis(SetDefinitionsVisitor realThis) {
    this.realThis = (MontiThingsVisitor) realThis;
  }

  @Override
  public void setRealThis(SetExpressionsVisitor realThis) {
    this.realThis = (MontiThingsVisitor) realThis;
  }
}
