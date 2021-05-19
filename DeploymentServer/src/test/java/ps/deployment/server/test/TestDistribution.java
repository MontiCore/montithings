package ps.deployment.server.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeployClientLocation;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.distribution.DefaultDistributionCalculator;
import ps.deployment.server.distribution.IDistributionCalculator;
import ps.deployment.server.distribution.IPrologGenerator;
import ps.deployment.server.distribution.RestPrologGenerator;
import ps.deployment.server.distribution.config.DeployConfigGenerator;

public class TestDistribution {
  
  @Test(timeout = 10_000L)
  public void testDefaultDistributionCalculator() throws IOException {
    File workingDir = new File("tmp");
    String plFacts = new String(getClass().getResourceAsStream("/scripts/ex1_facts.pl").readAllBytes(), StandardCharsets.UTF_8);
    String plQuery = new String(getClass().getResourceAsStream("/scripts/ex1_query.pl").readAllBytes(), StandardCharsets.UTF_8);
    
    IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
    List<String> components = Lists.newArrayList("RoomTempSensor", "RoomTempController");
    
    try {
      Distribution dist = calc.computeDistribution(null, components).exceptionally((t) -> {
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
    clients.add(DeployClient.create("2fa84e32", true, DeployClientLocation.create("1", "1", "101"), "sensor_temperature"));
    clients.add(DeployClient.create("713fa127", true, DeployClientLocation.create("1", "1", "101"), "heat_controller"));
    
    String jsonConfig = new String(getClass().getResourceAsStream("/scripts/ex1_config.json").readAllBytes(), StandardCharsets.UTF_8);
    
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
  }
  
  @Test
  public void testFullCalculation() throws Exception {
    
    // example data
    LinkedList<DeployClient> clients = new LinkedList<>();
    clients.add(DeployClient.create("2fa84e32", true, DeployClientLocation.create("1", "1", "101"), "HighPerformanceAdditionComputeUnit"));
    clients.add(DeployClient.create("713fa127", true, DeployClientLocation.create("1", "1", "101")));
    
    JsonObject jsonDeploy = null;
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/json/deployment-info.json"))) {
      jsonDeploy = JsonParser.parseReader(reader).getAsJsonObject();
    }
    
    DeploymentInfo deployment = DeploymentInfo.fromJson(jsonDeploy);
    JsonObject config = new DeployConfigGenerator(deployment).generateConfig();
    assertNotNull(config);
    
    File workingDir = new File("./tmp/");
    
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
    
    IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
    List<String> components = deployment.getComponentTypes();
    Distribution dist = calc.computeDistribution(clients, components).exceptionally((t)->{
      t.printStackTrace();
      fail();
      return null;
    }).get();
    
    System.out.println(dist);
  }
  
  
  
  
  
  
}
