/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cocoTest;

import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMontiArcNode;
import org.junit.Test;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class ValidTest extends AbstractCoCoTest{

  @Test
  public void checkValidTest(){
    ASTComponent astMontiArcNode = (ASTComponent) loadComponentAST("portTest.PortTest");
    checkValid("portTest.PortTest");
  }
}
