package ps.deployment.server;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.paho.client.mqttv3.MqttClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.distribution.DefaultDistributionCalculator;
import ps.deployment.server.distribution.IDistributionCalculator;
import ps.deployment.server.distribution.IPrologGenerator;
import ps.deployment.server.distribution.RestPrologGenerator;
import ps.deployment.server.distribution.config.DeployConfigGenerator;
import ps.deployment.server.distribution.config.DockerComposeConfig;
import ps.deployment.server.distribution.listener.IDeployStatusListener;

public class ProofOfConcept implements IDeployStatusListener {
  
  public static void main(String[] args) throws Exception {
    String host = "127.0.0.1";
    if(args.length > 0) {
      host = args[0];
    }
    
    NetworkInfo net = new NetworkInfo();
    net.setMqttHost(host);
    net.setDockerRepositoryPrefix(host+":5000/");
    new ProofOfConcept(net);
  }
  
  private final DeploymentServer server;
  
  private final DeploymentInfo deploymentInfo;
  private final File workingDir = new File("tmp");
  private final JsonObject jsonConfig;
  private final NetworkInfo network;
  
  public ProofOfConcept(NetworkInfo network) throws Exception {
    this.network = network;
    
    MqttClient mqttClient = new MqttClient("tcp://"+network.getMqttHost()+":"+network.getMqttPort(), "orchestrator");
    mqttClient.connect();
    this.server = new DeploymentServer(mqttClient, this);
    
    String strDeployJson = "{\"instances\":[{\"componentType\":\"hierarchy.Example\",\"instanceName\":\"hierarchy.Example\",\"requirements\":[]},{\"componentType\":\"hierarchy.Source\",\"instanceName\":\"hierarchy.Example.source\",\"requirements\":[]},{\"componentType\":\"hierarchy.Sink\",\"instanceName\":\"hierarchy.Example.sink\",\"requirements\":[]},{\"componentType\":\"hierarchy.LowPassFilter\",\"instanceName\":\"hierarchy.Example.lpf\",\"requirements\":[]},{\"componentType\":\"hierarchy.Converter\",\"instanceName\":\"hierarchy.Example.c\",\"requirements\":[]},{\"componentType\":\"hierarchy.Doubler\",\"instanceName\":\"hierarchy.Example.d\",\"requirements\":[\"HighPerformanceAdditionComputeUnit\"]}]}";
    
    JsonObject jsonDeploy = JsonParser.parseString(strDeployJson).getAsJsonObject();
    this.deploymentInfo = DeploymentInfo.fromJson(jsonDeploy);
    this.jsonConfig = new DeployConfigGenerator(this.deploymentInfo).generateConfig();
    
    // Shutdown deployment when orchestrator is shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      try {
        for(DeployClient client : this.server.getClients()) {
          this.server.deploy(client.getClientID(), null);
        }
      } catch(Exception e) {}
    }));
  }
  
  private void updateDeployment() {
    System.out.println("Updating deployment...");
    try {
      // Generate Prolog files.
      IPrologGenerator gen = new RestPrologGenerator();
      String plFacts = gen.generateFacts(server.getClients()).get();
      String plQuery = gen.generateQuery(jsonConfig.toString()).get();
      
      // Compute distribution.
      IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
      List<String> instanceNames = this.deploymentInfo.getInstanceNames();
      Distribution dist = calc.computeDistribution(server.getClients(), instanceNames).get();
      
      System.out.println(dist);
      if(dist != null) {
        Map<String,DockerComposeConfig> composes = DockerComposeConfig.fromDistribution(dist, deploymentInfo, network);
        for(Entry<String, DockerComposeConfig> e : composes.entrySet()) {
          server.deploy(e.getKey(), e.getValue().serializeYaml());
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onClientOnline(DeployClient client) {
    this.updateDeployment();
  }
  
  @Override
  public void onClientOffline(DeployClient client) {
    this.updateDeployment();
  }
  
}
