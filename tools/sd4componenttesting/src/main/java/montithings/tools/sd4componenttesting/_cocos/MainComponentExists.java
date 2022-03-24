// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import montithings.tools.sd4componenttesting._ast.ASTTestDiagram;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;

import java.util.Optional;

public class MainComponentExists implements SD4ComponentTestingASTTestDiagramCoCo {
  @Override
  public void check(ASTTestDiagram node) {
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(node.getMainComponent());
    if (!comp.isPresent()) {
      Log.error(String.format(SD4ComponentTestingError.NO_MAIN_COMPONENT_IMPLEMENTATION.toString(), node.getMainComponent()));
    }
  }
}
