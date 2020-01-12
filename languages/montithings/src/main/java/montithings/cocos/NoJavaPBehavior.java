// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTBehaviorElementCoCo;

/**
 * Checks that there is no Java Behavior in MontiThings components
 *
 * @author (last commit) Joshua Fürste
 */
public class NoJavaPBehavior implements MontiArcASTBehaviorElementCoCo {

  @Override
  public void check(ASTBehaviorElement astBehaviorElement) {
    if (astBehaviorElement instanceof ASTJavaPBehavior)
      Log.error("0xMT125 JavaP Behavior should not be used in MontiThings components",
          astBehaviorElement.get_SourcePositionStart());
  }
}
