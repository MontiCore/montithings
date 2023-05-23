package montithings.services.openapi_generator;

import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.se_rwth.commons.logging.Log;
import montithings.services.openapi_generator.openapi.generator.OpenAPIToMTConverter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OpenAPIToMTConverterTest {
  @Test
  public void testParseOpenAPIDocument() throws IOException {
    Path model = Paths.get("src/test/resources/openapi/SmartDoor.json");
    JSONParser parser = new JSONParser();


    Optional<ASTJSONDocument> document = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(document.isPresent());

  }

  @Test
  public void testGenerateMTModel() throws IOException {
    Path model = Paths.get("src/test/resources/openapi/SmartDoor.json");
    JSONParser parser = new JSONParser();


    Optional<ASTJSONDocument> document = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(document.isPresent());


    String test = OpenAPIToMTConverter.generateMTModel(document.get().getJSONValue());
    Log.info(test, "OUTPUT");
  }
}
