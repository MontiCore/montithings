// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montithings.generator.helper.TypesPrinter;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import setdefinitions._ast.*;
import setdefinitions._visitor.SetDefinitionsPrettyPrinter;

import java.util.Stack;


public class CppSetDefinitionsPrettyPrinter extends SetDefinitionsPrettyPrinter {

  MontiThingsTypeCheck tc = new MontiThingsTypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());

  public CppSetDefinitionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSetValueRegEx node) {
    getPrinter().print("(");
    getPrinter().print("std::regex_match(");
    getPrinter().print("((std::ostringstream&)(std::ostringstream(\"\") << ");
    CppSetExpressionsPrettyPrinter.getExpressions().peek().accept(getTraverser());
    getPrinter().print(")).str(), ");

    getPrinter().print("std::regex(");
    node.getFormat().accept(getTraverser());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTSetValueRange node) {
    getPrinter().print("(");
    CppSetExpressionsPrettyPrinter.getExpressions().peek().accept(getTraverser());
    getPrinter().print(" >= ");
    node.getLowerBound().accept(getTraverser());
    getPrinter().print(" && ");

    if (node.isPresentStepsize()) {
      getPrinter().print("((");
      CppSetExpressionsPrettyPrinter.getExpressions().peek().accept(getTraverser());
      getPrinter().print(" - ");
      node.getLowerBound().accept(getTraverser());
      getPrinter().print(")");
      getPrinter().print(" % ");
      node.getStepsize().accept(getTraverser());
      getPrinter().print(" == 0 ");
      getPrinter().print(") && ");
    }
    CppSetExpressionsPrettyPrinter.getExpressions().peek().accept(getTraverser());
    getPrinter().print(" <= ");
    node.getUpperBound().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTListExpression node) {
    getPrinter().print("[=](){");
    getPrinter().print("std::list<" );
    SymTypeExpression type;
    if (node.isEmptyExpressions()) {
      type = SymTypeExpressionFactory.createTypeConstant("Object");
    }
    else {
      type = tc.typeOf(node.getExpression(0));
    }
    getPrinter().print(TypesPrinter.printCPPTypeName(type));
    getPrinter().print( "> __list__init ({");
    acceptSeperatedList(node.getExpressionList());
    getPrinter().print("});");
    getPrinter().print("collections::list<");
    getPrinter().print(TypesPrinter.printCPPTypeName(type));
    getPrinter().print("> __list__init__2 (__list__init);");
    getPrinter().print("return __list__init__2;}()");
  }

  @Override
  public void handle(ASTMapExpression node) {
    SymTypeExpression keyType, valueType;
    if (node.isEmptyKeyValuePairs()) {
      keyType = SymTypeExpressionFactory.createTypeConstant("Object");
      valueType = SymTypeExpressionFactory.createTypeConstant("Object");
    }
    else {
      keyType = tc.typeOf(node.getKeyValuePair(0).getKey());
      valueType = tc.typeOf(node.getKeyValuePair(0).getValue());
    }
    getPrinter().print("[=](){");
    getPrinter().print("collections::map");
    getPrinter().print("<" + TypesPrinter.printCPPTypeName(keyType) + ", ");
    getPrinter().print(TypesPrinter.printCPPTypeName(valueType) + ">");
    getPrinter().print(" __map__init (std::map");
    getPrinter().print("<" + TypesPrinter.printCPPTypeName(keyType) + ", ");
    getPrinter().print(TypesPrinter.printCPPTypeName(valueType) + ">");
    getPrinter().print("({");
    acceptSeperatedSetList(node.getKeyValuePairList());
    getPrinter().print("}));");
    getPrinter().print("return __map__init;}()");
  }

  @Override
  public void handle(ASTKeyValuePair node) {
    getPrinter().print("{");
    node.getKey().accept(getTraverser());
    getPrinter().print(",");
    node.getValue().accept(getTraverser());
    getPrinter().print("}");
  }
}
