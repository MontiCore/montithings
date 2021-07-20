package ps.deployment.server.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ps.deployment.server.Utils;
import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.LocationSpecifier;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.distribution.DefaultDistributionCalculator;
import ps.deployment.server.distribution.IDistributionCalculator;
import ps.deployment.server.distribution.IPrologGenerator;
import ps.deployment.server.distribution.RestPrologGenerator;
import ps.deployment.server.distribution.config.DeployConfigGenerator;
import ps.deployment.server.distribution.config.DockerComposeConfig;
import ps.deployment.server.distribution.config.DockerComposeService;

public class TestDistribution {
  
  @Test(timeout = 10_000L)
  public void testDefaultDistributionCalculator() throws IOException {
    File workingDir = new File("tmp");
    String plFacts = new String(Utils.readAllBytes(getClass().getResourceAsStream("/scripts/ex1_facts.pl")), StandardCharsets.UTF_8);
    String plQuery = new String(Utils.readAllBytes(getClass().getResourceAsStream("/scripts/ex1_query.pl")), StandardCharsets.UTF_8);
    
    IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
    List<String> components = Lists.newArrayList("RoomTempSensor", "RoomTempController");
    
    List<DeployClient> targets = new ArrayList<>();
    
    // add clients from example facts.pl
    for (int building = 1; building <= 3; building++) {
      for (int floor = 1; floor <= 3; floor++) {
        for (int room = 301; room <= 303; room++) {
          LocationSpecifier loc = LocationSpecifier.create(String.valueOf(building), String.valueOf(floor), String.valueOf(room));
          targets.add(DeployClient.create("raspy_b"+building+"_f"+floor+"_temp_"+(room-300), true, loc));
          targets.add(DeployClient.create("raspy_b"+building+"_f"+floor+"_controller_"+(room-300), true, loc));
        }
      }
    }
    
    try {
      Distribution dist = calc.computeDistribution(targets, components).exceptionally((t) -> {
        t.printStackTrace();
        return null;
      }).get();
      assertNotNull(dist);
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
  
  @Test(timeout = 10_000L)
  public void testRestPrologGenerator() throws Exception {
    LinkedList<DeployClient> clients = new LinkedList<>();
    clients.add(DeployClient.create("2fa84e32", true, LocationSpecifier.create("1", "1", "101"), "sensor_temperature"));
    clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101"), "heat_controller"));
    
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
    JsonObject config = new DeployConfigGenerator(deployment).generateConfig();
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
    clients.add(DeployClient.create("2fa84e32", true, LocationSpecifier.create("1", "1", "101"), "HighPerformanceAdditionComputeUnit"));
    clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101")));
    
    JsonObject jsonDeploy = null;
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/json/deployment-info.json"))) {
      jsonDeploy = JsonParser.parseReader(reader).getAsJsonObject();
    }
    
    DeploymentInfo deployment = DeploymentInfo.fromJson(jsonDeploy);
    JsonObject config = new DeployConfigGenerator(deployment).generateConfig();
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
    
    // Compute distribution.
    
    IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
    List<String> instanceNames = deployment.getInstanceNames();
    Distribution dist = calc.computeDistribution(clients, instanceNames).exceptionally((t) -> {
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
  public void testDockerCompose_serialization() {
    // example data
    DockerComposeConfig config = new DockerComposeConfig();
    config.addService("testapp1", new DockerComposeService("test.image1:latest", "testInstance1", "0.0.0.0", 1234));
    config.addService("testapp2", new DockerComposeService("test.image2:latest", "testInstance2", "0.0.0.0", 1234));
    System.out.println(config.serializeYaml());
  }
  
}
