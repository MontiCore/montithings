// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.SubComponentsConnected;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import behavior._ast.ASTConnectStatement;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;

import java.util.Collection;
import java.util.List;
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
    Collection<String> targets = this.getTargetNames(node);
    addConnectorTargetsFromBehavior(targets, (ASTMTComponentType) node);
    Collection<String> sources = this.getSourceNames(node);
    addConnectorSourcesFromBehavior(sources, (ASTMTComponentType) node);
    for (ComponentInstanceSymbol subSymbol : compSymbol.getSubComponents()) {
      // --------- INCOMING PORTS ----------
      Collection<String> subInputPorts =
        this.getNames(subSymbol.getType().getAllIncomingPorts());
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
        }
        else {
          Optional<PortSymbol> portSymbol = subSymbol.getType().getPort(port.split("\\.")[1]);
          if (portSymbol.isPresent() && !portsToIgnore.contains(portSymbol.get())) {
            Log.error(String.format(ArcError.INCOMING_PORT_NOT_CONNECTED.toString(), port,
              subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          }
        }
      }
      // --------- OUTGOING PORTS ----------
      Collection<String> subOutputPorts
        = this.getNames(subSymbol.getType().getAllOutgoingPorts());
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
        }
        else {
          Optional<PortSymbol> portSymbol = subSymbol.getType().getPort(port.split("\\.")[1]);
          if (portSymbol.isPresent() && !portsToIgnore.contains(portSymbol.get())) {
            Log.error(String.format(ArcError.OUTGOING_PORT_NOT_CONNECTED.toString(), port,
              subSymbol.getFullName(), compSymbol.getFullName()), sourcePosition);
          }
        }
      }
    }
  }

  protected void addConnectorSourcesFromBehavior(Collection<String> sources, ASTMTComponentType node) {
    List<ASTBehavior> behaviors = node.getBody()
            .getArcElementList()
            .stream()
            .filter(element -> element instanceof ASTBehavior)
            .map(behavior -> (ASTBehavior) behavior)
            .collect(Collectors.toList());
    for (ASTBehavior behavior : behaviors) {
      List<ASTConnectStatement> connectStatements = behavior.getMCJavaBlock().getMCBlockStatementList().stream()
              .filter(element -> element instanceof ASTConnectStatement)
              .map(connector -> ((ASTConnectStatement) connector))
              .collect(Collectors.toList());
      for (ASTConnectStatement connectStatement : connectStatements){
        sources.add(connectStatement.getConnector().getSourceName());
      }
    }
  }

  protected void addConnectorTargetsFromBehavior(Collection<String> sources, ASTMTComponentType node) {
    List<ASTBehavior> behaviors = node.getBody()
            .getArcElementList()
            .stream()
            .filter(element -> element instanceof ASTBehavior)
            .map(behavior -> (ASTBehavior) behavior)
            .collect(Collectors.toList());
    for (ASTBehavior behavior : behaviors) {
      List<ASTConnectStatement> connectStatements = behavior.getMCJavaBlock().getMCBlockStatementList().stream()
              .filter(element -> element instanceof ASTConnectStatement)
              .map(connector -> ((ASTConnectStatement) connector))
              .collect(Collectors.toList());
      for (ASTConnectStatement connectStatement : connectStatements){
        sources.addAll(connectStatement.getConnector().getTargetsNames());
      }
    }
  }

  public PortConnection(Set<PortSymbol> portsToIgnore) {
    this.portsToIgnore = portsToIgnore;
  }
}
