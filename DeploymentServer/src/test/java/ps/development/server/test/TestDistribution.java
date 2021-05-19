package ps.development.server.test;

import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.google.common.collect.Lists;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeployClientLocation;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.distribution.DefaultDistributionCalculator;
import ps.deployment.server.distribution.IDistributionCalculator;
import ps.deployment.server.distribution.IPrologGenerator;
import ps.deployment.server.distribution.RestPrologGenerator;

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
    String strFactsProlog = gen.generateFacts(clients).exceptionally((t)->{
      t.printStackTrace();
      return null;
    }).get();
    assertNotNull(strFactsProlog);
    
    String strQueryProlog = gen.generateQuery(jsonConfig).exceptionally((t)->{
      t.printStackTrace();
      return null;
    }).get();
    assertNotNull(strQueryProlog);
  }
  
}
