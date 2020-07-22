// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;

/**
 * Checks that Right Side of Binding is Implementation
 */
public class RightSideIsImplementation implements BindingsASTBindingRuleCoCo {

  // Input: BindingRule with form "Interface -> Implementation;"
  @Override
  public void check(ASTBindingRule node) {
    if (!node.isPresentImplementationComponentDefinition()) {
      new ImplementationExists().check(node);
    }
    else if(!(node.getImplementationComponentDefinition() instanceof ASTMTComponentType)){
      Log.error(String.format(BindingsError.RIGHT_SIDE_NO_IMPLEMENTATION.toString()));
    }
    else if (((ASTMTComponentType)node.getImplementationComponentDefinition()).getMTComponentModifier().isInterface()){
      Log.error(String.format(BindingsError.RIGHT_SIDE_NO_IMPLEMENTATION.toString()));
    }
  }
}