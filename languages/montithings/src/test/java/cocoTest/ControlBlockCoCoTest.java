package cocoTest;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.*;
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

  @Test
  public void multipleControlBlockTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "MultipleControlBlocks");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new MaxOneControlBlock()),
            node,
            new ExpectedErrorInfo(1 , "xMT101"));
  }

  @Test
  public void batchPortTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "PortsInBatchStatementIncoming");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new PortsInBatchStatementAreIncoming()),
            node,
            new ExpectedErrorInfo(2 , "xMT111", "xMT112"));
  }

  @Test
  public void syncPortTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "PortsInSyncStatementIncoming");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new PortsInSyncGroupAreIncoming()),
            node,
            new ExpectedErrorInfo(2 , "xMT113", "xMT114"));
  }
}
