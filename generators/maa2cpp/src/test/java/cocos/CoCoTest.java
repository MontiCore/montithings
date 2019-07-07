package cocos;

import javax.annotation.MatchesPattern.Checker;

import org.junit.BeforeClass;
import org.junit.Test;

import de.montiarcautomaton.cocos.NoAJavaBehaviourInComponents;
import de.montiarcautomaton.cocos.NoJavaImportsForCPPGenerator;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.MontiArcCoCos;

public class CoCoTest extends AbstractCoCoTest{
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAJavaBehaviorInComponents() {
    MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    ASTMontiArcNode node = loadComponentAST("cocoModels.DistanceLogger");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new NoAJavaBehaviourInComponents()),
        node, new ExpectedErrorInfo(1, "xMA300"));
  }
  
  @Test
  public void testJavaImports() {
    MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    ASTMontiArcNode node = loadComponentAST("cocoModels.Imports");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new NoJavaImportsForCPPGenerator()),
        node, new ExpectedErrorInfo(1, "xMA301"));
  }
  

}
