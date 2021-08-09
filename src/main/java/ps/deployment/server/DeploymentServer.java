package ps.deployment.server;

import java.io.File;
import ps.deployment.server.api.HttpAPIController;
import ps.deployment.server.api.MqttAPIController;
import ps.deployment.server.data.NetworkInfo;

public class DeploymentServer {
  
  public static void main(String[] args) {
    System.out.println("DeploymentServer is starting...");
    
    File workingDir = new File("tmp");
    NetworkInfo network = new NetworkInfo();
    network.setMqttHost("node4.se.rwth-aachen.de");
    network.setMqttPort(1883);
    network.setDockerRepositoryPrefix("registry.git.rwth-aachen.de/monticore/montithings/deployment/ba-schneider/deployexampleapp/");
    
    DeploymentManager manager = new DeploymentManager(workingDir, network);
    
    System.out.println("Starting HTTP API controller...");
    HttpAPIController controllerHttp = new HttpAPIController(manager);
    controllerHttp.start();
    
    System.out.println("Starting MQTT API controller...");
    MqttAPIController controller = new MqttAPIController(manager);
    controller.start();
    
    
    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      try {
        manager.terminate();
      } catch(Exception e) {}
    }));
    
    System.out.println("Successfully started.");
    
  }
  
}
