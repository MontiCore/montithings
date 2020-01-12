// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montithings._ast.ASTResourceInterface;

/**
 * Checks that resource ports only exist in components tagged with the
 * <<deploy>> stereotype.
 *
 * @author (last commit) Joshua Fürste
 */
public class ResourcePortsOnlyOnOutermostComponent implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if (!node.getStereotypeOpt().isPresent() || !node.getStereotypeOpt().get().containsStereoValue("deploy")){
      node.getBody().getElementList()
              .stream()
              .filter(ASTResourceInterface.class::isInstance)
              .forEach(e -> Log.error("0xMT127 Non-Deploy component " + node.getName() + " contains " +
                      "resource ports.", e.get_SourcePositionStart()));
    }
  }
}
