/* (c) https://github.com/MontiCore/monticore */
package montithings.services.prolog_generator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.ocl._parser.OCLParser;
import de.se_rwth.commons.logging.Log;
import montithings.services.prolog_generator.oclquery.generator.OCLToPrologConverter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenerateOCLQueryTest {
  @Test
  public void testParseOCLQuery() throws IOException {
    Path model = Paths.get("src/test/resources/iot-config/oclexpression.ocl");
    OCLParser parser = new OCLParser();

    Optional<ASTExpression> ocl = parser.parseExpression(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(ocl.isPresent());
  }


  @Test
  public void testGenerateOCLQuery() throws IOException {
    Path model = Paths.get("src/test/resources/iot-config/oclexpression.ocl");
    OCLParser parser = new OCLParser();

    Optional<ASTExpression> ocl = parser.parseExpression(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(ocl.isPresent());

    String query = OCLToPrologConverter.generateOCLQuery(ocl.get(), "TEST");
    Log.info(query, "OUTPUT");
  }
}
