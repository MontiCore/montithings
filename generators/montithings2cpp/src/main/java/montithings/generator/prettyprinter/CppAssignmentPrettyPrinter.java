// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import javax.measure.converter.UnitConverter;
import java.util.List;
import java.util.Optional;

import static montithings.generator.prettyprinter.CppPrettyPrinterUtils.*;
import static montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter.*;
import static montithings.util.IdentifierUtils.getPortForName;

public class CppAssignmentPrettyPrinter extends AssignmentExpressionsPrettyPrinter {

  protected TypeCheck tc;

  // In some code parts postconditions should not be checked, e.g. within the
  // catch condition of a postcondition
  protected boolean suppressPostconditionCheck = false;

  // When log tracing is enabled the exchanged messages are wrapped into a Pair type which holds the corresponding ID
  // Therefore, the argument of setNextValue() has to be adapted accordingly
  protected boolean isLogTracingEnabled = false;

  public CppAssignmentPrettyPrinter(IndentPrinter printer, boolean isLogTracingEnabled, boolean suppressPostconditionCheck) {
    super(printer);
    tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(),
      new DeriveSymTypeOfMontiThingsCombine());
    this.suppressPostconditionCheck = suppressPostconditionCheck;
    this.isLogTracingEnabled = isLogTracingEnabled;
  }

  @Override public void handle(ASTIncSuffixExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      handleIncDec(node.getExpression(), "post", "+1");
    }
    else {
      super.handle(node);
    }
  }

  @Override public void handle(ASTDecSuffixExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      handleIncDec(node.getExpression(), "post", "-1");
    }
    else {
      super.handle(node);
    }
  }

  @Override public void handle(ASTIncPrefixExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      handleIncDec(node.getExpression(), "pre", "+1");
    }
    else {
      super.handle(node);
    }
  }

  @Override public void handle(ASTDecPrefixExpression node) {
    if (node.getExpression() instanceof ASTNameExpression) {
      handleIncDec(node.getExpression(), "pre", "-1");
    }
    else {
      super.handle(node);
    }
  }

  protected void handleIncDec(ASTExpression node, String preOrPost, String operation) {
    if (isStateVariable(node)) {
      ASTNameExpression nameExpr = (ASTNameExpression) node;
      getPrinter().print(Identifier.getStateName() + ".");
      getPrinter().print(preOrPost + "Set" + capitalize(nameExpr.getName()) + "(");
      getPrinter().print(Identifier.getStateName() + ".");
      getPrinter().print("get" + capitalize(nameExpr.getName()) + "()");
      getPrinter().print(operation);
      getPrinter().print(")");
    } else if (node instanceof ASTNameExpression) {
      ASTNameExpression nameExpr = (ASTNameExpression) node;
      boolean isPre = preOrPost.equals("pre");
      String op = operation.equals("+1") ? "++" : "--";
      getPrinter().print((isPre ? op : "") + nameExpr.getName() + (!isPre ? op : ""));
    }
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

    if (port.isPresent() || isStateVariable(nameExpression)) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());

      getPrinter().print("{");

      String prefix;
      if (isStateVariable(nameExpression)) {
        prefix = Identifier.getStateName();
      }
      else if (port.get().isIncoming()) {
        prefix = Identifier.getInputName();
      }
      else {
        prefix = Identifier.getResultName();
      }

      getPrinter().print(prefix + ".set" + capitalize(nameExpression.getName()) + "( ");

      if (node.getOperator() != ASTConstantsAssignmentExpressions.EQUALS) {
        node.getLeft().accept(getTraverser());
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

      Optional<SymTypeExpression> leftType = Optional.empty();
      Optional<SymTypeExpression> rightType = Optional.empty();

      if (port.isPresent() && port.get().getType() instanceof SymTypeOfNumericWithSIUnit) {
        leftType = Optional.of(port.get().getType());
        rightType = Optional.of(tc.typeOf(node.getRight()));
      }

      if (isStateVariable(nameExpression)
        && tc.typeOf(node.getLeft()) instanceof SymTypeOfNumericWithSIUnit
        && tc.typeOf(node.getRight()) instanceof SymTypeOfNumericWithSIUnit) {
        leftType = Optional.of(tc.typeOf(node.getLeft()));
        rightType = Optional.of(tc.typeOf(node.getRight()));
      }

      if (leftType.isPresent() && rightType.isPresent()) {
        UnitConverter converter = getSIConverter(leftType.get(), rightType.get());
        getPrinter().print(factorStart(converter));
        node.getRight().accept(getTraverser());
        getPrinter().print(factorEnd(converter));
      }

      else {
        node.getRight().accept(getTraverser());
      }
      getPrinter().println(" );");

      if (isLogTracingEnabled && !suppressPostconditionCheck) {
        IMontiThingsScope componentScope = getScopeOfEnclosingComponent(node);
        ComponentTypeSymbol component = (ComponentTypeSymbol) componentScope.getSpanningSymbol();
        List<VariableSymbol> stateVariables = ComponentHelper.getVariablesAndParameters(component);

        for (VariableSymbol stateVariable : stateVariables) {
          getPrinter().print("component.getLogTracer()->handleVariableStateChange(\"");
          getPrinter().print(stateVariable.getName() + "\",");
          getPrinter().print(Identifier.getStateName() + ".");
          getPrinter().print("get" + capitalize(stateVariable.getName()) + "());");
        }
      }

      if (port.isPresent() && port.get().isOutgoing() && !suppressPostconditionCheck) {
        // check postconditions and send value
        String portname = capitalize(nameExpression.getName());
        getPrinter()
          .println("component.checkPostconditions(" + Identifier.getInputName()
            + ", " + Identifier.getResultName() + ", " + Identifier.getStateName() + ", state__at__pre);");
        getPrinter().print(
          "interface.getPort" + portname + "()->setNextValue(" + Identifier.getResultName() +
            ".get" + portname + "Message(" + (isLogTracingEnabled ? "component.getLogTracer()->getCurrOutputUuid()" : "" ) + "));");
      }
      getPrinter().print("}");
    }
    else {
      if (tc.typeOf(node.getLeft()) instanceof SymTypeOfNumericWithSIUnit &&
        tc.typeOf(node.getRight()) instanceof SymTypeOfNumericWithSIUnit) {
        CommentPrettyPrinter.printPreComments(node, this.getPrinter());
        node.getLeft().accept(getTraverser());
        switch (node.getOperator()) {
          case ASTConstantsAssignmentExpressions.ANDEQUALS:
            this.getPrinter().print("&=");
            break;
          case ASTConstantsAssignmentExpressions.EQUALS:
            this.getPrinter().print("=");
            break;
          case ASTConstantsAssignmentExpressions.GTGTEQUALS:
            this.getPrinter().print(">>=");
            break;
          case ASTConstantsAssignmentExpressions.GTGTGTEQUALS:
            this.getPrinter().print(">>>=");
            break;
          case ASTConstantsAssignmentExpressions.LTLTEQUALS:
            this.getPrinter().print("<<=");
            break;
          case ASTConstantsAssignmentExpressions.MINUSEQUALS:
            this.getPrinter().print("-=");
            break;
          case ASTConstantsAssignmentExpressions.PERCENTEQUALS:
            this.getPrinter().print("%=");
            break;
          case ASTConstantsAssignmentExpressions.PIPEEQUALS:
            this.getPrinter().print("|=");
            break;
          case ASTConstantsAssignmentExpressions.PLUSEQUALS:
            this.getPrinter().print("+=");
            break;
          case ASTConstantsAssignmentExpressions.ROOFEQUALS:
            this.getPrinter().print("^=");
            break;
          case ASTConstantsAssignmentExpressions.SLASHEQUALS:
            this.getPrinter().print("/=");
            break;
          case ASTConstantsAssignmentExpressions.STAREQUALS:
            this.getPrinter().print("*=");
            break;
          default:
            Log.error("0xA0114 Missing implementation for RegularAssignmentExpression");
        }

        UnitConverter converter = getSIConverter(tc.typeOf(node.getLeft()),
          tc.typeOf(node.getRight()));
        getPrinter().print(factorStartSimple(converter));
        node.getRight().accept(getTraverser());
        getPrinter().print(factorEndSimple(converter));
        CommentPrettyPrinter.printPostComments(node, this.getPrinter());
      }
      else {
        super.handle(node);
      }
    }

    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
