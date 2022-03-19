// (c) https://github.com/MontiCore/monticore
package behavior.cocos;

import behavior._ast.ASTAttributeAssignment;
import behavior._ast.ASTObjectExpression;
import behavior._cocos.BehaviorASTObjectExpressionCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class AttributeAssignmentTypesCorrect implements BehaviorASTObjectExpressionCoCo {

  private TypeCheck typeCheck;

  public AttributeAssignmentTypesCorrect(TypeCheck tc) {
    typeCheck = tc;
  }

  @Override
  public void check(ASTObjectExpression node) {
    SymTypeExpression type = typeCheck.symTypeFromAST(node.getMCObjectType());
    for (ASTAttributeAssignment assignment : node.getAttributeAssignmentList()) {
      handleAttributeAssignment(assignment, type);
    }
  }

  protected void handleAttributeAssignment(ASTAttributeAssignment assignment, SymTypeExpression type) {
    List<VariableSymbol> fields = type.getFieldList(assignment.getName(), false);

    //check that attribute is a member of the class it was assigned to
    if (fields.size() != 1) {
      Log.error("Field " + assignment.getName() + " which was accessed in ObjectExpression of type "
          + type.print() + " is not a member of this class.");
    }
    else {
      SymTypeExpression fieldType = fields.get(0).getType();
      SymTypeExpression expressionType = typeCheck.typeOf(assignment.getExpression());

      //check that attribute gets assigned to an expression with a compatible type
      if (!typeCheck.compatible(fieldType, expressionType)) {
        Log.error("Field "+ assignment.getName() + " of type " + fieldType.print() + " which was accessed "
            + "in ObjectExpression of type " + type.print() + " gets assigned to an expression with an "
            + "incompatible type (" + expressionType.print() + ").");
      }
    }
  }

  public TypeCheck getTypeCheck() {
    return typeCheck;
  }

  public void setTypeCheck(TypeCheck tc) {
    this.typeCheck = tc;
  }
}
