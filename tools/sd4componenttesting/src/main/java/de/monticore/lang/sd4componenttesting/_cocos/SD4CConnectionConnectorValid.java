// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CConnection;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.lang.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class SD4CConnectionConnectorValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    // define no connector in MontiArc models
    if (!node.isPresentSource() || node.getTargetList().isEmpty()) {
      return;
    }

    ASTPortAccess source = node.getSource();
    List<ASTPortAccess> targetList = node.getTargetList();

    for (ASTPortAccess target : targetList) {
      List<ASTConnector> connectors = mainComponent.getAstNode().getConnectorsMatchingSource(source.getQName());

      boolean found = false;
      for (ASTConnector connector : connectors) {
        for (ASTPortAccess connectorTarget : connector.getTargetList()) {
          if (connectorTarget.getQName().equals(target.getQName())) {
            found = true;
          }
        }
      }

      if (!found) {
        SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
        String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
        Log.error(String.format(
          SD4ComponentTestingError.CONNECTION_NOT_DEFINED_AS_CONNECTOR.toString(),
          nodeString,
          mainComponent.getName()));
        return;
      }
    }
  }
}
