// (c) https://github.com/MontiCore/monticore
package cocoTest;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMontiThingsNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ControlBlockCoCoTest extends AbstractCoCoTest {

  private static final String PACKAGE = "cocoTest";

  @BeforeClass
  public static void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void emptyControlBlockTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "ControlBlockEmpty");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ControlBlockNotEmpty()),
            node,
            new ExpectedErrorInfo(1, "xMT102"));
  }

  @Test
  public void controlStatementsInComposedComponentTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "ControlBlockInComposedComponent");
    checkInvalid(new MontiThingsCoCoChecker()
            .addCoCo(new ControlBlockStatementsInComposedComponent()),
            node,
            new ExpectedErrorInfo(1, "xMT103"));
  }

  @Test
  public void multipleControlBlockTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "MultipleControlBlocks");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new MaxOneControlBlock()),
            node,
            new ExpectedErrorInfo(1 , "xMT101"));
  }

  @Test
  public void batchPortTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "PortsInBatchStatementIncoming");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new PortsInBatchStatementAreIncoming()),
            node,
            new ExpectedErrorInfo(2 , "xMT111", "xMT112"));
  }

  @Test
  public void syncPortTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "PortsInSyncStatementIncoming");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new PortsInSyncGroupAreIncoming()),
            node,
            new ExpectedErrorInfo(2 , "xMT113", "xMT114"));
  }

  @Test
  public void emptySyncTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "EmptySyncStatement");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new SyncGroupHasAtLeastOneElement()),
            node,
            new ExpectedErrorInfo(1 , "xMT115"));
  }

  @Test
  public void syncSubsetTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "SyncSubset");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new SyncGroupIsNoSubset()),
            node,
            new ExpectedErrorInfo(1 , "xMT116"));
  }

  @Test
  public void syncLowercaseTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "SyncLowercase");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new SyncGroupNamesUppercase()),
            node,
            new ExpectedErrorInfo(1 , "xMT116"));
  }

  @Test
  public void zeroUpdateIntervalTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "NegativeUpdateInterval");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new UpdateIntervalPositive()),
            node,
            new ExpectedErrorInfo(1 , "xMT118"));
  }

  @Test
  public void DoubleUpdateIntervalTest(){
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
            "." + "DoubleUpdateInterval");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new MaxOneUpdateInterval()),
            node,
            new ExpectedErrorInfo(1 , "xMT132"));
  }
}
