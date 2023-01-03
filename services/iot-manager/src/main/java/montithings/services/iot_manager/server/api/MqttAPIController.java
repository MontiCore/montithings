// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.api;

import com.google.gson.*;
import montithings.services.iot_manager.server.DeploymentManager;
import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.dto.DeploymentAssignmentDTO;
import montithings.services.iot_manager.server.exception.DeploymentException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Hosts an API for controlling a {@link DeploymentManager}.
 */
public class MqttAPIController implements IDeployStatusListener, IMqttSettingsListener {

  private static final String MQTT_PREFIX = "deploymngr";

  private static final String TOPIC_SETCONFIG_REQUEST = MQTT_PREFIX + "/setConfig/request";
  private static final String TOPIC_SETCONFIG_RESPONSE = MQTT_PREFIX + "/setConfig/response";
  private static final String TOPIC_SETINFO_REQUEST = MQTT_PREFIX + "/setInfo/request";
  private static final String TOPIC_SETINFO_RESPONSE = MQTT_PREFIX + "/setInfo/response";
  private static final String TOPIC_UPDATEDEPLOYMENT_REQUEST = MQTT_PREFIX + "/updateDeployment/request";
  private static final String TOPIC_UPDATEDEPLOYMENT_RESPONSE = MQTT_PREFIX + "/updateDeployment/response";
  private static final String TOPIC_UPDATEDDEPLOYMENT = MQTT_PREFIX + "/updatedDeployment";
  private static final String TOPIC_UPDATE_DEVICES = MQTT_PREFIX + "/updateDevice";
  private static final String TOPIC_UPDATE_STATE = MQTT_PREFIX + "/updateState";
  private static final String TOPIC_DYNAMICS = MQTT_PREFIX + "/dynamics";
  private static final String TOPIC_PORTS_INJECT = MQTT_PREFIX + "/portsInject";

  private final DeploymentManager manager;

  private MqttClient mqtt;

  private Set<ConnectionString> connectionStrings = new HashSet<ConnectionString>();

  public MqttAPIController(DeploymentManager manager) {
    this.manager = manager;
    this.manager.setStatusListener(this);
  }

  public boolean start(NetworkInfo net) {
    // Prepare MQTT
    try {
      this.mqtt = new MqttClient(net.getMqttURI(), "MqttAPIController");
      MqttConnectOptions opts = new MqttConnectOptions();
      opts.setAutomaticReconnect(true);
      if (!net.getMqttUsername().isEmpty()) {
        opts.setUserName(net.getMqttUsername());
      }
      if (!net.getMqttPassword().isEmpty()) {
        opts.setPassword(net.getMqttPassword().toCharArray());
      }
      while (true) {
        try {
          this.mqtt.connect(opts);
          break;
        } catch (MqttException e) {
          System.err
              .println("Failed to connect to MQTT broker \"" + net.getMqttURI() + "\". Trying again in 3 seconds...");
          try {
            Thread.sleep(3_000);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
        }
      }

      this.mqtt.subscribe(TOPIC_SETCONFIG_REQUEST, this::handleSetDeployConfig);
      this.mqtt.subscribe(TOPIC_SETINFO_REQUEST, this::handleSetDeployInfo);
      this.mqtt.subscribe(TOPIC_UPDATEDEPLOYMENT_REQUEST, this::handleUpdateDeployment);
      this.mqtt.subscribe(TOPIC_DYNAMICS, this::handleReceiveConnectionString);

      // notify others that we're online
      this.mqtt.publish(TOPIC_UPDATE_STATE, new MqttMessage());

      // update devices
      for (DeployClient c : manager.getTargetProvider().getClients()) {
        sendDeviceUpdate(c);
      }
    } catch (MqttException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private void publishSuccess(String topic, boolean success) {
    try {
      this.mqtt.publish(topic, new MqttMessage(("{\"success\":" + success + "}").getBytes(StandardCharsets.UTF_8)));
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  private void handleSetDeployConfig(String topic, MqttMessage message) {
    byte[] data = message.getPayload();
    if (data != null) {
      String strJson = new String(data, StandardCharsets.UTF_8);
      try {
        DeploymentConfiguration config = DeploymentConfiguration.fromJson(strJson);
        System.out.println(new DeployConfigBuilder(config).applyConfigConstraints().build());
        manager.setDeploymentInfo(config.getDeploymentInfo());
        manager.setDeploymentConfig(config);
        publishSuccess(TOPIC_SETCONFIG_RESPONSE, true);
        return;
      } catch (JsonParseException | DeploymentException e) {
        e.printStackTrace();
      }
    }
    // This is only executed when the above does not succeed in any way.
    publishSuccess(TOPIC_SETCONFIG_RESPONSE, false);
  }

  private void handleSetDeployInfo(String topic, MqttMessage message) {
    byte[] data = message.getPayload();
    if (data != null) {
      String strJson = new String(data, StandardCharsets.UTF_8);
      try {
        JsonObject jo = JsonParser.parseString(strJson).getAsJsonObject();
        DeploymentInfo deployInfo = DeploymentInfo.fromJson(jo);
        manager.setDeploymentInfo(deployInfo);
        publishSuccess(TOPIC_SETINFO_RESPONSE, true);
        return;
      } catch (JsonParseException | ClassCastException | DeploymentException e) {
        e.printStackTrace();
      }
    }
    // This is only executed when the above does not succeed in any way.
    publishSuccess(TOPIC_SETINFO_RESPONSE, false);
  }

  private void handleUpdateDeployment(String topic, MqttMessage message) {
    try {
      manager.updateDeployment();
      publishSuccess(TOPIC_UPDATEDEPLOYMENT_RESPONSE, true);
    } catch (DeploymentException e) {
      publishSuccess(TOPIC_UPDATEDEPLOYMENT_RESPONSE, false);
    }
  }

  private void handleReceiveConnectionString(String topic, MqttMessage message) {
    byte[] data = message.getPayload();

    if (data != null) {
      ConnectionString connectionString = new ConnectionString(data);
      connectionStrings.add(connectionString);
      String outermostComponent = manager.getDeploymentInfo().getOutermostComponent();
      sendPortsInject(connectionString, outermostComponent, true);
    }
  }

  private void sendDeviceUpdate(DeployClient client) {
    String jsonDevice = new GsonBuilder().create().toJson(client);
    try {
      MqttMessage msg = new MqttMessage(jsonDevice.getBytes(StandardCharsets.UTF_8));
      msg.setRetained(true);
      this.mqtt.publish(TOPIC_UPDATE_DEVICES, msg);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  private void sendPortsInject(ConnectionString connectionString, String outermostComponent, boolean connect) {
    try {
      MqttMessage msg = new MqttMessage(connectionString.getConnectionStringAsByte());
      String outermostComponentTopic = String.join("/", outermostComponent.split("."));
      String portNamePrefix = connect ? "connect" : "disconnect";
      String portName = portNamePrefix + connectionString.getComponentInterface();
      String topic = String.join("/", TOPIC_PORTS_INJECT,
          outermostComponentTopic, portName);
      this.mqtt.publish(topic, msg);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onClientOnline(DeployClient client) {
    this.sendDeviceUpdate(client);
  }

  @Override
  public void onClientOffline(DeployClient client) {
    this.sendDeviceUpdate(client);
    this.sendPortsInjectDisconnect(client);
  }

  /**
   * Send disconnect to outermost component for all components of offline client
   * 
   * @param client
   */
  private void sendPortsInjectDisconnect(DeployClient client) {
    // Current distribution contains mapping of clients to components
    for (Entry<String, String[]> e : manager.getCurrentDistribution().getDistributionMap().entrySet()) {
      String clientID = e.getKey();

      // Find components of offline clinet
      if (clientID.equals(client.getClientID())) {
        // Get outermost component name
        String outermostComponent = manager.getDeploymentInfo().getOutermostComponent();
        // Get instance names
        String[] instances = e.getValue();

        // Compare received connection strings with instances of offline client
        for (Iterator<ConnectionString> i = connectionStrings.iterator(); i.hasNext();) {
          ConnectionString connectionString = i.next();

          // If there is an instance that equals connection string instance name
          // send connection string to portsInject topic and remove connection string
          if (Arrays.asList(instances).contains(connectionString.getInstanceName())) {
            sendPortsInject(connectionString, outermostComponent, false);
            i.remove();
          }
        }
      }
    }
  }

  @Override
  public void onDeploymentUpdated(Distribution dist) {
    // Send array of deployment assignments representing the new distribution.
    JsonArray jarr = new JsonArray();
    Gson gson = new Gson();
    for (Entry<String, String[]> e : dist.getDistributionMap().entrySet()) {
      jarr.add(gson.toJsonTree(new DeploymentAssignmentDTO(e.getKey(), e.getValue())));
    }
    try {
      MqttMessage msg = new MqttMessage(jarr.toString().getBytes(StandardCharsets.UTF_8));
      msg.setRetained(true);
      this.mqtt.publish(TOPIC_UPDATEDDEPLOYMENT, msg);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onMqttSettingsChanged(NetworkInfo networkInfo) {
    // Connect from current MQTT connection
    if (this.mqtt.isConnected()) {
      try {
        this.mqtt.disconnect(5);
      } catch (MqttException e) {
        e.printStackTrace();
        try {
          System.out.println("Could not disconnect from MQTT, disconnecting forcibly");
          this.mqtt.disconnectForcibly();
        } catch (MqttException ex) {
          System.out.println("Could not forcibly disconnect from MQTT!");
          throw new RuntimeException(ex);
        }
      }
    }

    // Connect with updated MQTT settings
    this.start(networkInfo);
  }
}
