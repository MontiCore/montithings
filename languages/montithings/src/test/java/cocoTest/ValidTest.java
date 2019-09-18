/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cocoTest;

import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class ValidTest extends AbstractCoCoTest{

  @BeforeClass
  public static void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void checkValidTest(){
    checkValid("portTest.PortTest");
  }
}
