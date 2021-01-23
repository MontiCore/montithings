// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;
import montithings.generator.codegen.util.Identifier;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import javax.measure.converter.UnitConverter;
import java.util.Optional;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter.*;

public class CppAssignmentPrettyPrinter extends AssignmentExpressionsPrettyPrinter {

  private TypeCheck tc;

  public CppAssignmentPrettyPrinter(IndentPrinter printer) {
    super(printer);
    tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    this.realThis = this;
  }

  @Override
  public void handle(ASTAssignmentExpression node) {

    ASTNameExpression nameExpression;
    if (node.getLeft() instanceof ASTNameExpression) {
      nameExpression = (ASTNameExpression) node.getLeft();
    }
    else {
      super.handle(node);
      return;
    }
    Optional<PortSymbol> port = getPortForName(nameExpression);

    if (port.isPresent()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());

      String prefix;
      if (port.get().isIncoming()) {
        prefix = Identifier.getInputName();
      }
      else {
        prefix = Identifier.getResultName();
      }

      getPrinter().print(prefix + ".set" + capitalize(nameExpression.getName()) + "( ");

      if (node.getOperator() != ASTConstantsAssignmentExpressions.EQUALS) {
        node.getLeft().accept(getRealThis());
      }

      switch (node.getOperator()) {
        case ASTConstantsAssignmentExpressions.EQUALS:
          break;
        case ASTConstantsAssignmentExpressions.PLUSEQUALS:
          getPrinter().print(("+"));
          break;
        case ASTConstantsAssignmentExpressions.MINUSEQUALS:
          getPrinter().print(("-"));
          break;
        case ASTConstantsAssignmentExpressions.STAREQUALS:
          getPrinter().print(("*"));
          break;
        case ASTConstantsAssignmentExpressions.SLASHEQUALS:
          getPrinter().print(("/"));
          break;
        case ASTConstantsAssignmentExpressions.ANDEQUALS:
          getPrinter().print(("&"));
          break;
        case ASTConstantsAssignmentExpressions.PIPEEQUALS:
          getPrinter().print(("|"));
          break;
        case ASTConstantsAssignmentExpressions.ROOFEQUALS:
          getPrinter().print(("^"));
          break;
        case ASTConstantsAssignmentExpressions.GTGTEQUALS:
          getPrinter().print((">>"));
          break;
        case ASTConstantsAssignmentExpressions.GTGTGTEQUALS:
          getPrinter().print((">>>"));
          break;
        case ASTConstantsAssignmentExpressions.LTLTEQUALS:
          getPrinter().print(("<<"));
          break;
        case ASTConstantsAssignmentExpressions.PERCENTEQUALS:
          getPrinter().print(("%"));
          break;
        default:
          Log.error("0xMT814 Missing implementation for RegularAssignmentExpression");
      }

      if (port.get().getType() instanceof SymTypeOfNumericWithSIUnit){
        SymTypeExpression exprType = tc.typeOf(node.getRight());
        if(exprType instanceof SymTypeOfNumericWithSIUnit){
          UnitConverter converter = getSIConverter(port.get().getType(), exprType);
          getPrinter().print(factorStart(converter));
          node.getRight().accept(getRealThis());
          getPrinter().print(factorEnd(converter));
        }
      }
      else {
        node.getRight().accept(getRealThis());
      }
      getPrinter().print(" )");
    }
    else {
      if(tc.typeOf(node.getLeft()) instanceof SymTypeOfNumericWithSIUnit &&
              tc.typeOf(node.getRight()) instanceof SymTypeOfNumericWithSIUnit){
        CommentPrettyPrinter.printPreComments(node, this.getPrinter());
        node.getLeft().accept(this.getRealThis());
        switch(node.getOperator()) {
          case 1:
            this.getPrinter().print("&=");
            break;
          case 2:
            this.getPrinter().print("=");
            break;
          case 3:
            this.getPrinter().print(">>=");
            break;
          case 4:
            this.getPrinter().print(">>>=");
            break;
          case 5:
            this.getPrinter().print("<<=");
            break;
          case 6:
            this.getPrinter().print("-=");
            break;
          case 7:
            this.getPrinter().print("%=");
            break;
          case 8:
            this.getPrinter().print("|=");
            break;
          case 9:
            this.getPrinter().print("+=");
            break;
          case 10:
            this.getPrinter().print("^=");
            break;
          case 11:
            this.getPrinter().print("/=");
            break;
          case 12:
            this.getPrinter().print("*=");
            break;
          default:
            Log.error("0xA0114 Missing implementation for RegularAssignmentExpression");
        }

        UnitConverter converter = getSIConverter(tc.typeOf(node.getLeft()), tc.typeOf(node.getRight()));
        getPrinter().print(factorStartSimple(converter));
        node.getRight().accept(getRealThis());
        getPrinter().print(factorEndSimple(converter));
        CommentPrettyPrinter.printPostComments(node, this.getPrinter());
      }
      else {
        super.handle(node);
      }
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
}
