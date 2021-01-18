// (c) https://github.com/MontiCore/monticore
package replayer;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;

public class StateStore implements MqttCallback {

  public final static String SET_STATE_TOPIC = "/setState/";

  public final static String GET_STATE_TOPIC = "/getState/";

  public final static String STATE_TOPIC = "/state/";

  public final static String REPLAY_STATE_TOPIC = "/replaySinceState/";

  // component instance FQN -> last state of the instance
  Map<String, State> lastKnownStates = new HashMap<>();

  Replayer replayer;

  MqttClient client;

  public StateStore(String brokerURI, Replayer replayer) throws MqttException {
    this.replayer = replayer;
    client = new MqttClient(brokerURI, MqttClient.generateClientId());
    client.connect();
    client.setCallback(this);
    client.subscribe(SET_STATE_TOPIC + "#");
    client.subscribe(GET_STATE_TOPIC + "#");
    client.subscribe(REPLAY_STATE_TOPIC + "#");
  }

  @Override public void messageArrived(String topic, MqttMessage message) throws Exception {
    if (topic.startsWith(SET_STATE_TOPIC)) {
      String instanceName = topic.substring(SET_STATE_TOPIC.length());
      String state = new String(message.getPayload());
      System.out.println("Set state: " + instanceName + " : " + state);
      storeState(instanceName, state);
    }
    if (topic.startsWith(GET_STATE_TOPIC)) {
      String instanceName = topic.substring(GET_STATE_TOPIC.length());
      System.out.println("Get state for: " + instanceName);
      publishState(instanceName);
    }
  }

  protected void storeState(String instanceName, String state) {
    State stateToStore = new State(state);
    for (String port : replayer.getReceivedMessages().keySet()) {
      stateToStore.skipMessages(port, replayer.getReceivedMessages().get(port).size());
    }
    lastKnownStates.put(instanceName, stateToStore);
  }

  protected void publishState(String instanceName) throws MqttException {
    MqttMessage mqttMessage = new MqttMessage();
    State state = getLastKnownState(instanceName);
    String payload;
    if (state == null) {
      payload = "none";
    } else {
      payload = state.getState();
    }
    mqttMessage.setPayload(payload.getBytes());
    client.publish(STATE_TOPIC + instanceName, mqttMessage);
  }

  public State getLastKnownState(String instanceName) {
    return lastKnownStates.get(instanceName);
  }

  public Map<String, Integer> getMessagesToSkip(String instanceName) {
    if (lastKnownStates.get(instanceName) == null) {
      State dummyState = new State("");
      for (String port : replayer.getReceivedMessages().keySet()) {
        dummyState.skipMessages(port, 0);
      }
      return dummyState.getMessagesToSkipForPort();
    }

    return new HashMap<>(lastKnownStates.get(instanceName).getMessagesToSkipForPort());
  }

  @Override public void connectionLost(Throwable cause) {
    // useless for us
    cause.printStackTrace();
    System.out.println("State Store lost connection. Reconncting...");
    while (!client.isConnected()) {
      try {
        client.connect();
      }
      catch (MqttException e) {
        e.printStackTrace();
      }
    }
    System.out.println("State Store reconnected.");
  }

  @Override public void deliveryComplete(IMqttDeliveryToken token) {
    // useless for us
  }
}
