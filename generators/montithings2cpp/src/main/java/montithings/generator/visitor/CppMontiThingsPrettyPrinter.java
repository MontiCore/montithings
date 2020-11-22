// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.setexpressions._ast.*;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._symboltable.IMontiArcScope;
import montithings._ast.ASTIsPresentExpression;
import montithings._visitor.MontiThingsPrettyPrinter;
import montithings._visitor.MontiThingsVisitor;
import montithings.generator.helper.ComponentHelper;
import net.sourceforge.plantuml.Log;
import portextensions._ast.ASTSyncStatement;
import setdefinitions._ast.ASTSetDefinition;
import setdefinitions._ast.ASTSetValueList;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsVisitor;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.generator.visitor.CppPrettyPrinterUtils.isSet;

/**
 * TODO
 *
 * @since 23.07.20
 */
public class CppMontiThingsPrettyPrinter extends MontiThingsPrettyPrinter {
  private MontiThingsVisitor realThis;

  public CppMontiThingsPrettyPrinter(IndentPrinter printer) {
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
    getPrinter().print("(");
    node.getLeft().accept(getRealThis());
    getPrinter().print(")");
    getPrinter().print(" || ");
    getPrinter().print("(");
    node.getRight().accept(getRealThis());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIntersectionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getLeft().accept(getRealThis());
    getPrinter().print(")");
    getPrinter().print(" && ");
    getPrinter().print("(");
    node.getRight().accept(getRealThis());
    getPrinter().print(")");
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
      getPrinter().print("((");
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

  @Override public void traverse(ASTSetValueRegEx node) {
    // do not visit format node
  }

  @Override
  public void handle(ASTSetDefinition node){
    for (int i = 0; i < node.sizeSetAllowedValuess(); i++) {
      getPrinter().print("(");
      node.getSetAllowedValues(i).accept(getRealThis());
      getPrinter().print(")");
      if (i < node.sizeSetAllowedValuess()-1) {
        getPrinter().print(" || ");
      }
    }
  }

  @Override
  public void handle(ASTIsPresentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<PortSymbol> port = getPortForName(node.getNameExpression());
    if (!port.isPresent()) {
      getRealThis().handle(node.getNameExpression());
      return;
    }

    ComponentTypeSymbol comp;
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    if (s.getSpanningSymbol() instanceof ComponentTypeSymbol) {
      comp = (ComponentTypeSymbol) s.getSpanningSymbol();
    }
    else {
      de.se_rwth.commons.logging.Log.error("ASTNameExpression " + node.getNameExpression().getName()
        + "has an unknown scope (neither statement nor automaton)");
      // throw useless exception to make compiler happy with accessing "comp" afterwards
      throw new IllegalArgumentException();
    }
    List<PortSymbol> portsInBatchStatement = ComponentHelper.getPortsInBatchStatement(comp);
    List<ASTSyncStatement> syncStatements = ComponentHelper
      .elementsOf(comp).filter(ASTSyncStatement.class).toList();

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = "input";
      }
      else {
        prefix = "result";
      }

      getPrinter().print(prefix + ".get" + capitalize(node.getNameExpression().getName()) + "()");

    }
    else if (!syncStatements.isEmpty()) {
      StringBuilder synced = new StringBuilder("(");
      for (ASTSyncStatement sync : syncStatements) {
        String s1 = sync
          .getSyncedPortList()
          .stream()
          .map(str -> "input.get" + capitalize(str) + "()" + isSet(portsInBatchStatement, str))
          .collect(Collectors.joining(" && "));
        synced.append(s1);
      }
      synced.append(")");
      getPrinter().print(synced.toString());

    }
    else {
      getPrinter().print(node.getNameExpression().getName());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  protected Optional<PortSymbol> getPortForName(ASTNameExpression node) {
    if (!(node.getEnclosingScope() instanceof IMontiArcScope)) {
      getPrinter().print(node.getName());
      return Optional.empty();
    }
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    String name = node.getName();
    return s.resolvePort(name);
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
