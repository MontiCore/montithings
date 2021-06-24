// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTTestDiagram;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class MainComponentExists implements SD4ComponentTestingASTTestDiagramCoCo {

  @Override
  public void check(ASTTestDiagram node) {
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(node.getMainComponent());
  }
}
