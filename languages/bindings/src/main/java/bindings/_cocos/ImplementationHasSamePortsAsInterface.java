// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that Implementation component has the same ports as Interface component
 */
public class ImplementationHasSamePortsAsInterface implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {
    if (!node.isPresentImplementationComponentDefinition()) {
      new ImplementationExists().check(node);
    }
    else if ((!node.isInstance() &&!node.isPresentInterfaceComponentDefinition())||(node.isInstance()&&!node.isPresentInterfaceInstanceDefinition())) {
      new InterfaceExists().check(node);
    }
    else {
      if(node.isInstance()){
        checkIfPortsMatch(node.getInterfaceInstanceSymbol().getTypeInfo(), node.getImplementationComponentSymbol());
        checkIfPortsMatch(node.getImplementationComponentSymbol(), node.getInterfaceInstanceSymbol().getTypeInfo());
      }
      else {
        checkIfPortsMatch(node.getInterfaceComponentSymbol(), node.getImplementationComponentSymbol());
        checkIfPortsMatch(node.getImplementationComponentSymbol(), node.getInterfaceComponentSymbol());
      }
    }
  }

  public void checkIfPortsMatch(ComponentTypeSymbol componentSymbol1, ComponentTypeSymbol componentSymbol2) {
    List<PortSymbol> interfacePortSymbols = componentSymbol1.getAllPorts();
    for (PortSymbol s : interfacePortSymbols) {
      Optional<PortSymbol> similarS = componentSymbol2.getPort(s.getName());
      if (!similarS.isPresent()) {
        Log.error(String.format(BindingsError.NOT_SAME_PORTS_IMPLEMENTED.toString()));
      }
      else if (s.isIncoming() != similarS.get().isIncoming()) {
        Log.error(String.format(BindingsError.NOT_SAME_PORTS_IMPLEMENTED.toString()));
      }
      else if (!s.getType().print().equals(similarS.get().getType().print())){
        Log.error(String.format(BindingsError.NOT_SAME_PORTS_IMPLEMENTED.toString()));
      }
    }
  }

}