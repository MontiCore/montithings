// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import montithings.tools.sd4componenttesting._ast.ASTSD4CConnection;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import montithings.tools.sd4componenttesting.util.SD4CElementType;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;

import java.util.Optional;

public class SD4CConnectionMainOutputValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    //Case 2: PORTACCESS -> : VALUE;
    // will checked in case 3 or 4
    if (node.getType() != SD4CElementType.MAIN_OUTPUT) {
      return;
    }

    if (node.getSource().isPresentComponent()) {
      SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
      String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
      Log.error(String.format(
        SD4ComponentTestingError.MAIN_OUTPUT_COMPONENT_GIVEN.toString(),
        node.getSource().getComponent(),
        nodeString));
      return;
    }

    Optional<PortSymbol> portSymbol = mainComponent.getOutgoingPort(node.getSource().getPort());

    if (!portSymbol.isPresent()) {
      SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
      String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
      Log.error(String.format(
        SD4ComponentTestingError.MAIN_OUTPUT_UNKNOWN_PORT.toString(),
        node.getSource().getPort(),
        nodeString,
        mainComponent.getName()));
    }
  }
}
