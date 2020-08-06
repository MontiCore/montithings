// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that Implementation component exists
 *
 * @author Julian Krebber
 */
public class ImplementationExists implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {
  if(!node.isPresentImplementationComponentDefinition()) {
    Log.error(String.format(BindingsError.NO_MODEL_IMPLEMENTATION.toString()));
  }
  }
}