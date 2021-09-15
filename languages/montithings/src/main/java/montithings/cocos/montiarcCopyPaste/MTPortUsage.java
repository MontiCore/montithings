/* (c) https://github.com/MontiCore/monticore */
package montithings.cocos.montiarcCopyPaste;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._cocos.PortUsage;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * checks whether ports are ports introduced in order to introduce dynamic
 * before checking whether or not they are used, as dynamic ports do not
 * have to be connected to any target
 */
public class MTPortUsage extends PortUsage {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponentType node '%s' has no symbol. "
        + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol symbol = node.getSymbol();

    // CoCo does not apply to atomic components
    if (symbol.getSubComponents().isEmpty()) {
      return;
    }

    //dynamic Ports
    Collection<String> dynamicPorts = new ArrayList<>();
    dynamicPorts.add("new_component");
    dynamicPorts.add("remove_component");

    Collection<String> sources = node.getConnectors().stream()
        .map(ASTConnector::getSourceName)
        .collect(Collectors.toList());
    Collection<String> targets = node.getConnectors().stream()
        .map(ASTConnector::getTargetsNames)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    // --------- INCOMING PORTS ----------
    Collection<String> incomingPorts = this.getNamesOfPorts(symbol.getAllIncomingPorts());
    incomingPorts.removeAll(sources);
    incomingPorts.removeAll(dynamicPorts);
    for (String port : incomingPorts) {
      final SourcePosition sourcePosition = this.getSourcePosition(symbol, node, port);
      if (targets.contains(port)) {
        Log.error(
            ArcError.INCOMING_PORT_AS_TARGET.format(port, symbol.getFullName()),
            sourcePosition);
      }
      else {
        Log.warn(
            ArcError.INCOMING_PORT_NO_FORWARD.format(port, symbol.getFullName()),
            sourcePosition);
      }
    }
    // --------- OUTGOING PORTS ----------
    Collection<String> outgoingPorts = this.getNamesOfPorts(symbol.getAllOutgoingPorts());
    outgoingPorts.removeAll(targets);
    for (String port : outgoingPorts) {
      final SourcePosition sourcePosition = this.getSourcePosition(symbol, node, port);
      if (sources.contains(port)) {
        Log.error(
            ArcError.OUTGOING_PORT_AS_SOURCE.format(port, symbol.getFullName()),
            sourcePosition);
      }
      else {
        Log.warn(
            ArcError.OUTGOING_PORT_NO_FORWARD.format(port, symbol.getFullName()),
            sourcePosition);
      }
    }
  }
}
