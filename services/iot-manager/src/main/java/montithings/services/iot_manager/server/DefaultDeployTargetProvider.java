// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import com.google.common.base.Charsets;
import com.google.gson.*;
import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.distribution.config.DockerComposeConfig;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.distribution.listener.VoidDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultDeployTargetProvider implements IDeployTargetProvider {
  
  private static final long CLIENT_TIMEOUT = 20 * 1000L;
  
  private static final Pattern patternClientID = Pattern.compile("deployment\\/([\\w\\d]+)\\/.*");
  
  private final long providerID;
  private final MqttClient mqttClient;
  private final Map<String, DeployClient> clients = new HashMap<>();
  private IDeployStatusListener listener;
  
  private boolean active = true;
  
  public DefaultDeployTargetProvider(long providerID, MqttClient mqttClient, IDeployStatusListener listener) throws MqttException {
    this.providerID = providerID;
    this.mqttClient = mqttClient;
    this.listener = listener;
  }
  
  public DefaultDeployTargetProvider(long providerID, MqttClient mqttClient) throws MqttException {
    this(providerID, mqttClient, new VoidDeployStatusListener());
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
   * Starts the Watchdog for this {@link DefaultDeployTargetProvider}. Detects timeouts of
   * clients.
   */
  private void runWatchdog() {
    try {
      while (true) {
        Thread.sleep(CLIENT_TIMEOUT);
        if(!active) return;
        
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
        LocationSpecifier location = new LocationSpecifier();
        location.setBuilding(building);
        location.setFloor(floor);
        location.setRoom(room);
        client = DeployClient.create(clientID, false, location, providerID, hardware);
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
    
    /// System.out.println("received heartbeat from client " + clientID);
    DeployClient client = clients.get(clientID);
    if (client != null) {
      client.setLastSeen(System.currentTimeMillis());
      if (!client.isOnline()) {
        client.setOnline(true);
        this.listener.onClientOnline(client);
      }
    }
  }
  
  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) throws DeploymentException {
    Map<String, DockerComposeConfig> composes = DockerComposeConfig.fromDistribution(distribution, deploymentInfo, net);
    for (Entry<String, DockerComposeConfig> e : composes.entrySet()) {
      this.deploy(e.getKey(), e.getValue().serializeYaml());
    }
  }
  
  public void deploy(String clientID, String ymlCompose) throws DeploymentException {
    try {
      MqttMessage msg = new MqttMessage();
      if (ymlCompose != null)
        msg.setPayload(ymlCompose.getBytes(Charsets.UTF_8));
      this.mqttClient.publish("deployment/" + clientID + "/push", msg);
    } catch(MqttException e) {
      throw new DeploymentException(e);
    }
  }
  
  public Collection<DeployClient> getClients() {
    return this.clients.values();
  }
  
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }

  @Override
  public void initialize() throws DeploymentException {
    try {
      MqttConnectOptions options = new MqttConnectOptions();
      // allow more messages being sent with QOS>0
      options.setMaxInflight(1_000);
      options.setAutomaticReconnect(true);
      mqttClient.connect(options);
      this.prepare();
    } catch(MqttException e) {
      e.printStackTrace();
      throw new DeploymentException("Could not initialize basic target provider", e);
    }
  }

  @Override
  public void close() throws DeploymentException {
    try {
      if(mqttClient.isConnected()) {
        mqttClient.disconnect();
      }
      mqttClient.close();
      this.active = false;
    } catch(MqttException ignored) {
      // We can ignore this, since the client is already dead if this fails.
    }
  }
  
}
