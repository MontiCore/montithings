/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cocoTest;


import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.*;
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

  @Test
  public void TimeSyncInAtomicTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "TimeSyncInAtomic");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new TimeSyncOnlyInComposedComponents()),
            node,
            new ExpectedErrorInfo(1, "xMT119"));
  }

  @Test
  public void TimeSyncInSubComponentsTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE +
            "." + "TimeSyncInSubComps");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new TimeSyncInSubComponents()),
            node,
            new ExpectedErrorInfo(2, "xMT120"));
  }

  @Test
  public void resourcePortsInNonDeployTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "ResourcePortInNonDeploy");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ResourcePortsOnlyOnOutermostComponent()),
            node,
            new ExpectedErrorInfo(2 , "xMT127"));
  }

  @Test
  public void resourcePortNameLowercaseTest(){
    ASTMontiArcNode node = loadComponentAST(PACKAGE
            + "." + "ResourcePortLowercase");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new LowerCaseResourcePort()),
            node,
            new ExpectedErrorInfo(2 , "xMT133"));
  }
}
