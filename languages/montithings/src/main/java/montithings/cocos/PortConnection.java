/* (c) https://github.com/MontiCore/monticore */
package montithings.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.SubComponentsConnected;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * All ports of subcomponents should be used in at least one connector or be handled by templates.
 */
public class PortConnection extends SubComponentsConnected {

  /**
   * Ports with templates that should be ignored when checking for unconnected ports
   */
  Set<PortSymbol> portsToIgnore;

  @Override
  public void check(ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
        + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentTypeSymbol compSymbol = node.getSymbol();
    final Collection<String> targets = this.getTargetNames(node);
    final Collection<String> sources = this.getSourceNames(node);
    for (ComponentInstanceSymbol subSymbol : compSymbol.getSubComponents()) {
      if (subSymbol.getType().loadSymbol().isPresent()) {
        // --------- INCOMING PORTS ----------
        Collection<String> subInputPorts =
            this.getNames(subSymbol.getTypeInfo().getAllIncomingPorts());
        subInputPorts = subInputPorts.stream()
            .map(s -> subSymbol.getName() + "." + s)
            .collect(Collectors.toList());
        subInputPorts.removeAll(targets);
        for (String port : subInputPorts) {
          SourcePosition sourcePosition = this.getSourcePosition(compSymbol, node, port);
          if (sources.contains(port)) {
            Log.error(
                String.format(ArcError.INCOMING_PORT_AS_SOURCE.toString(), port,
                    subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          } else {
            Optional<PortSymbol> portSymbol = subSymbol.getType().loadSymbol().get().getPort(port.split("\\.")[1]);
            if (portSymbol.isPresent() && !portsToIgnore.contains(portSymbol.get())) {
              Log.error(String.format(ArcError.INCOMING_PORT_NOT_CONNECTED.toString(), port, subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
            }
          }
        }
        // --------- OUTGOING PORTS ----------
        Collection<String> subOutputPorts
            = this.getNames(subSymbol.getTypeInfo().getAllOutgoingPorts());
        subOutputPorts = subOutputPorts.stream()
            .map(s -> subSymbol.getName() + "." + s)
            .collect(Collectors.toList());
        subOutputPorts.removeAll(sources);
        for (String port : subOutputPorts) {
          SourcePosition sourcePosition = this.getSourcePosition(compSymbol, node, port);
          if (targets.contains(port)) {
            Log.error(
                String.format(ArcError.OUTGOING_PORT_AS_TARGET.toString(), port,
                    subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          } else {
            Optional<PortSymbol> portSymbol = subSymbol.getType().loadSymbol().get().getPort(port.split("\\.")[1]);
            if (portSymbol.isPresent() && !portsToIgnore.contains(portSymbol.get())) {
               Log.error(String.format(ArcError.OUTGOING_PORT_NOT_CONNECTED.toString(), port, subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
            }
          }
        }
      } else {
        Log.error(String.format(ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE.toString(),
            subSymbol.getType().getName(), subSymbol.getFullName()),
            subSymbol.getAstNode().get_SourcePositionStart());
      }
    }
  }

  public PortConnection(Set<PortSymbol> portsToIgnore) {
    this.portsToIgnore = portsToIgnore;
  }
}
