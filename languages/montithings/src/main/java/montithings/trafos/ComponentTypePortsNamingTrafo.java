// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTConnectStatement;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._visitor.MontiThingsTraverser;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentTypePortsNamingTrafo extends BasicTransformations implements MontiThingsTrafo, MontiThingsTraverser {
  protected static final String TOOL_NAME = "ComponentTypePortsNamingTrafo";
  protected ASTMACompilationUnit compilationUnit;
  Set<PortSymbol> portsToIgnore;

  public ComponentTypePortsNamingTrafo(Set<PortSymbol> portsToIgnore) {
    this.portsToIgnore = portsToIgnore;
  }

  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels, Collection<ASTMACompilationUnit> addedModels, ASTMACompilationUnit targetComp) {
    Log.info("Apply transformation: Component Type Port Names: " + targetComp.getComponentType().getName(), TOOL_NAME);
    compilationUnit = targetComp;
    targetComp.accept(this);
    return originalModels;
  }

  @Override
  public void visit(ASTMTComponentType comp) {
    for (PortSymbol unconnectedPort : getUnconnectedPorts(comp)) {
      addPort(comp, comp.getName() + "_" + unconnectedPort.getName(), unconnectedPort.isOutgoing(), unconnectedPort.getType());
    }
  }

  private Set<PortSymbol> getUnconnectedPorts(ASTMTComponentType comp) {
    final ComponentTypeSymbol compSymbol = comp.getSymbol();
    Set<PortSymbol> portSymbols = new HashSet<>();
    Collection<String> targets = this.getTargetNames(comp);
    addConnectorTargetsFromBehavior(targets, (ASTMTComponentType) comp);
    Collection<String> sources = this.getSourceNames(comp);
    addConnectorSourcesFromBehavior(sources, (ASTMTComponentType) comp);
    for (ComponentInstanceSymbol subSymbol : compSymbol.getSubComponents()) {
      // --------- INCOMING PORTS ----------
      Collection<String> subInputPorts = this.getNames(subSymbol.getType().getAllIncomingPorts());
      subInputPorts = subInputPorts.stream().map(s -> subSymbol.getName() + "." + s).collect(Collectors.toList());
      subInputPorts.removeAll(targets);
      for (String port : subInputPorts) {
        Optional<PortSymbol> portSymbol = subSymbol.getType().getPort(port.split("\\.")[1]);
        if (portSymbol.isPresent() && !portsToIgnore.contains(portSymbol.get())) {
          portSymbols.add(portSymbol.get());
        }
      }
      // --------- OUTGOING PORTS ----------
      Collection<String> subOutputPorts = this.getNames(subSymbol.getType().getAllOutgoingPorts());
      subOutputPorts = subOutputPorts.stream().map(s -> subSymbol.getName() + "." + s).collect(Collectors.toList());
      subOutputPorts.removeAll(sources);
      for (String port : subOutputPorts) {
        Optional<PortSymbol> portSymbol = subSymbol.getType().getPort(port.split("\\.")[1]);
        if (portSymbol.isPresent() && !portsToIgnore.contains(portSymbol.get())) {
          portSymbols.add(portSymbol.get());
        }
      }
    }
    return portSymbols;
  }

  protected void addConnectorSourcesFromBehavior(Collection<String> sources, ASTMTComponentType node) {
    List<ASTBehavior> behaviors = node.getBody().getArcElementList().stream().filter(element -> element instanceof ASTBehavior).map(behavior -> (ASTBehavior) behavior).collect(Collectors.toList());
    for (ASTBehavior behavior : behaviors) {
      List<ASTConnectStatement> connectStatements = behavior.getMCJavaBlock().getMCBlockStatementList().stream().filter(element -> element instanceof ASTConnectStatement).map(connector -> ((ASTConnectStatement) connector)).collect(Collectors.toList());
      for (ASTConnectStatement connectStatement : connectStatements) {
        sources.add(connectStatement.getConnector().getSourceName());
      }
    }
  }

  protected void addConnectorTargetsFromBehavior(Collection<String> sources, ASTMTComponentType node) {
    List<ASTBehavior> behaviors = node.getBody().getArcElementList().stream().filter(element -> element instanceof ASTBehavior).map(behavior -> (ASTBehavior) behavior).collect(Collectors.toList());
    for (ASTBehavior behavior : behaviors) {
      List<ASTConnectStatement> connectStatements = behavior.getMCJavaBlock().getMCBlockStatementList().stream().filter(element -> element instanceof ASTConnectStatement).map(connector -> ((ASTConnectStatement) connector)).collect(Collectors.toList());
      for (ASTConnectStatement connectStatement : connectStatements) {
        sources.addAll(connectStatement.getConnector().getTargetsNames());
      }
    }
  }

  protected Collection<String> getSourceNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getSourceName).collect(Collectors.toList());
  }

  protected Collection<String> getTargetNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getTargetsNames).flatMap(Collection::stream).collect(Collectors.toList());
  }

  protected Collection<String> getNames(Collection<PortSymbol> ports) {
    return ports.stream().map(PortSymbol::getName).collect(Collectors.toList());
  }
}
