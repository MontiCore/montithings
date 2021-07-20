package ps.deployment.server;

import java.io.File;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import ps.deployment.server.api.HttpAPIController;
import ps.deployment.server.api.MqttAPIController;
import ps.deployment.server.data.NetworkInfo;

public class DeploymentServer {
  
  public static void main(String[] args) {
    File workingDir = new File("tmp");
    NetworkInfo network = new NetworkInfo();
    network.setMqttHost("node4.se.rwth-aachen.de");
    //network.setMqttHost("127.0.0.1");
    network.setMqttPort(1883);
    network.setDockerRepositoryPrefix("registry.git.rwth-aachen.de/monticore/montithings/deployment/ba-schneider/deployexampleapp/");
    
    DeploymentManager manager = new DeploymentManager(workingDir, network);
    try {
      MqttClient mqttClient = new MqttClient(network.getMqttURI(), "orchestrator");
      MqttConnectOptions options = new MqttConnectOptions();
      // allow more messages being sent with QOS>0
      options.setMaxInflight(1_000);
      mqttClient.connect(options);
      manager.setTargetProvider(new DefaultDeployTargetProvider(mqttClient));
    }
    catch (MqttException e1) {
      e1.printStackTrace();
    }
    
    HttpAPIController controllerHttp = new HttpAPIController(manager);
    controllerHttp.start();
    
    MqttAPIController controller = new MqttAPIController(manager);
    controller.start();
    
    
    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      try {
        manager.terminate();
      } catch(Exception e) {}
    }));
  }
  
}
