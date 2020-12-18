// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

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
    /*
    if (!node.isPresentImplementationComponentDefinition()) {
      new ImplementationExists().check(node);
    }
    else if ((!node.isInstance() &&!node.isPresentInterfaceComponentDefinition())||(node.isInstance()&&!node.isPresentInterfaceInstanceDefinition())) {
      new InterfaceExists().check(node);
    }
    else {
      if(node.isInstance()&& !GenericBindingUtil.canImplementInterface(node.getInterfaceInstanceSymbol().getType(), node.getImplementationComponentSymbol())){
        Log.error(String.format(BindingsError.NOT_SAME_PORTS_IMPLEMENTED.toString()));
      }
      else if (!node.isInstance()&&!GenericBindingUtil.canImplementInterface(node.getInterfaceComponentSymbol(), node.getImplementationComponentSymbol())) {
        Log.error(String.format(BindingsError.NOT_SAME_PORTS_IMPLEMENTED.toString()));
      }
    }
     */
  }
}