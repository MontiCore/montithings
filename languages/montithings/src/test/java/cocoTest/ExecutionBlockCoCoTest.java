package cocoTest;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.ExecutionBlockPriorityCorrectness;
import montithings.cocos.ExecutionBlockWellFormed;
import montithings.cocos.ExecutionGuardIsValid;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExecutionBlockCoCoTest extends AbstractCoCoTest {

  private static final String PACKAGE = "cocoTest";

  @BeforeClass
  public static void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void validTest(){
    checkValid(PACKAGE + "." + "ValidExecutionBlock");
  }

  @Test
  public void executionWithoutElseTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "ExecutionWithoutElse");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ExecutionBlockWellFormed()),
            node,
            new ExpectedErrorInfo(1, "xMT104"));
  }

  @Test
  public void executionBlockEmpty(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "ExecutionBlockEmpty");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ExecutionBlockWellFormed()),
            node,
            new ExpectedErrorInfo(1, "xMT104"));
  }

  @Test
  public void executionHasMoreThanOneElseTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "ExecutionWithTwoElse");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ExecutionBlockWellFormed()),
            node,
            new ExpectedErrorInfo(1, "xMT105"));
  }

  @Test
  public void executionPrioritiesTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "ExecutionBlockPriorities");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ExecutionBlockPriorityCorrectness()),
            node,
            new ExpectedErrorInfo(2, "xMT106", "xMT107"));
  }

  @Test
  public void executionGuardWithUndefinedElementsTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "UndefinedExecutionGuard");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ExecutionGuardIsValid()),
            node,
            new ExpectedErrorInfo(1 , "xMT108"));
  }


}
