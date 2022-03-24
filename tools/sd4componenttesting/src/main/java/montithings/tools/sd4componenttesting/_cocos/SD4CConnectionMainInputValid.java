// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
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

import java.util.List;
import java.util.Optional;

public class SD4CConnectionMainInputValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    //Case 3: -> PORTACCESS : VALUE;
    // will checked in case 2 or 4
    if (node.getType() != SD4CElementType.MAIN_INPUT) {
      return;
    }

    List<ASTPortAccess> targetList = node.getTargetList();

    // check if one target defines a component
    for (ASTPortAccess target : targetList) {
      if (target.isPresentComponent()) {
        SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
        String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
        Log.error(String.format(
          SD4ComponentTestingError.MAIN_INPUT_COMPONENT_GIVEN.toString(),
          target.getComponent(),
          nodeString));
        return;
      }
    }

    // check if one target defines port which is not a incoming port of main component
    for (ASTPortAccess target : targetList) {
      Optional<PortSymbol> portSymbol = mainComponent.getIncomingPort(target.getPort());

      if (!portSymbol.isPresent()) {
        SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
        String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
        Log.error(String.format(
          SD4ComponentTestingError.MAIN_INPUT_UNKNOWN_PORT.toString(),
          target.getPort(),
          nodeString,
          mainComponent.getName()));
      }
    }
  }
}
