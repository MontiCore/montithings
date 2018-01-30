package component.body;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc.cocos.MontiArcCoCos;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class checks all context conditions related the combination of elements in component
 * bodies
 *
 * @author Andreas Wortmann
 */
public class BodyTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ComponentWithAJavaAndAutomaton");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA050"));
  }
  
  @Test
  public void testAmbiguousPortAndVariableNames() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "AmbiguousPortAndVariableNames");
    checkInvalid(MontiArcCoCos.createChecker(),node, new ExpectedErrorInfo(6,  "xMA035", "xMA053"));
  }
  
}
