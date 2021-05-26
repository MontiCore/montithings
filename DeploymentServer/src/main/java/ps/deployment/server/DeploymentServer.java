package ps.deployment.server;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeployClientLocation;
import ps.deployment.server.distribution.listener.IDeployStatusListener;
import ps.deployment.server.distribution.listener.VoidDeployStatusListener;

public class DeploymentServer {
  
  public static void main(String[] args) throws Exception {
    
    String mqttHost = "127.0.0.1";
    
    MqttClient mqttClient = new MqttClient("tcp://" + mqttHost + ":1883", "orchestrator");
    mqttClient.connect();
    DeploymentServer ds = new DeploymentServer(mqttClient);
    
    boolean deploy = false;
    String deployOnclientID = "bfb9531471e8";
    
    if (deploy) {
      ds.deploy(deployOnclientID, "version: \"3.7\"\n" + "services:\n" + "    app:\n" + "        image: hierarchy.temperaturecontroller\n" + "        command: --name test --brokerHostname " + mqttHost + " --brokerPort 1883\n" + "        network_mode: \"host\"\n" + "        restart: always");
    }
    else {
      ds.deploy(deployOnclientID, null);
    }
  }
  
  private static final long CLIENT_TIMEOUT = 15 * 1000L;
  
  private static final Pattern patternClientID = Pattern.compile("deployment\\/([\\w\\d]+)\\/.*");
  
  private final MqttClient mqttClient;
  private final Map<String, DeployClient> clients = new HashMap<>();
  private IDeployStatusListener listener;
  
  public DeploymentServer(MqttClient mqttClient, IDeployStatusListener listener) throws MqttException {
    this.mqttClient = mqttClient;
    this.listener = listener;
    this.prepare();
  }
  
  public DeploymentServer(MqttClient mqttClient) throws MqttException {
    this(mqttClient, new VoidDeployStatusListener());
  }
  
  private void prepare() throws MqttException {
    this.prepareMQTT();
    new Thread(this::runWatchdog).start();
  }
  
  private void prepareMQTT() throws MqttException {
    this.mqttClient.subscribe("deployment/+/status", this::onReceivedStatus);
    this.mqttClient.subscribe("deployment/+/heartbeat", this::onReceivedHeartbeat);
    this.mqttClient.subscribe("deployment/+/config", this::onReceivedConfig);
    
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
  
  private String extractClientID(String topic) {
    Matcher matcher = patternClientID.matcher(topic);
    return matcher.find() ? matcher.group(1) : null;
  }
  
  private void onReceivedStatus(String topic, MqttMessage message) throws Exception {
    String clientID = extractClientID(topic);
    if (clientID == null)
      return; // invalid topic
  }
  
  private void onReceivedConfig(String topic, MqttMessage message) throws Exception {
    String clientID = extractClientID(topic);
    if (clientID == null)
      return; // invalid topic
    
    DeployClient client = clients.get(clientID);
    try {
      // parse JSON payload
      JsonObject jsonPayload = JsonParser.parseString(new String(message.getPayload(), StandardCharsets.UTF_8)).getAsJsonObject();
      JsonObject jLocation = jsonPayload.get("location").getAsJsonObject();
      JsonArray jHardware = jsonPayload.get("hardware").getAsJsonArray();
      
      String building = jLocation.get("building").getAsString();
      String floor = jLocation.get("floor").getAsString();
      String room = jLocation.get("room").getAsString();
      LinkedList<String> hardwareList = new LinkedList<>();
      for (JsonElement jStr : jHardware) {
        hardwareList.add(jStr.getAsString());
      }
      String[] hardware = hardwareList.toArray(new String[hardwareList.size()]);
      
      // update device config
      if (client == null) {
        // This client has not been registered yet. Register it now.
        DeployClientLocation location = new DeployClientLocation();
        location.setBuilding(building);
        location.setFloor(floor);
        location.setRoom(room);
        client = DeployClient.create(clientID, false, location, hardware);
        clients.put(clientID, client);
      }
      else {
        client.getLocation().setBuilding(building);
        client.getLocation().setFloor(floor);
        client.getLocation().setRoom(room);
        client.setHardware(hardware);
      }
    }
    catch (JsonParseException | IllegalStateException e) {
      // Invalid json received. Ignore this message.
      System.err.println("Received invalid client configuration from client \"" + clientID + "\".");
    }
  }
  
  private void onReceivedHeartbeat(String topic, MqttMessage message) throws Exception {
    String clientID = extractClientID(topic);
    if(clientID == null)
      return; // invalid topic
    
    System.out.println("received heartbeat from client " + clientID);
    DeployClient client = clients.get(clientID);
    if (client == null) {
      // This client has not been registered yet. Register it now.
      DeployClientLocation location = new DeployClientLocation();
      location.setBuilding("b01");
      location.setFloor("f02");
      location.setRoom("r03");
      client = DeployClient.create(clientID, false, location);
      clients.put(clientID, client);
    }
    
    client.setLastSeen(System.currentTimeMillis());
    if (!client.isOnline()) {
      client.setOnline(true);
      this.listener.onClientOnline(client);
    }
  }
  
  public void deploy(String clientID, String ymlCompose) throws MqttPersistenceException, MqttException {
    MqttMessage msg = new MqttMessage();
    if (ymlCompose != null)
      msg.setPayload(ymlCompose.getBytes(Charsets.UTF_8));
    this.mqttClient.publish("deployment/" + clientID + "/push", msg);
  }
  
  public Collection<DeployClient> getClients() {
    return this.clients.values();
  }
  
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }
  
}
