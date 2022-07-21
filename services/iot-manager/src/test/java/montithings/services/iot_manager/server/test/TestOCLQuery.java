package montithings.services.iot_manager.server.test;

import de.se_rwth.commons.logging.Log;
import montithings.services.iot_manager.server.Utils;
import montithings.services.iot_manager.server.distribution.DefaultDistributionCalculator;
import montithings.services.iot_manager.server.distribution.IPrologGenerator;
import montithings.services.iot_manager.server.distribution.RestPrologGenerator;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class TestOCLQuery {
  @Test(timeout = 100_000L)
  public void testRestPrologGenerator() throws Exception {
    String objectDiagram = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/VL53L0X.od")), StandardCharsets.UTF_8);
    String ocl = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/requiredDistanceSensor.ocl")), StandardCharsets.UTF_8);

    IPrologGenerator gen = new RestPrologGenerator();
    String strDeviceDescriptionProlog = gen.generateDeviceDescription(objectDiagram).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(strDeviceDescriptionProlog);
    Log.info(strDeviceDescriptionProlog, "DEVICEDESCRIPTION");

    String strOCLQueryProlog = gen.generateOCLQuery(ocl).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(strOCLQueryProlog);
    Log.info(strOCLQueryProlog, "OCLQUERY");
  }

  /*@Test
  public void testCompatibilityCheck() throws Exception {
    String objectDiagram = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/VL53L0X.od")), StandardCharsets.UTF_8);
    String ocl = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/requiredDistanceSensor.ocl")), StandardCharsets.UTF_8);

    IPrologGenerator gen = new RestPrologGenerator();
    String strDeviceDescriptionProlog = gen.generateDeviceDescription(objectDiagram).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(strDeviceDescriptionProlog);

    String strOCLQueryProlog = gen.generateOCLQuery(ocl).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(strOCLQueryProlog);

    File workingDir = new File("tmp");

    DefaultDistributionCalculator calc = new DefaultDistributionCalculator(strDeviceDescriptionProlog, strOCLQueryProlog, workingDir, true);
    assertTrue(calc.compatibilityCheck());
  }*/
}
