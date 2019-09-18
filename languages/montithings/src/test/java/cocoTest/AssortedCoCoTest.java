/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cocoTest;


import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.MaxOneBehaviorPerComponent;
import montithings.cocos.NoJavaImportStatements;
import montithings.cocos.NoJavaPBehavior;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssortedCoCoTest extends AbstractCoCoTest  {
  private static final String PACKAGE = "cocoTest";

  @BeforeClass
  public static void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void multipleBehaviorTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "MultipleExecutionBlocks");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new MaxOneBehaviorPerComponent()),
            node,
            new ExpectedErrorInfo(1, "xMT110"));
  }

  @Test
  public void javaPBehaviorTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "JavaPBehavior");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new NoJavaPBehavior()),
            node,
            new ExpectedErrorInfo(1, "xMT125"));
  }

  @Test
  public void javaImportTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "JavaImport");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new NoJavaImportStatements()),
            node,
            new ExpectedErrorInfo(1, "xMT124"));
  }

}
