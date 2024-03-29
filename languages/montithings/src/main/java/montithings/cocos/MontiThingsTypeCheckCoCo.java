// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._symboltable.PortSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.collect.LinkedListMultimap;
import componenttest._ast.ASTExpectValueOnPort;
import componenttest._ast.ASTSendValueOnPort;
import componenttest._ast.ASTTestBlock;
import componenttest._visitor.ComponentTestVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor2;
import de.monticore.symbols.basicsymbols._ast.ASTFunction;
import de.monticore.symbols.basicsymbols._ast.ASTVariable;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeConstant;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;
import montithings.MontiThingsMill;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._symboltable.MontiThingsScope;
import montithings._visitor.MontiThingsTraverser;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;
import prepostcondition._visitor.PrePostConditionVisitor2;

import java.util.HashMap;
import java.util.Map;

import static montithings.util.PortUtil.findPortSymbolOfTestBlock;

public class MontiThingsTypeCheckCoCo extends TypeCheckCoCo
  implements MontiThingsASTMTComponentTypeCoCo, ArcBasisVisitor2,
  MCVarDeclarationStatementsVisitor2, MCCommonStatementsVisitor2,
  PrePostConditionVisitor2, ComponentTestVisitor2 {
  /**
   * Creates an instance of TypeCheckCoCo
   *
   * @param typeCheck a {@link TypeCheck} object instantiated with the correct
   *                  ISynthesize and ITypesCalculator objects of
   *                  the current language
   */
  public MontiThingsTypeCheckCoCo(TypeCheck typeCheck) {
    super(typeCheck);
  }

  public static MontiThingsTypeCheckCoCo getCoCo() {
    TypeCheck typeCheck = new MontiThingsTypeCheck(new SynthesizeSymTypeFromMontiThings(),
      new DeriveSymTypeOfMontiThingsCombine());
    return new MontiThingsTypeCheckCoCo(typeCheck);
  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4ArcBasis(this);
    traverser.add4MCVarDeclarationStatements(this);
    traverser.add4MCCommonStatements(this);
    traverser.add4PrePostCondition(this);
    traverser.add4ComponentTest(this);
    return traverser;
  }

  @Override
  public void check(ASTMTComponentType ast) {
    ast.accept(createTraverser());
  }

  @Override
  public void visit(ASTArcParameter node) {
    if (node.isPresentDefault()) {
      checkFieldOrVariable(node, node.getDefault());
    }
    else {
      checkFieldOrVariable(node, (ASTExpression) null);
    }
  }

  @Override
  public void visit(ASTArcField node) {
    checkFieldOrVariable(node, node.getInitial());
  }

  @Override
  public void visit(ASTVariableDeclarator node) {
    //check if VariableInit is an expression, otherwise initiation cannot be checked here
    if (node.isPresentVariableInit() && node.getVariableInit() instanceof ASTSimpleInit) {
      checkFieldOrVariable(node.getDeclarator(),
        ((ASTSimpleInit) node.getVariableInit()).getExpression());
    }
    else {
      checkFieldOrVariable(node.getDeclarator(), (ASTExpression) null);
    }
  }

  @Override
  public void visit(ASTExpressionStatement node) {
    checkExpression(node.getExpression());
  }

  @Override
  public void visit(ASTPrecondition node) {
    checkCondition(node.getGuard());
  }

  @Override
  public void visit(ASTPostcondition node) {
    checkCondition(node.getGuard());
  }

  @Override
  public void visit(ASTIfStatement node) {
    checkCondition(node.getCondition());
  }

  @Override
  public void visit(ASTCommonForControl node) {
    if (node.isPresentCondition()) {
      checkCondition(node.getCondition());
    }
    for (ASTExpression e : node.getExpressionList()) {
      checkExpression(e);
    }
  }

  @Override
  public void visit(ASTForInitByExpressions node) {
    for (ASTExpression e : node.getExpressionList()) {
      checkExpression(e);
    }
  }

  @Override
  public void visit(ASTEnhancedForControl node) {
    checkExpression(node.getExpression());
  }

  @Override
  public void visit(ASTWhileStatement node) {
    checkCondition(node.getCondition());
  }

  @Override
  public void visit(ASTDoWhileStatement node) {
    checkCondition(node.getCondition());
  }

  @Override
  public void visit(ASTSwitchStatement node) {
    checkExpression(node.getExpression());
  }

  @Override
  public void visit(ASTConstantExpressionSwitchLabel node) {
    checkExpression(node.getConstant());
  }

  @Override
  public void visit(ASTTestBlock node) {
    Map<String, SymTypeExpression> portTypes = new HashMap<>();
    PortSymbol port = findPortSymbolOfTestBlock(node);
    addPortTypes(portTypes, port);

    for (ASTSendValueOnPort sendValueOnPort : node.getSendValueOnPortList()) {
      SymTypeExpression portType = portTypes.get(sendValueOnPort.getName());
      SymTypeExpression expressionType = tc.typeOf(sendValueOnPort.getExpression());
      if (!MontiThingsTypeCheck.compatible(portType, expressionType)) {
        Log.error("Type of port " + sendValueOnPort.getName() + " not compatible with its assigned expression" +
          " in ASTSendValueOnPort from test block for port " + node.getPortName() + "!");
      }
    }

    for (ASTExpectValueOnPort expectValueOnPort : node.getExpectValueOnPortList()) {
      SymTypeExpression portType = portTypes.get(expectValueOnPort.getName());
      SymTypeExpression expressionType = tc.typeOf(expectValueOnPort.getExpression());
      if (!expectValueOnPort.getCompareOperator().isPresentEquals() &&
        !expectValueOnPort.getCompareOperator().isPresentNotEquals()) {
        // other comparison operators can only be used for numeric values
        if (portType instanceof SymTypeConstant && !((SymTypeConstant) portType).isNumericType()) {
          Log.error("Comparison Operators other than equals and notEquals only allow numeric values in " +
            "ASTExpectValueOnPort nonterminals");
        }
      }
      if (!MontiThingsTypeCheck.compatible(portType, expressionType)) {
        Log.error("Type of port " + expectValueOnPort.getName() + " not compatible with its assigned expression" +
          " in ASTSendValueOnPort from test block for port " + node.getPortName() + "!");
      }
    }
  }

  private void addPortTypes(Map<String, SymTypeExpression> portTypes, PortSymbol port) {
    if (port != null) {
      TypeSymbol type = port.getTypeInfo();
      if (type.getSpannedScope() instanceof MontiThingsScope) {
        LinkedListMultimap<String, FieldSymbol> fieldSymbols = ((MontiThingsScope) type.getSpannedScope()).getFieldSymbols();
        for (String name : fieldSymbols.keySet()) {
          // check whether port is incoming or outgoing
          SymTypeExpression portType = fieldSymbols.get(name).get(0).getType();
          if (portType instanceof SymTypeOfGenerics) {
            portTypes.put(name, ((SymTypeOfGenerics) portType).getArgument(0));
          }
        }
      }
    }
  }

  private void checkCondition(ASTExpression e) {
    if (tc instanceof MontiThingsTypeCheck) {
      ((MontiThingsTypeCheck) tc).setCondition(true);
    }
    SymTypeExpression eType = tc.typeOf(e);
    if (!MontiThingsTypeCheck.isBoolean(eType)) {
      Log.error("conditions have to be evaluable to boolean, but expression "
        + e.toString() + " is of type " + eType.getTypeInfo().getName());
    }
    if (tc instanceof MontiThingsTypeCheck) {
      ((MontiThingsTypeCheck) tc).setCondition(false);
    }
  }

  @Override
  protected void checkFieldOrVariable(ASTVariable node, SymTypeExpression assignmentType) {
    if (!node.isPresentSymbol())
      logError(node, "Variable symbol not present");
    else if (node.getSymbol().getType() == null)
      logError(node, "Variable symbol has no type");
    else if (assignmentType != null && !OCLTypeCheck
      .compatible(node.getSymbol().getType(), assignmentType))
      logError(node, String.format("Variable type %s incompatible to assigned type %s",
        node.getSymbol().getType().print(), assignmentType.print()));
  }

  @Override
  protected void checkMethodOrFunction(ASTFunction node, SymTypeExpression returnType) {
    if (!node.isPresentSymbol())
      logError(node, "Function symbol not present");
    else if (node.getSymbol().getReturnType() == null)
      logError(node, "Function symbol has no return type");
    else {
      if (node.getSymbol().getReturnType().isVoidType()) {
        if (returnType != null && !returnType.isVoidType())
          logError(node, String.format("Return type void incompatible to actual return type %s",
            returnType.print()));
      }
      else if (returnType == null)
        logError(node, "No return type given");
      else if (!OCLTypeCheck.compatible(node.getSymbol().getReturnType(), returnType))
        logError(node, String.format("Return type %s incompatible to actual return type %s",
          node.getSymbol().getReturnType().print(), returnType.print()));
    }
  }
}
