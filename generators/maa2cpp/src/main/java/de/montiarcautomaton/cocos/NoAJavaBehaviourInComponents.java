package de.montiarcautomaton.cocos;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;

public class NoAJavaBehaviourInComponents implements MontiArcASTJavaPBehaviorCoCo {

  @Override
  public void check(ASTJavaPBehavior node) {
    Log.error("0xMA308 JavaP is not supported by the CPP generator.",
        node.get_SourcePositionStart());
    
  }
  
}
