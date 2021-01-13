// (c) https://github.com/MontiCore/monticore
package replayer;

import com.google.common.base.Preconditions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
  public static void main(String[] args) throws MqttException {
    Preconditions.checkArgument(args.length == 1,
      "Only argument should be the URI of the MQTT Broker");

    String brokerUri = args[0];
    Replayer replayer = new Replayer(brokerUri);
    StateStore stateStore = new StateStore(brokerUri, replayer);
    replayer.setStateStore(stateStore);
    while (true)
      ;
  }
}
