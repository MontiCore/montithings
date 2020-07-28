// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._ast.ASTComponentType;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;

/**
 * Checks that Left Side of Binding is Interface
 */
public class LeftSideIsInterface implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {
    if ((!node.isInstance() &&!node.isPresentInterfaceComponentDefinition())||(node.isInstance()&&!node.isPresentInterfaceInstanceDefinition())) {
      new InterfaceExists().check(node);
    }
    else {
      ASTComponentType component = node.isInstance()?node.getInterfaceInstanceSymbol().getTypeInfo().getAstNode():node.getInterfaceComponentDefinition();
     if (!(component instanceof ASTMTComponentType)) {
        Log.error(String.format(BindingsError.LEFT_SIDE_NO_INTERFACE.toString()));
      }
      else if (!((ASTMTComponentType) component).getMTComponentModifier().isInterface()) {
        Log.error(String.format(BindingsError.LEFT_SIDE_NO_INTERFACE.toString()));
      }
    }
  }
}