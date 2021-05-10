// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunits._ast.ASTSIUnit;
import de.monticore.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunittypes4computing._visitor.SIUnitTypes4ComputingVisitor;
import de.monticore.siunittypes4math._visitor.SIUnitTypes4MathVisitor;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTIsPresentExpression;
import montithings._ast.ASTPublishPort;
import montithings._auxiliary.SetExpressionsMillForMontiThings;
import montithings._symboltable.IMontiThingsScope;
import montithings._visitor.MontiThingsPrettyPrinter;
import montithings._visitor.MontiThingsVisitor;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import portextensions._ast.ASTSyncStatement;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsVisitor;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import static montithings.generator.visitor.CppPrettyPrinterUtils.*;
import static montithings.util.IdentifierUtils.getPortForName;

public class CppMontiThingsPrettyPrinter extends MontiThingsPrettyPrinter {
  protected MontiThingsVisitor realThis;

  public CppMontiThingsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  Stack<ASTExpression> expressions = new Stack<>();

  @Override public void handle(ASTPublishPort node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    for (String port : node.getPublishedPortsList()) {
      getPrinter().print("port" + capitalize(port) +
        "->setNextValue(" + Identifier.getResultName() +
        ".get" + capitalize(port) + "());");
    }
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
  public void handle(ASTUnionExpression node) {
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
  public void handle(ASTIntersectionExpression node) {
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
  public void handle(ASTSetUnionExpression node) {
    Log.error("0xMT822 SetUnionExpression is not supported.");
  }

  @Override
  public void handle(ASTSetIntersectionExpression node) {
    Log.error("0xMT823 SetIntersectionExpression is not supported.");
  }

  @Override
  public void handle(ASTSetValueItem node) {
    for (int i = 0; i < node.sizeExpressions(); i++) {
      getPrinter().print("(");
      expressions.peek().accept(getRealThis());
      getPrinter().print(" == ");
      node.getExpression(i).accept(getRealThis());
      getPrinter().print(")");

      if (i < node.sizeExpressions() - 1) {
        getPrinter().print(" || ");
      }
    }
  }

  @Override
  public void handle(ASTSetValueRange node) {
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
  public void visit(ASTSetValueRegEx node) {
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
  public void handle(ASTSetEnumeration node) {
    for (int i = 0; i < node.sizeSetCollectionItems(); i++) {
      getPrinter().print("(");
      node.getSetCollectionItem(i).accept(getRealThis());
      getPrinter().print(")");
      if (i < node.sizeSetCollectionItems() - 1) {
        getPrinter().print(" || ");
      }
    }
  }

  @Override
  public void handle(ASTSetComprehension node) {
    if (!expressions.isEmpty()) {
      //Called from SetInExpression
      if (node.getLeft().isPresentSetVariableDeclaration()) {
        //check expressions on the right side, if they are the only restrictions,
        //this method only gets called after handling SetInExpressions
        printSetComprehensionExpressionsForSetInExpression(node,
          node.getLeft().getSetVariableDeclaration().getSymbol());
      }
      else if (node.getLeft().isPresentGeneratorDeclaration()) {
        //check expressions and check that element is in set of GeneratorDeclaration
        printSetComprehensionExpressionsForSetInExpression(node,
          node.getLeft().getGeneratorDeclaration().getSymbol());
        getPrinter().print(" && ");
        getPrinter().print("(");
        ASTSetInExpression expr = SetExpressionsMillForMontiThings.setInExpressionBuilder()
          .setOperator("isin")
          .setElem(expressions.peek())
          .setSet(node.getLeft().getGeneratorDeclaration().getExpression()).build();
        expr.accept(getRealThis());
        getPrinter().print(")");
      }
      else {
        Log.error("Expressions at the left side of SetComprehensions are not supported");
      }
    }
  }

  protected void printSetComprehensionExpressionsForSetInExpression(
    ASTSetComprehension setComprehension, VariableSymbol symbol) {
    String varName = symbol.getName();
    String varType = symbol.getType().getTypeInfo().getName();
    getPrinter().print("[&] (" + varType + " " + varName + ") { return ");

    for (int i = 0; i < setComprehension.sizeSetComprehensionItems(); i++) {
      if (!(setComprehension.getSetComprehensionItem(i).isPresentExpression())) {
        Log.error("Only expressions are supported at the right side of set comprehensions");
      }
      getPrinter().print("(");
      setComprehension.getSetComprehensionItem(i).getExpression().accept(getRealThis());
      getPrinter().print(")");

      if (i != setComprehension.sizeSetComprehensionItems() - 1) {
        getPrinter().print("&&");
      }
    }
    getPrinter().print(";");
    getPrinter().print("}(");
    expressions.peek().accept(getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTIsPresentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<PortSymbol> port = getPortForName(node.getNameExpression());
    if (!port.isPresent()) {
      getRealThis().handle(node.getNameExpression());
      return;
    }

    IMontiThingsScope componentScope = getScopeOfEnclosingComponent(node);
    ComponentTypeSymbol comp = (ComponentTypeSymbol) componentScope.getSpanningSymbol();

    List<PortSymbol> portsInBatchStatement = ComponentHelper.getPortsInBatchStatement(comp);

    List<ASTSyncStatement> syncStatements = ComponentHelper
      .elementsOf(comp).filter(ASTSyncStatement.class).toList();

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = Identifier.getInputName();
      }
      else {
        prefix = Identifier.getResultName();
      }

      getPrinter().print(prefix + ".get" + capitalize(node.getNameExpression().getName()) + "()");

    }
    else if (!syncStatements.isEmpty()) {
      StringBuilder synced = new StringBuilder("(");
      for (ASTSyncStatement sync : syncStatements) {
        String s1 = sync
          .getSyncedPortList()
          .stream()
          .map(str -> Identifier.getInputName() + ".get" + capitalize(str) + "()" + isSet(
            portsInBatchStatement, str))
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

  @Override
  public void handle(ASTSIUnitType4Computing node) {
    node.getMCPrimitiveType().accept(getRealThis());
  }

  @Override
  public void handle(ASTSIUnit node) {
    getPrinter().print("double");
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

  @Override
  public void setRealThis(SIUnitTypes4ComputingVisitor realThis) {
    this.realThis = (MontiThingsVisitor) realThis;
  }

  @Override
  public void setRealThis(SIUnitTypes4MathVisitor realThis) {
    this.realThis = (MontiThingsVisitor) realThis;
  }
}
