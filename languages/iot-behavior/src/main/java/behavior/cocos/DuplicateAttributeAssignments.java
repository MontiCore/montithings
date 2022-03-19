// (c) https://github.com/MontiCore/monticore
package behavior.cocos;

import behavior._ast.ASTAttributeAssignment;
import behavior._ast.ASTObjectExpression;
import behavior._cocos.BehaviorASTObjectExpressionCoCo;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class DuplicateAttributeAssignments implements BehaviorASTObjectExpressionCoCo {

  private MCBasicTypesFullPrettyPrinter prettyPrinter =
      new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter());

  @Override
  public void check(ASTObjectExpression node) {
    Set<String> attributeNames = new HashSet<>();
    for (ASTAttributeAssignment assignment : node.getAttributeAssignmentList()) {
      if (attributeNames.contains(assignment.getName())) {
        Log.warn("Attribute " + assignment.getName() + " gets assigned twice in ObjectExpression"
            + "of type " + node.getMCObjectType().printType(prettyPrinter));
      }
      attributeNames.add(assignment.getName());
    }
  }

  public void setPrettyPrinter(MCBasicTypesFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
  }
}
