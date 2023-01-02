package montithings.services.prolog_generator;

import de.se_rwth.commons.logging.Log;
import montithings.services.prolog_generator.devicedescription._parser.DeviceDescriptionParser;
import montithings.services.prolog_generator.devicedescription._ast.ASTDevice;
import montithings.services.prolog_generator.devicedescription.generator.ObjectDiagramToPrologConverter;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenerateDeviceDescriptionTest {
  @Test
  public void testParseDeviceDescription() throws IOException {
    Path model = Paths.get("src/test/resources/iot-config/devicedescription.od");
    DeviceDescriptionParser parser = new DeviceDescriptionParser();


    Optional<ASTDevice> od = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(od.isPresent());

  }

  @Test
  public void testGenerateDeviceDescription() throws IOException {
    Path model = Paths.get("src/test/resources/iot-config/VL53L0X.od");
    DeviceDescriptionParser parser = new DeviceDescriptionParser();


    Optional<ASTDevice> od = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(od.isPresent());


    String test = ObjectDiagramToPrologConverter.generateFacts(od.get().getObjectDiagram());
    Log.info(test, "OUTPUT");
  }
}
