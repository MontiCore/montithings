/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import de.monticore.lang.json._parser.JSONParser;

public class SignedBasicDoubleLiteralTest {
  
  @Test
  public void testBookstore() {
    checkDoubleLiteral(89, "89.0");
    checkDoubleLiteral(-89, "-89.0");
    checkDoubleLiteral(82.4, "82.4");
    checkDoubleLiteral(-82.4, "-82.4");
    checkDoubleLiteral(2e+1, "2e+1");
    checkDoubleLiteral(-2e+1, "-2e+1");
    checkDoubleLiteral(43e-3, "43e-3");
    checkDoubleLiteral(-43e-3, "-43e-3");
    checkDoubleLiteral(4e7, "4e7");
    checkDoubleLiteral(-4e7, "-4e7");
    checkDoubleLiteral(6.3e+2, "6.3e+2");
    checkDoubleLiteral(-6.3e+2, "-6.3e+2");
    checkDoubleLiteral(6.3e-2, "6.3e-2");
    checkDoubleLiteral(-6.3e-2, "-6.3e-2");
    checkDoubleLiteral(2E+1, "2E+1");
    checkDoubleLiteral(-2E+1, "-2E+1");
    checkDoubleLiteral(43E-3, "43E-3");
    checkDoubleLiteral(-43E-3, "-43E-3");
    checkDoubleLiteral(4E7, "4E7");
    checkDoubleLiteral(-4E7, "-4E7");
    checkDoubleLiteral(6.3E+2, "6.3E+2");
    checkDoubleLiteral(-6.3E+2, "-6.3E+2");
    checkDoubleLiteral(6.3E-2, "6.3E-2");
    checkDoubleLiteral(-6.3E-2, "-6.3E-2");
  }
  
  /**
   * Parses double literals encoded in Strings and checks their correctness with
   * respect to the expected value.
   * 
   * @param d The expected double value
   * @param s The String value to parse
   * @throws IOException
   */
  private void checkDoubleLiteral(double d, String s) {
    JSONParser parser = new JSONParser();
    Optional<ASTSignedBasicDoubleLiteral> ast;
    try {
      ast = parser.parse_StringSignedBasicDoubleLiteral(s);
      assertTrue(!parser.hasErrors());
      assertEquals(d, ast.get().getValue(), 0);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
