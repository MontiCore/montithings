// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that Interface component exists
 *
 * @author Julian Krebber
 */
public class InterfaceExists implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {
    checkInterfaceComponent(node);
    checkInterfaceInstance(node);
  }

  protected void checkInterfaceInstance(ASTBindingRule node) {
    if (node.isPresentInterfaceInstance()) {
      String interInstanceName = node.getInterfaceInstance().getQName();
      Optional<ComponentInstanceSymbol> comp = node.getEnclosingScope().resolveComponentInstance(interInstanceName);
      if (!comp.isPresent()) {
        Log.error(String.format(BindingsError.NO_MODEL_INTERFACE.toString(), interInstanceName));
      }
    }
  }

  protected void checkInterfaceComponent(ASTBindingRule node) {
    if (node.isPresentInterfaceComponent()) {
      String interCompName = node.getInterfaceComponent().getQName();
      Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(interCompName);
      if (!comp.isPresent()) {
        Log.error(String.format(BindingsError.NO_MODEL_INTERFACE.toString(), interCompName));
      }
    }
  }
}