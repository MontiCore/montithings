// (c) https://github.com/MontiCore/monticore
package montithings.cocos.montiarcCopyPaste;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis._visitor.ArcBasisFullPrettyPrinter;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montithings.types.check.MontiThingsTypeCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Replaces ArcTypeCheck with our own type check.
 * Everything else is copy paste becuase its either
 * in the same method as the ArcTypeCheck usage or has
 * private visibility
 */
public class MTConnectorSourceAndTargetExistAndFit
  extends arcbasis._cocos.ConnectorSourceAndTargetExistAndFit {

  @Override public void check(ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol component = node.getSymbol();
    node.getConnectors().forEach(connector -> {
      final Optional<PortSymbol> sourcePort;
      final List<Optional<PortSymbol>> targetPorts = new ArrayList<>();
      try {
        sourcePort = getPortSymbol(connector.getSource(), component, ()->printConnector(connector), true);
        connector.streamTarget().map(target -> getPortSymbol(target, component, ()->printConnector(connector), false)).forEach(targetPorts::add);
      }
      catch (ResolvedSeveralEntriesForSymbolException e) {
        // none of this coco's business
        return;
      }

      // finally check the types of the (present) port symbols
      sourcePort.ifPresent(source ->
        targetPorts.stream().filter(Optional::isPresent).map(Optional::get).forEach(target -> {

          SymTypeExpression sourceType = source.getType();
          SymTypeExpression targetType = target.getType();

          if (!MontiThingsTypeCheck.compatible(sourceType, targetType)) {
            Log.error(
              ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH.format(
                source.getType().print(), target.getType().print(),
                printConnector(connector),
                component.getFullName()),
              connector.get_SourcePositionStart());
          }
        })
      );
    });
  }


  /**
   * @return a nice string that can be used to enhance error messages
   */
  private static String printConnector(ASTConnector connector){
    return new ArcBasisFullPrettyPrinter().prettyprint(connector)
      .replaceAll("[;\n]", "");
  }

  /**
   * Retrieves the symbol of a port and logs an error if that is not possible.
   * Also calls {@link #checkDirection(PortSymbol, ASTPortAccess, boolean, Supplier)}
   * to ensure the port is not connected backwards
   * @param portAccess the port for which the symbol should be found
   * @param component the component that contains the port
   * @param isSource identifies whether the port is the source or the target of an connector
   * @param connector string representation of the connector to use in error messages
   * @return symbol of the port, may be {@link Optional#empty()}, if {@link Log#enableFailQuick(boolean)} is disabled
   */
  private static Optional<PortSymbol> getPortSymbol(ASTPortAccess portAccess, ComponentTypeSymbol component, Supplier<String> connector, boolean isSource){
    Optional<PortSymbol> portSymbol;
    // is the port is a port of the surrounding component?
    if (!portAccess.isPresentComponent()) {
      portSymbol = component.getPort(portAccess.getPort(), true);
    }
    else {
      // is the port the port of a sub-component?
      portSymbol = component.getSubComponent(portAccess.getComponent()).flatMap(componentInstanceSymbol -> componentInstanceSymbol.getType().getPort(portAccess.getPort(), true));
    }
    // some checks with the resulting port-optional
    if (!portSymbol.isPresent()) {
      Log.error(
        String.format((isSource?ArcError.SOURCE_PORT_NOT_EXISTS:ArcError.TARGET_PORT_NOT_EXISTS).toString(),
          portAccess.getQName(),
          connector.get(),
          component.getFullName()),
        portAccess.get_SourcePositionStart());
    } else {
      checkDirection(portSymbol.get(), portAccess, isSource, connector);
    }
    return portSymbol;
  }
}
