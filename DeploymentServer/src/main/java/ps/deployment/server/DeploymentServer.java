package ps.deployment.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.common.base.Charsets;

public class DeploymentServer {
  
  public static void main(String[] args) throws Exception {
    MqttClient mqttClient = new MqttClient("tcp://127.0.0.1:1883", "orchestrator");
    mqttClient.connect();
    DeploymentServer ds = new DeploymentServer(mqttClient);
    
    boolean deploy = false;
    String deployOnclientID = "33ab8e3d577b";
    
    if(deploy) {
      ds.deploy(deployOnclientID, "version: \"3.7\"\n"
          + "services:\n"
          + "    app:\n"
          + "        image: hierarchy.temperaturecontroller\n"
          + "        command: --name test --brokerHostname {mqtt_ip} --brokerPort 1883\n"
          + "        network_mode: \"host\"\n"
          + "        restart: always");
    } else {
      ds.deploy(deployOnclientID, null);
    }
  }
  
  private static final Pattern patternClientID = Pattern.compile("deployment\\/([\\w\\d]+)\\/.*");
  
  private final MqttClient mqttClient;
  
  public DeploymentServer(MqttClient mqttClient) throws MqttException {
    this.mqttClient = mqttClient;
    this.prepareMQTT();
  }
  
  private void prepareMQTT() throws MqttException {
    this.mqttClient.subscribe("deployment/+/status", this::onReceivedStatus);
    this.mqttClient.subscribe("deployment/+/heartbeat", this::onReceivedHeartbeat);
    this.mqttClient.publish("deployment/poll", new MqttMessage());
  }
  
  private void onReceivedStatus(String topic, MqttMessage message) throws Exception {
    System.out.println(topic);
  }
  
  private void onReceivedHeartbeat(String topic, MqttMessage message) throws Exception {
    Matcher matcher = patternClientID.matcher(topic);
    if (!matcher.find())
      return;
    String clientID = matcher.group(1);
    System.out.println("received heartbeat from client " + clientID);
  }
  
  private void deploy(String clientID, String ymlCompose) throws MqttPersistenceException, MqttException {
    MqttMessage msg = new MqttMessage();
    if(ymlCompose != null) msg.setPayload(ymlCompose.getBytes(Charsets.UTF_8));
    this.mqttClient.publish("deployment/"+clientID+"/push", msg);
  }
  
}
