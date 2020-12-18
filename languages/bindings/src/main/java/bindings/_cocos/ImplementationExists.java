// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that Implementation component exists
 *
 * @author Julian Krebber
 */
public class ImplementationExists implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {
    String implComp = node.getImplementationComponent().getQName();
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(implComp);

    if (!comp.isPresent()) {
      Log.error(String.format(BindingsError.NO_MODEL_IMPLEMENTATION.toString(), implComp));
    }
  }
}