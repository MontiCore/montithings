package ps.deployment.server;

import java.io.File;
import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import ps.deployment.server.api.HttpAPIController;
import ps.deployment.server.api.MqttAPIController;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.k8s.K8sDeployTargetProvider;

public class DeploymentServer {
  
  public static void main(String[] args) {
    System.out.println("DeploymentServer is starting...");
    
    File workingDir = new File("tmp");
    NetworkInfo network = new NetworkInfo();
    network.setMqttHost("node4.se.rwth-aachen.de");
    //network.setMqttHost("127.0.0.1");
    network.setMqttPort(1883);
    network.setDockerRepositoryPrefix("registry.git.rwth-aachen.de/monticore/montithings/deployment/ba-schneider/deployexampleapp/");
    
    DeploymentManager manager = new DeploymentManager(workingDir, network);
    /*try {
      System.out.println("Connecting to MQTT broker...");
      MqttClient mqttClient = new MqttClient(network.getMqttURI(), "orchestrator");
      MqttConnectOptions options = new MqttConnectOptions();
      // allow more messages being sent with QOS>0
      options.setMaxInflight(1_000);
      options.setAutomaticReconnect(true);
      mqttClient.connect(options);
      //manager.setTargetProvider(new DefaultDeployTargetProvider(mqttClient));
      try {
        String hostURL = "https://localhost:6443";
        String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImpvRXFtY2Q3ZHJ3Q001OElWNXI1ME1vRElrUmw5eElFd0dCMk83a3VFMkkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImlvdC10b2tlbi1jOXJyaCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJpb3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI1ZmY3Zjc5Zi0xNjQxLTRiYTctOGZjNC03MDIzMDVkNWEzZjEiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDppb3QifQ.STCH2LLFlyhTXd5mA1IppKW2-mYUBpwNdQm3QxEP3qgMKJNK0vBJqgD26861CqFo8UwHAom4ZBj3a_jnvazVakYLWUz-qR-9Wzvco8li3yNfZWRCZR5QAUUX2drnajtLIf8-CErw282Y4UPZrGrSWKBJfYOG_CMW_ZVwDE1aAQFw9nfDFj2TZb7CyL5WccoUVKptqsJYkUqLjcV0d3rzuFyquXMdCWVp5tvyys8KU1f3he8uuLYYXGJhqZ3OziVmULX0SA1dWU7VJ9sFjJknsyfl_Q8X0HQ6Lb9j_QkUOPh_PntrVR56Oyw6-C5KBQRx3p-wVyvjLfeUUuQQgtlwfw";
        PoolDeployTargetProvider target = new PoolDeployTargetProvider();
        target.addProvider(new K8sDeployTargetProvider(hostURL, token));
        target.addProvider(new DefaultDeployTargetProvider(mqttClient));
        manager.setTargetProvider(target);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    catch (MqttException e1) {
      e1.printStackTrace();
    }*/
    
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
