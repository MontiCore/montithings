// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.ocl.setexpressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montithings._auxiliary.SetExpressionsMillForMontiThings;
import montithings.generator.helper.TypesPrinter;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import java.util.Stack;


public class CppSetExpressionsPrettyPrinter extends SetExpressionsPrettyPrinter {

  protected static Stack<ASTExpression> expressions = new Stack<>();

  MontiThingsTypeCheck tc = new MontiThingsTypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());


  public CppSetExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSetInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    expressions.push(node.getElem());
    node.getSet().accept(getTraverser());
    expressions.pop();
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTUnionExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(")");
    getPrinter().print(" || ");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIntersectionExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(")");
    getPrinter().print(" && ");
    getPrinter().print("(");
    node.getRight().accept(getTraverser());
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
      expressions.peek().accept(getTraverser());
      getPrinter().print(" == ");
      node.getExpression(i).accept(getTraverser());
      getPrinter().print(")");

      if (i < node.sizeExpressions() - 1) {
        getPrinter().print(" || ");
      }
    }
  }

  @Override
  public void handle(ASTSetEnumeration node) {
    if (expressions.size() > 1) {
      Log.error("SetEnumeration was used in an AssignmentExpression and LetInExpression at the same time");
    }
    else if (expressions.size() > 0 && expressions.peek() instanceof ASTAssignmentExpression) {
      handleSetEnumerationInAssignment(node);
    }
    else {
      for (int i = 0; i < node.sizeSetCollectionItems(); i++) {
        getPrinter().print("(");
        node.getSetCollectionItem(i).accept(getTraverser());
        getPrinter().print(")");
        if (i < node.sizeSetCollectionItems() - 1) {
          getPrinter().print(" || ");
        }
      }
    }
  }

  protected void handleSetEnumerationInAssignment(ASTSetEnumeration node) {
    getPrinter().print("[=](){");
    getPrinter().print("std::set<" );
    SymTypeExpression type;
    if (node.isEmptySetCollectionItems()) {
      type = SymTypeExpressionFactory.createTypeConstant("Object");
    }
    else {
      type = SymTypeExpressionFactory.createTypeConstant("Object");
    }
    getPrinter().print(TypesPrinter.printCPPTypeName(type));
    getPrinter().print( "> __set__init ({");
    getPrinter().print("});");
    getPrinter().print("collections::set<");
    getPrinter().print(TypesPrinter.printCPPTypeName(type));
    getPrinter().print("> __set__init__2 (__set__init);");
    getPrinter().print("return __set__init__2;}()");
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
        expr.accept(getTraverser());
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
      setComprehension.getSetComprehensionItem(i).getExpression().accept(getTraverser());
      getPrinter().print(")");

      if (i != setComprehension.sizeSetComprehensionItems() - 1) {
        getPrinter().print("&&");
      }
    }
    getPrinter().print(";");
    getPrinter().print("}(");
    expressions.peek().accept(getTraverser());
    getPrinter().print(")");
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public static Stack<ASTExpression> getExpressions() {
    return expressions;
  }

  public static void setExpressions(Stack<ASTExpression> expressions) {
    expressions = expressions;
  }
}
