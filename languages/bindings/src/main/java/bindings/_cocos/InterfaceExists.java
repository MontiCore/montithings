// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that Interface component exists
 */
public class InterfaceExists implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {
    if(!node.isPresentInterfaceComponentDefinition()) {
      Log.error(String.format(BindingsError.NO_MODEL_INTERFACE.toString()));
    }
  }
}