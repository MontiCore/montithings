// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import montithings.services.iot_manager.server.Utils;
import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.distribution.*;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;
import montithings.services.iot_manager.server.distribution.config.DockerComposeConfig;
import montithings.services.iot_manager.server.distribution.config.DockerComposeService;
import montithings.services.iot_manager.server.exception.DeploymentException;
import org.junit.Test;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;

public class TestDistribution {
  
  @Test(timeout = 10_000L)
  public void testRestPrologGenerator() throws Exception {
    LinkedList<DeployClient> clients = new LinkedList<>();
    clients.add(DeployClient.create("vL53L0X", true, LocationSpecifier.create("1", "1", "101"), 0, "", "sensor_temperature"));
    clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101"), 0, "", "heat_controller"));
    
    String jsonConfig = new String(Utils.readAllBytes(getClass().getResourceAsStream("/scripts/ex1_config.json")), StandardCharsets.UTF_8);
    
    IPrologGenerator gen = new RestPrologGenerator();
    String strFactsProlog = gen.generateFacts(clients).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(strFactsProlog);
    
    String strQueryProlog = gen.generateQuery(jsonConfig).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(strQueryProlog);
  }
  
  @Test
  public void testConfigGeneration() throws Exception {
    JsonObject jsonDeploy = null;
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/json/deployment-info.json"))) {
      jsonDeploy = JsonParser.parseReader(reader).getAsJsonObject();
    }
    
    DeploymentInfo deployment = DeploymentInfo.fromJson(jsonDeploy);
    DeploymentConfiguration conf = new DeploymentConfiguration();
    conf.setDeploymentInfo(deployment);
    conf.setConstraints(new ArrayList<>(0));
    JsonObject config = new DeployConfigBuilder(conf).build();
    assertNotNull(config);
    
    IPrologGenerator gen = new RestPrologGenerator();
    String query = gen.generateQuery(config.toString()).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(query);
  }
  
  @Test
  public void testFullCalculation() throws Exception {
    
    // example data
    LinkedList<DeployClient> clients = new LinkedList<>();
    clients.add(DeployClient.create("vL53L0X", true, LocationSpecifier.create("1", "1", "101"), 0, "", "HighPerformanceAdditionComputeUnit"));
    clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101"), 0, ""));
    
    JsonObject jsonDeploy = null;
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/json/deployment-info.json"))) {
      jsonDeploy = JsonParser.parseReader(reader).getAsJsonObject();
    }
    
    DeploymentInfo deployment = DeploymentInfo.fromJson(jsonDeploy);
    DeploymentConfiguration conf = new DeploymentConfiguration();
    conf.setDeploymentInfo(deployment);
    conf.setConstraints(new ArrayList<>(0));
    JsonObject config = new DeployConfigBuilder(conf).build();
    assertNotNull(config);
    
    File workingDir = new File("tmp");
    
    // Generate Prolog files.
    
    IPrologGenerator gen = new RestPrologGenerator();
    String plFacts = gen.generateFacts(clients).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(plFacts);
    
    String plQuery = gen.generateQuery(config.toString()).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    assertNotNull(plQuery);

    //generate OCL queries in prolog
    Map<String, String> hardwareRequirements = new HashMap<>();
    hardwareRequirements.put("HierarchyExampleC", "exists DistanceSensor sensor:\n" +
      "  let max = sensor.range.max; min = sensor.range.min in\n" +
      "    max > min implies max - min > 1000\n" +
      "  &&\n" +
      "  sensor.accuracy.percent < 10\n");
    Map<String, String> plOCLQueries = new HashMap<>();
    for (String instanceName : hardwareRequirements.keySet()) {
      String plOCLQuery = gen.generateOCLQuery(instanceName + ":" + hardwareRequirements.get(instanceName)).exceptionally((t) -> {
        return null;
      }).get();
      if (plOCLQuery == null) {
        throw new DeploymentException("Could not generate Prolog OCL query");
      }
      plOCLQueries.put(instanceName, plOCLQuery);
    }

    //generate devicedescriptions in prolog
    Map<String, String> deviceDescriptions = new HashMap<>();
    String objectDiagram = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/VL53L0X.od")), StandardCharsets.UTF_8);
    deviceDescriptions.put("VL53L0X", objectDiagram);
    Map<String, String> plDeviceDescriptions = new HashMap<>();
    for (String name : deviceDescriptions.keySet()) {
      String plDeviceDescription = gen.generateDeviceDescription(deviceDescriptions.get(name)).exceptionally((t) -> {
        return null;
      }).get();
      if (plDeviceDescription == null) {
        throw new DeploymentException("Could not generate Prolog Device Description");
      }
      plDeviceDescriptions.put(name, plDeviceDescription);
    }

    // Compute distribution.
    IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, plOCLQueries, plDeviceDescriptions, workingDir);
    List<String> instanceNames = deployment.getInstanceNames();
    Distribution dist = calc.computeDistribution(new DistributionCalcRequest(clients, instanceNames)).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    
    System.out.println(dist);
    
    // Generate docker compose files.
    
    Map<String,DockerComposeConfig> composes = DockerComposeConfig.fromDistribution(dist, deployment, new NetworkInfo());
    for(Entry<String, DockerComposeConfig> e : composes.entrySet()) {
      System.out.println(e.getKey()+": "+e.getValue().serializeYaml());
    }
  }
  
  @Test
  public void testPersistence() throws Exception {
    
    // example data
    JsonObject jsonDeploy = null;
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/json/deployment-info.json"))) {
      jsonDeploy = JsonParser.parseReader(reader).getAsJsonObject();
    }
    DeploymentInfo deployment = DeploymentInfo.fromJson(jsonDeploy);
    DeploymentConfiguration conf = new DeploymentConfiguration();
    conf.setDeploymentInfo(deployment);
    conf.setConstraints(new ArrayList<>(0));
    JsonObject config = new DeployConfigBuilder(conf).build();
    assertNotNull(config);
    
    File workingDir = new File("tmp");
    
    Distribution dist1, dist2;
    
    { // first calculation
      LinkedList<DeployClient> clients = new LinkedList<>();
      clients.add(DeployClient.create("vL53L0X", true, LocationSpecifier.create("1", "1", "101"), 0, "", "HighPerformanceAdditionComputeUnit"));
      clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101"), 0, ""));
      
      // Generate Prolog files.
      
      IPrologGenerator gen = new RestPrologGenerator();
      String plFacts = gen.generateFacts(clients).exceptionally((t) -> {
        t.printStackTrace();
        fail();
        return null;
      }).get();
      assertNotNull(plFacts);
      
      String plQuery = gen.generateQuery(config.toString()).exceptionally((t) -> {
        t.printStackTrace();
        fail();
        return null;
      }).get();
      assertNotNull(plQuery);

      //generate OCL queries in prolog
      Map<String, String> hardwareRequirements = new HashMap<>();
      hardwareRequirements.put("HierarchyExampleC", "exists DistanceSensor sensor:\n" +
        "  let max = sensor.range.max; min = sensor.range.min in\n" +
        "    max > min implies max - min > 1000\n" +
        "  &&\n" +
        "  sensor.accuracy.percent < 10\n");
      Map<String, String> plOCLQueries = new HashMap<>();
      for (String instanceName : hardwareRequirements.keySet()) {
        String plOCLQuery = gen.generateOCLQuery(instanceName + ":" + hardwareRequirements.get(instanceName)).exceptionally((t) -> {
          return null;
        }).get();
        if (plOCLQuery == null) {
          throw new DeploymentException("Could not generate Prolog OCL query");
        }
        plOCLQueries.put(instanceName, plOCLQuery);
      }

      //generate devicedescriptions in prolog
      Map<String, String> deviceDescriptions = new HashMap<>();
      String objectDiagram = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/VL53L0X.od")), StandardCharsets.UTF_8);
      deviceDescriptions.put("VL53L0X", objectDiagram);
      Map<String, String> plDeviceDescriptions = new HashMap<>();
      for (String name : deviceDescriptions.keySet()) {
        String plDeviceDescription = gen.generateDeviceDescription(deviceDescriptions.get(name)).exceptionally((t) -> {
          return null;
        }).get();
        if (plDeviceDescription == null) {
          throw new DeploymentException("Could not generate Prolog Device Description");
        }
        plDeviceDescriptions.put(name, plDeviceDescription);
      }
      
      // Compute distribution.
      List<String> instanceNames = deployment.getInstanceNames();
      DistributionCalcRequest req = new DistributionCalcRequest(clients, instanceNames);
      IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, plOCLQueries, plDeviceDescriptions, workingDir);
      dist1 = calc.computeDistribution(req).exceptionally((t) -> {
        t.printStackTrace();
        fail();
        return null;
      }).get();
    }
    
    { // second calculation
      LinkedList<DeployClient> clients = new LinkedList<>();
      clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101"), 0, ""));
      clients.add(DeployClient.create("vL53L0X", true, LocationSpecifier.create("1", "1", "101"), 0, "", "HighPerformanceAdditionComputeUnit"));
      
      // Generate Prolog files.
      
      IPrologGenerator gen = new RestPrologGenerator();
      String plFacts = gen.generateFacts(clients).exceptionally((t) -> {
        t.printStackTrace();
        fail();
        return null;
      }).get();
      assertNotNull(plFacts);
      
      String plQuery = gen.generateQuery(config.toString()).exceptionally((t) -> {
        t.printStackTrace();
        fail();
        return null;
      }).get();
      assertNotNull(plQuery);

      //generate OCL queries in prolog
      Map<String, String> hardwareRequirements = new HashMap<>();
      hardwareRequirements.put("HierarchyExampleC", "exists DistanceSensor sensor:\n" +
        "  let max = sensor.range.max; min = sensor.range.min in\n" +
        "    max > min implies max - min > 1000\n" +
        "  &&\n" +
        "  sensor.accuracy.percent < 10\n");
      Map<String, String> plOCLQueries = new HashMap<>();
      for (String instanceName : hardwareRequirements.keySet()) {
        String plOCLQuery = gen.generateOCLQuery(instanceName + ":" + hardwareRequirements.get(instanceName)).exceptionally((t) -> {
          return null;
        }).get();
        if (plOCLQuery == null) {
          throw new DeploymentException("Could not generate Prolog OCL query");
        }
        plOCLQueries.put(instanceName, plOCLQuery);
      }

      //generate devicedescriptions in prolog
      Map<String, String> deviceDescriptions = new HashMap<>();
      String objectDiagram = new String(Utils.readAllBytes(getClass().getResourceAsStream("/ocl/VL53L0X.od")), StandardCharsets.UTF_8);
      deviceDescriptions.put("VL53L0X", objectDiagram);
      Map<String, String> plDeviceDescriptions = new HashMap<>();
      for (String name : deviceDescriptions.keySet()) {
        String plDeviceDescription = gen.generateDeviceDescription(deviceDescriptions.get(name)).exceptionally((t) -> {
          return null;
        }).get();
        if (plDeviceDescription == null) {
          throw new DeploymentException("Could not generate Prolog Device Description");
        }
        plDeviceDescriptions.put(name, plDeviceDescription);
      }
      
      // Compute distribution.
      List<String> instanceNames = deployment.getInstanceNames();
      DistributionCalcRequest req = new DistributionCalcRequest(clients, instanceNames);
      req.setReferenceDistribution(dist1);
      
      IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, plOCLQueries, plDeviceDescriptions, workingDir);
      dist2 = calc.computeDistribution(req).exceptionally((t) -> {
        t.printStackTrace();
        fail();
        return null;
      }).get();
    }
    
    // validate results
    System.out.println(dist1);
    System.out.println(dist2);
    assertTrue(dist1.toString().equals(dist2.toString()));
  }
  
  @Test
  public void testDockerCompose_serialization() {
    // example data
    DockerComposeConfig config = new DockerComposeConfig();
    config.addService("testapp1", new DockerComposeService("test.image1:latest", "testInstance1", "0.0.0.0", 1234));
    config.addService("testapp2", new DockerComposeService("test.image2:latest", "testInstance2", "0.0.0.0", 1234));
    System.out.println(config.serializeYaml());
  }
  
}
