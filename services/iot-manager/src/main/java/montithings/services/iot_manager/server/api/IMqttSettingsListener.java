// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.api;

import montithings.services.iot_manager.server.data.NetworkInfo;

public interface IMqttSettingsListener {
  /**
   * Called when the settings of the MQTT connection are changed
   *
    * @param networkInfo the updated MQTT settings
   */
  void onMqttSettingsChanged(NetworkInfo networkInfo);
}
