package cocoTest;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._ast.ASTMontiThingsNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.ControlBlockNotEmpty;
import montithings.cocos.ControlBlockStatementsInComposedComponent;
import org.junit.BeforeClass;
import org.junit.Test;

public class ControlBlockCoCoTest extends AbstractCoCoTest {

  private static final String PACKAGE = "cocoTest";


  @BeforeClass
  public static void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void emptyControlBlockTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "ControlBlockEmpty");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ControlBlockNotEmpty()),
            node,
            new ExpectedErrorInfo(1, "xMT102"));
  }

  @Test
  public void controlStatementsInComposedComponentTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "ControlBlockInComposedComponent");
    checkInvalid(new MontiThingsCoCoChecker()
            .addCoCo(new ControlBlockStatementsInComposedComponent()),
            node,
            new ExpectedErrorInfo(1, "xMT103"));
  }
}
