// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import montithings.services.iot_manager.server.api.HttpAPIController;
import montithings.services.iot_manager.server.api.MqttAPIController;
import montithings.services.iot_manager.server.data.NetworkInfo;

import java.io.File;

public class DeploymentServer {
  
  /**
   * Main entry point for deployment server.
   * Starts a {@link DeploymentManager} with HTTP & MQTT API.
   * */
  public static void main(String[] args) {
    String mqttHost = "";
    if(args.length == 0) {
      System.err.println("Missing required argument: MQTT Broker Hostname");
      System.exit(1);
    } else {
      mqttHost = args[0];
    }
    
    System.out.println("DeploymentServer is starting...");
    
    File workingDir = new File("tmp");
    NetworkInfo network = new NetworkInfo();
    network.setMqttHost(mqttHost);
    network.setMqttPort(1883);
    network.setDockerRepositoryPrefix("registry.git.rwth-aachen.de/monticore/montithings/deployment/ba-schneider/deployexampleapp/");
    
    DeploymentManager manager = new DeploymentManager(workingDir, network);
    
    System.out.println("Starting HTTP API controller...");
    HttpAPIController controllerHttp = new HttpAPIController(manager);
    controllerHttp.start();
    
    System.out.println("Starting MQTT API controller...");
    MqttAPIController controller = new MqttAPIController(manager);
    controller.start();
    
    // Shut down deployment after termination request.
    // Note: IDEs often just kill the process, so this might not be called in
    // your development environment.
    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      try {
        manager.terminate();
      } catch(Exception e) {}
    }));
    
    System.out.println("Successfully started.");
    
  }
  
}
