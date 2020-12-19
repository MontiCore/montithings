// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import montithings.util.GenericBindingUtil;

/**
 * Checks that Implementation component has the same ports as Interface component
 *
 * @author Julian Krebber
 */
public class ImplementationHasSamePortsAsInterface implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {

    new ImplementationExists().check(node);
    new InterfaceExists().check(node);

    ComponentTypeSymbol compToBeReplaced =
      node.isInstance()
        ? node.getInterfaceInstanceSymbol().getType()
        : node.getInterfaceComponentSymbol();
    ComponentTypeSymbol compThatReplaces = node.getImplementationComponentSymbol();

    if (!GenericBindingUtil.canImplementInterface(compThatReplaces, compToBeReplaced)) {
      Log.error(String.format(BindingsError.NOT_SAME_PORTS_IMPLEMENTED.toString(),
        compToBeReplaced.getFullName(), compThatReplaces.getFullName()));
    }
  }

}