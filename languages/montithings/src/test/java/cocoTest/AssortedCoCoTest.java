// (c) https://github.com/MontiCore/monticore
package cocoTest;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montithings._ast.ASTMontiThingsNode;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings.cocos.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssortedCoCoTest extends AbstractCoCoTest {
  private static final String PACKAGE = "cocoTest";

  private ASTMontiArcNode node;

  @BeforeClass
  public static void setup() {
    Log.enableFailQuick(false);
  }

  @Test
  public void interfaceComponentContentTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "InterfaceComponentWithContent");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new InterfaceComponentContainsOnlyPorts()),
        node,
        new ExpectedErrorInfo(3, "xMT200"));
  }

  @Test
  public void multipleBehaviorTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "MultipleExecutionBlocks");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new MaxOneBehaviorPerComponent()),
        node,
        new ExpectedErrorInfo(1, "xMT110"));
  }

  @Test
  public void defaultValueTypeCheckTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE + "." + "DefaultValueWrongType");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new DefaultValuesCorrectlyAssigned()),
        node, new ExpectedErrorInfo(1, "xMT014"));
  }

  @Test
  public void javaPBehaviorTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "JavaPBehavior");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new NoJavaPBehavior()),
        node,
        new ExpectedErrorInfo(1, "xMT125"));
  }

  @Test
  public void javaImportTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "JavaImport");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new NoJavaImportStatements()),
        node,
        new ExpectedErrorInfo(1, "xMT124"));
  }

  @Test
  public void javaValidImportTest() {
    checkValid(PACKAGE, "JavaImportValid");
  }

  @Test
  public void TimeSyncInAtomicTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "TimeSyncInAtomic");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new TimeSyncOnlyInComposedComponents()),
        node,
        new ExpectedErrorInfo(1, "xMT119"));
  }

  @Test
  public void TimeSyncInSubComponentsTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE +
        "." + "TimeSyncInSubComps");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new TimeSyncInSubComponents()),
        node,
        new ExpectedErrorInfo(2, "xMT120"));
  }

  @Test
  public void resourcePortsInNonDeployTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." + "ResourcePortInNonDeploy");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new ResourcePortsOnlyOnOutermostComponent()),
        node,
        new ExpectedErrorInfo(2, "xMT127"));
  }

  @Test
  public void resourcePortNameLowercaseTest() {
    ASTMontiThingsNode node = loadComponentAST(PACKAGE
        + "." + "ResourcePortLowercase");
    checkInvalid(new MontiThingsCoCoChecker().addCoCo(new LowerCaseResourcePort()),
        node,
        new ExpectedErrorInfo(2, "xMT133"));
  }
}
