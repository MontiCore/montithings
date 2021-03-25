// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.*;
import conditionbasis._ast.ASTCondition;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._visitor.MontiThingsVisitor;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

import java.util.Optional;

public class SITypesTypeCheckCoCo extends TypeCheckCoCo implements MontiThingsASTMTComponentTypeCoCo, MontiThingsVisitor {
  /**
   * Creates an instance of TypeCheckCoCo
   *
   * @param typeCheck a {@link TypeCheck} object instantiated with the correct
   *                  ISynthesize and ITypesCalculator objects of
   *                  the current language
   */
  public SITypesTypeCheckCoCo(TypeCheck typeCheck) {
    super(typeCheck);
  }

  public static SITypesTypeCheckCoCo getCoCo() {
    TypeCheck typeCheck = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    return new SITypesTypeCheckCoCo(typeCheck);
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
      checkFieldOrVariable(node.getDeclarator(), ((ASTSimpleInit) node.getVariableInit()).getExpression());
    }
    else {
      checkFieldOrVariable(node.getDeclarator(), (ASTExpression) null);
    }
  }

  @Override
  public void visit(ASTAssignmentExpression node) {
    checkExpression(node);
  }

  @Override
  public void visit(ASTPrecondition node){
    checkCondition(node.getGuard());
  }

  @Override
  public void visit(ASTPostcondition node){
    checkCondition(node.getGuard());
  }

  private void checkCondition(ASTExpression e){
    SymTypeExpression eType = tc.typeOf(e);
    if (!TypeCheck.isBoolean(eType)) {
      Log.error("conditions have to be evaluable to boolean, but expression " + e.toString() + " is of type " + eType.getTypeInfo().getName());
    }
  }
}
