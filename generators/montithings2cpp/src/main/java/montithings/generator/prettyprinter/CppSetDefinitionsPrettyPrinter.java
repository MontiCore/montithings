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
import setdefinitions._ast.ASTListExpression;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsPrettyPrinter;

import java.util.Stack;


public class CppSetDefinitionsPrettyPrinter extends SetDefinitionsPrettyPrinter {

  Stack<ASTExpression> expressions;

  MontiThingsTypeCheck tc = new MontiThingsTypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());

  public CppSetDefinitionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSetValueRegEx node) {
    getPrinter().print("(");
    getPrinter().print("std::regex_match(");
    getPrinter().print("((std::ostringstream&)(std::ostringstream(\"\") << ");
    expressions.peek().accept(getTraverser());
    getPrinter().print(")).str(), ");

    getPrinter().print("std::regex(");
    node.getFormat().accept(getTraverser());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTSetValueRange node) {
    getPrinter().print("(");
    expressions.peek().accept(getTraverser());
    getPrinter().print(" >= ");
    node.getLowerBound().accept(getTraverser());
    getPrinter().print(" && ");

    if (node.isPresentStepsize()) {
      getPrinter().print("((");
      expressions.peek().accept(getTraverser());
      getPrinter().print(" - ");
      node.getLowerBound().accept(getTraverser());
      getPrinter().print(")");
      getPrinter().print(" % ");
      node.getStepsize().accept(getTraverser());
      getPrinter().print(" == 0 ");
      getPrinter().print(") && ");
    }
    expressions.peek().accept(getTraverser());
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
    TypesPrinter.printCPPTypeName(type);
    getPrinter().print( "> __list__init (");
    acceptSeperatedList(node.getExpressionList());
    getPrinter().print(");");
    getPrinter().print("collections::list<");
    TypesPrinter.printCPPTypeName(type);
    getPrinter().print("> __list__init__2 (__list__init);");
    getPrinter().print("return collections::list(__list__init);}()");
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Stack<ASTExpression> getExpressions() {
    return expressions;
  }

  public void setExpressions(
    Stack<ASTExpression> expressions) {
    this.expressions = expressions;
  }
}
