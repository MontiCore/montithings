package ps.deployment.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.common.base.Charsets;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.listener.IDeployStatusListener;
import ps.deployment.server.data.listener.VoidDeployStatusListener;

public class DeploymentServer {
  
  public static void main(String[] args) throws Exception {
    MqttClient mqttClient = new MqttClient("tcp://127.0.0.1:1883", "orchestrator");
    mqttClient.connect();
    DeploymentServer ds = new DeploymentServer(mqttClient);
    
    boolean deploy = false;
    String deployOnclientID = "bfb9531471e8";
    
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
  
  
  
  
  private static final long CLIENT_TIMEOUT = 15 * 1000L;
  
  private static final Pattern patternClientID = Pattern.compile("deployment\\/([\\w\\d]+)\\/.*");
  
  private final MqttClient mqttClient;
  private final Map<String, DeployClient> clients = new HashMap<>();
  private IDeployStatusListener listener = new VoidDeployStatusListener();
  
  public DeploymentServer(MqttClient mqttClient) throws MqttException {
    this.mqttClient = mqttClient;
    this.prepare();
  }
  
  private void prepare() throws MqttException {
    this.prepareMQTT();
    new Thread(this::runWatchdog).start();
  }
  
  private void prepareMQTT() throws MqttException {
    this.mqttClient.subscribe("deployment/+/status", this::onReceivedStatus);
    this.mqttClient.subscribe("deployment/+/heartbeat", this::onReceivedHeartbeat);
    
    // request current status from all active clients
    this.mqttClient.publish("deployment/poll", new MqttMessage());
  }
  
  /**
   * Starts the Watchdog for this {@link DeploymentServer}. Detects timeouts of
   * clients.
   */
  private void runWatchdog() {
    try {
      while (true) {
        Thread.sleep(CLIENT_TIMEOUT);
        long now = System.currentTimeMillis();
        
        for (DeployClient client : clients.values()) {
          if (client.getLastSeen() + CLIENT_TIMEOUT < now) {
            // The client has not sent a heart-beat for too long and is now
            // considered offline.
            if (client.isOnline()) {
              client.setOnline(false);
              this.listener.onClientOffline(client);
            }
          }
        }
      }
    }
    catch (InterruptedException e) {
      System.err.println("Watchdog timer failed: " + e.getMessage());
    }
  }
  
  private void onReceivedStatus(String topic, MqttMessage message) throws Exception {
    Matcher matcher = patternClientID.matcher(topic);
    if (!matcher.find())
      return;
    String clientID = matcher.group(1);
  }
  
  private void onReceivedHeartbeat(String topic, MqttMessage message) throws Exception {
    Matcher matcher = patternClientID.matcher(topic);
    if (!matcher.find())
      return;
    String clientID = matcher.group(1);
    System.out.println("received heartbeat from client " + clientID);
    DeployClient client = clients.get(clientID);
    if (client == null) {
      // This client has not been registered yet. Register it now.
      client = new DeployClient();
      client.setClientID(clientID);
      clients.put(clientID, client);
    }
    
    client.setLastSeen(System.currentTimeMillis());
    if(!client.isOnline()) {
      this.listener.onClientOnline(client);
      client.setOnline(true);
    }
  }
  
  public void deploy(String clientID, String ymlCompose) throws MqttPersistenceException, MqttException {
    MqttMessage msg = new MqttMessage();
    if (ymlCompose != null)
      msg.setPayload(ymlCompose.getBytes(Charsets.UTF_8));
    this.mqttClient.publish("deployment/" + clientID + "/push", msg);
  }
  
}
