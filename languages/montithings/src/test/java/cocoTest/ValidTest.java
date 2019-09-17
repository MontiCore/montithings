/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cocoTest;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMontiArcNode;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class ValidTest extends AbstractCoCoTest{

  public static final String PACKAGE = "cocoTest";

  @BeforeClass
  public static void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void checkValidTest(){
    ASTComponent astMontiArcNode = (ASTComponent) loadComponentAST("portTest.PortTest");
    checkValid("portTest.PortTest");
  }
}
