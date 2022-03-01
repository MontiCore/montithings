// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import montithings.services.iot_manager.server.Utils;
import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint;
import montithings.services.iot_manager.server.data.constraint.Constraint;
import montithings.services.iot_manager.server.distribution.*;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;
import montithings.services.iot_manager.server.distribution.suggestion.Suggestion;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class TestSuggestion {
  
  @Test(timeout = 10_000L)
  public void testSuggestion() throws IOException {
    File workingDir = new File("tmp");
    String plFacts = new String(Utils.readAllBytes(getClass().getResourceAsStream("/scripts/ex2_facts.pl")), StandardCharsets.UTF_8);
    String plQuery = new String(Utils.readAllBytes(getClass().getResourceAsStream("/scripts/ex2_query.pl")), StandardCharsets.UTF_8);
    
    IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
    List<String> components = Lists.newArrayList("RoomTempSensor", "RoomTempController");
    
    List<DeployClient> targets = new ArrayList<>();
    
    // add clients from example facts.pl
    for (int building = 1; building <= 3; building++) {
      for (int floor = 1; floor <= 3; floor++) {
        for (int room = 301; room <= 303; room++) {
          LocationSpecifier loc = LocationSpecifier.create(String.valueOf(building), String.valueOf(floor), String.valueOf(room));
          targets.add(DeployClient.create("raspy_b"+building+"_f"+floor+"_temp_"+(room-300), true, loc, 0));
          targets.add(DeployClient.create("raspy_b"+building+"_f"+floor+"_controller_"+(room-300), true, loc, 0));
        }
      }
    }
    
    try {
      Map<Distribution, List<Suggestion>> dist = calc.computeDistributionSuggestion(new DistributionSuggestionRequest(targets, components, 0, 10)).exceptionally((t) -> {
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
  public void testFullCalculation() throws Exception {
    
    // example data
    LinkedList<DeployClient> clients = new LinkedList<>();
    clients.add(DeployClient.create("2fa84e32", true, LocationSpecifier.create("1", "1", "101"), 0, "HighPerformanceAdditionComputeUnit"));
    clients.add(DeployClient.create("713fa127", true, LocationSpecifier.create("1", "1", "101"), 0));
    
    JsonObject jsonDeploy = null;
    try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/json/deployment-info.json"))) {
      jsonDeploy = JsonParser.parseReader(reader).getAsJsonObject();
    }
    
    DeploymentInfo deployment = DeploymentInfo.fromJson(jsonDeploy);
    DeploymentConfiguration deployConfiguration = new DeploymentConfiguration();
    deployConfiguration.setDeploymentInfo(deployment);
    
    List<Constraint> constraints = new ArrayList<Constraint>();
    constraints.add(new BasicConstraint("hierarchy.Example.sink", BasicConstraint.Type.GREATER_EQUAL, 3, "1", "1", "101"));
    constraints.add(new BasicConstraint("hierarchy.Example", BasicConstraint.Type.EQUALS, 3, "1", "1", "101"));
    deployConfiguration.setConstraints(constraints);
    
    DeployConfigBuilder cBuilder = new DeployConfigBuilder(deployConfiguration);
    constraints.stream().forEach(c->c.applyConstraint(cBuilder));
    JsonObject config = cBuilder.build();
    
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
    DistributionSuggestionRequest req = new DistributionSuggestionRequest(clients, instanceNames, 0, 10);
    Map<Distribution, List<Suggestion>> dist = calc.computeDistributionSuggestion(req).exceptionally((t) -> {
      t.printStackTrace();
      fail();
      return null;
    }).get();
    
    Iterator<Entry<Distribution, List<Suggestion>>> it = dist.entrySet().iterator();
    assertTrue(it.hasNext());
    
    Entry<Distribution, List<Suggestion>> firstSolution = it.next();
    List<Suggestion> suggestions = firstSolution.getValue();
    DeploymentConfiguration conf = deployConfiguration.clone();
    
    for(Suggestion sugg : suggestions) {
      sugg.applyTo(conf);
    }
    System.out.println(deployConfiguration);
    System.out.println(conf);
  }
  
}
