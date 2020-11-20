/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.helper;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montithings.generator.codegen.ConfigParams;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * All ports of subcomponents should be used in at least one connector or be handled by templates.
 */
public class PortConnection {

  public void check(ASTComponentType node, ConfigParams config) {
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
            if(portSymbol.isPresent()&&config.getAdditionalPort(portSymbol.get()).isPresent()){
              config.getOverridePorts().add(portSymbol.get());
            }
            else {
              Log.error(String.format(ArcError.INCOMING_PORT_NOT_CONNECTED.toString(), port, subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
            }
          }
        }
        // --------- INCOMING PORTS ----------
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
            if(portSymbol.isPresent()&&config.getAdditionalPort(portSymbol.get()).isPresent()){
              config.getOverridePorts().add(portSymbol.get());
            }
            else {
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

  protected Collection<String> getNames(Collection<PortSymbol> ports) {
    return ports.stream().map(PortSymbol::getName).collect(Collectors.toList());
  }

  protected Collection<String> getSourceNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getSourceName)
        .collect(Collectors.toList());
  }

  protected Collection<String> getTargetNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getTargetsNames)
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  protected SourcePosition getSourcePosition(ComponentTypeSymbol symbol,
      ASTComponentType node, String port) {
    return symbol.getPort(port.split("\\.")[1]).map(p -> p.getAstNode().get_SourcePositionStart())
        .orElse(node.get_SourcePositionEnd());
  }
}
