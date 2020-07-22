// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

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
    if (!node.isPresentInterfaceComponentDefinition()) {
      new InterfaceExists().check(node);
    }
    else if(!(node.getInterfaceComponentDefinition() instanceof ASTMTComponentType)){
      Log.error(String.format(BindingsError.LEFT_SIDE_NO_INTERFACE.toString()));
    }
    else if (!((ASTMTComponentType)node.getInterfaceComponentDefinition()).getMTComponentModifier().isInterface()){
      Log.error(String.format(BindingsError.LEFT_SIDE_NO_INTERFACE.toString()));
    }
  }
}