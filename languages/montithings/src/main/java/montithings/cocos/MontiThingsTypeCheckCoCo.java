// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.symbols.basicsymbols._ast.ASTFunction;
import de.monticore.symbols.basicsymbols._ast.ASTVariable;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._visitor.MontiThingsVisitor;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

public class MontiThingsTypeCheckCoCo extends TypeCheckCoCo
  implements MontiThingsASTMTComponentTypeCoCo, MontiThingsVisitor {
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

  @Override
  public void check(ASTMTComponentType ast) {
    ast.accept(this);
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
