// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import javax.measure.converter.UnitConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static montithings.generator.prettyprinter.CppPrettyPrinterUtils.*;
import static montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter.*;
import static montithings.util.IdentifierUtils.getPortForName;

public class CppAssignmentPrettyPrinter extends AssignmentExpressionsPrettyPrinter {

  protected TypeCheck tc;

  // In some code parts postconditions should not be checked, e.g. within the
  // catch condition of a postcondition
  protected boolean suppressPostconditionCheck;

  // When log tracing is enabled the exchanged messages are wrapped into a Pair type which holds the corresponding ID
  // Therefore, the argument of setNextValue() has to be adapted accordingly
  protected boolean isLogTracingEnabled;

  public CppAssignmentPrettyPrinter(IndentPrinter printer, boolean isLogTracingEnabled,
    boolean suppressPostconditionCheck) {
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
    }
    else if (node instanceof ASTNameExpression) {
      ASTNameExpression nameExpr = (ASTNameExpression) node;
      boolean isPre = preOrPost.equals("pre");
      String op = operation.equals("+1") ? "++" : "--";
      getPrinter().print((isPre ? op : "") + nameExpr.getName() + (!isPre ? op : ""));
    }
  }

  @Override
  public void handle(ASTAssignmentExpression node) {
    CppSetExpressionsPrettyPrinter.getExpressions().push(node);
    if (node.getLeft() instanceof ASTNameExpression) {
      ASTNameExpression nameExpression = (ASTNameExpression) node.getLeft();
      if (isPort(nameExpression) || isStateVariable(nameExpression)) {
        handlePortAndVariableAssignments(node, nameExpression);
      }
    }
    else if (assignmentUsesSiUnits(node, tc)) {
      handleSiUnitAssignment(node);
    }
    else {
      super.handle(node);
    }
    CppSetExpressionsPrettyPrinter.getExpressions().pop();
  }

  protected void handlePortAndVariableAssignments(ASTAssignmentExpression node,
    ASTNameExpression nameExpression) {
    Optional<PortSymbol> port = getPortForName(nameExpression);
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("{");
    getPrinter().print(getNameExpressionPrefix(nameExpression));
    getPrinter().print(".set" + capitalize(nameExpression.getName()) + "( ");
    printNewValue(node);
    getPrinter().println(" );");
    informLogTracerAboutVariableChange(node);
    if (port.isPresent() && port.get().isOutgoing() && !suppressPostconditionCheck) {
      printPostconditions(nameExpression);
    }
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  protected void printPostconditions(ASTNameExpression nameExpression) {
    // check postconditions and send value
    String portname = capitalize(nameExpression.getName());
    getPrinter()
      .println("component.checkPostconditions(" + Identifier.getInputName()
        + ", " + Identifier.getResultName() + ", " + Identifier.getStateName()
        + ", state__at__pre);");
    getPrinter().print(
      "interface.getPort" + portname + "()->setNextValue(" + Identifier.getResultName() +
        ".get" + portname + "Message(" + (isLogTracingEnabled ?
        "component.getLogTracer()->getCurrOutputUuid()" :
        "") + "));");
  }

  protected void informLogTracerAboutVariableChange(ASTAssignmentExpression node) {
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
  }

  protected void printNewValue(ASTAssignmentExpression node) {
    Preconditions.checkArgument(node.getLeft() instanceof ASTNameExpression);
    ASTNameExpression nameExpression = (ASTNameExpression) node.getLeft();
    Optional<PortSymbol> port = getPortForName(nameExpression);

    if (node.getOperator() != ASTConstantsAssignmentExpressions.EQUALS) {
      node.getLeft().accept(getTraverser());
    }
    handleOperator(node.getOperator(), false);

    if ((port.isPresent() && usesSiUnit(port.get())) || assignmentUsesSiUnits(node, tc)) {
      printSiUnitConversion(node, nameExpression);
    }
    else {
      node.getRight().accept(getTraverser());
    }
  }

  private void printSiUnitConversion(ASTAssignmentExpression node,
    ASTNameExpression nameExpression) {
    Optional<SymTypeExpression> leftType = Optional.empty();
    Optional<SymTypeExpression> rightType = Optional.of(tc.typeOf(node.getRight()));

    Optional<PortSymbol> port = getPortForName(nameExpression);
    if (port.isPresent() && usesSiUnit(port.get())) {
      leftType = Optional.of(port.get().getType());
    }

    if (isStateVariable(nameExpression) && assignmentUsesSiUnits(node, tc)) {
      leftType = Optional.of(tc.typeOf(node.getLeft()));
    }

    if (leftType.isPresent()) {
      UnitConverter converter = getSIConverter(leftType.get(), rightType.get());
      getPrinter().print(factorStart(converter));
      node.getRight().accept(getTraverser());
      getPrinter().print(factorEnd(converter));
    }
  }

  protected String getNameExpressionPrefix(ASTNameExpression nameExpression) {
    String prefix = "";
    Optional<PortSymbol> port = getPortForName(nameExpression);
    if (isStateVariable(nameExpression)) {
      prefix = Identifier.getStateName();
    }
    else if (port.isPresent() && port.get().isIncoming()) {
      prefix = Identifier.getInputName();
    }
    else if (port.isPresent() && port.get().isOutgoing()) {
      prefix = Identifier.getResultName();
    }
    return prefix;
  }

  protected void handleSiUnitAssignment(ASTAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, this.getPrinter());
    node.getLeft().accept(getTraverser());
    handleOperator(node.getOperator(), true);
    UnitConverter converter = getSIConverter(tc.typeOf(node.getLeft()),
      tc.typeOf(node.getRight()));
    getPrinter().print(factorStartSimple(converter));
    node.getRight().accept(getTraverser());
    getPrinter().print(factorEndSimple(converter));
    CommentPrettyPrinter.printPostComments(node, this.getPrinter());
  }

  protected void handleOperator(int operator, boolean printEqualsSign) {
    Map<Integer, String> operators = new HashMap<Integer, String>() {{
      put(ASTConstantsAssignmentExpressions.EQUALS, "");
      put(ASTConstantsAssignmentExpressions.PLUSEQUALS, "+");
      put(ASTConstantsAssignmentExpressions.MINUSEQUALS, "-");
      put(ASTConstantsAssignmentExpressions.STAREQUALS, "*");
      put(ASTConstantsAssignmentExpressions.SLASHEQUALS, "/");
      put(ASTConstantsAssignmentExpressions.ANDEQUALS, "&");
      put(ASTConstantsAssignmentExpressions.PIPEEQUALS, "|");
      put(ASTConstantsAssignmentExpressions.ROOFEQUALS, "^");
      put(ASTConstantsAssignmentExpressions.GTGTEQUALS, ">>");
      put(ASTConstantsAssignmentExpressions.GTGTGTEQUALS, ">>>");
      put(ASTConstantsAssignmentExpressions.LTLTEQUALS, "<<");
      put(ASTConstantsAssignmentExpressions.PERCENTEQUALS, "%");
    }};

    if (operators.containsKey(operator)) {
      getPrinter().print(operators.get(operator));
    }
    else {
      Log.error("0xMT814 Missing implementation for RegularAssignmentExpression");
    }
    if (printEqualsSign) {
      getPrinter().print("=");
    }
  }

}
