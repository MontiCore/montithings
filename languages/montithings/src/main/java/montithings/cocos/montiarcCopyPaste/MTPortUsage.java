// (c) https://github.com/MontiCore/monticore
package montithings.cocos.montiarcCopyPaste;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import behavior._ast.ASTConnectStatement;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings.util.ClassDiagramUtil;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static montithings.util.GenericBindingUtil.getComponentFromString;

public class MTPortUsage implements MontiThingsASTMTComponentTypeCoCo {
  @Override
  public void check(@NotNull ASTMTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponentType node '%s' has no symbol. "
            + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol symbol = node.getSymbol();

    // CoCo does not apply to atomic components
    if (symbol.getSubComponents().isEmpty()) {
      return;
    }

    Collection<String> sources = node.getConnectors().stream()
            .map(ASTConnector::getSourceName)
            .collect(Collectors.toList());
    addConnectorSourcesFromBehavior(sources, node);
    Collection<String> targets = node.getConnectors().stream()
            .map(ASTConnector::getTargetsNames)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    addConnectorTargetsFromBehavior(targets, node);
    // --------- INCOMING PORTS ----------
    Collection<String> incomingPorts = this.getNamesOfPorts(symbol.getAllIncomingPorts());
    incomingPorts.removeAll(sources);
    for (String port : incomingPorts) {
      final SourcePosition sourcePosition = this.getSourcePosition(symbol, node, port);
      if (targets.contains(port)) {
        Log.error(
                ArcError.INCOMING_PORT_AS_TARGET.format(port, symbol.getFullName()),
                sourcePosition);
      }
      else {
        if (!node.getSpannedScope().resolvePort(port).isPresent()) {
          Log.warn(
                  ArcError.INCOMING_PORT_NO_FORWARD.format(port, symbol.getFullName()),
                  sourcePosition);
        }
        String portTypeName = node.getSpannedScope().resolvePort(port).get().getTypeInfo().getName();
        if (!portTypeName.startsWith(ClassDiagramUtil.COMPONENT_TYPE_PREFIX) || getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(),
                portTypeName.substring(2)) == null) {
          Log.warn(
                  ArcError.INCOMING_PORT_NO_FORWARD.format(port, symbol.getFullName()),
                  sourcePosition);
        }
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

  protected Collection<String> getNamesOfPorts(Collection<PortSymbol> ports) {
    return ports.stream().map(PortSymbol::getName).collect(Collectors.toList());
  }

  protected SourcePosition getSourcePosition(ComponentTypeSymbol symbol,
                                             ASTComponentType node, String port) {
    return symbol.getPort(port).map(p -> p.getAstNode().get_SourcePositionStart())
            .orElse(node.get_SourcePositionEnd());
  }
}
