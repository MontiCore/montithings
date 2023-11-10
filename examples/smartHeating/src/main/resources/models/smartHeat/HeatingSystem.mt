// (c) https://github.com/MontiCore/monticore
package smartHeat;

component HeatingSystem {
  Thermostat thermostat_1;
  Thermostat thermostat_2;
  WindowSensorCombiner window_sensor_1;
  WindowSensorCombiner window_sensor_2;

  TemperatureAdjustmentUnit tau;
  MovementSensor movement_sensor;

  ExampleDataGenerator edg;
  ExampleDataPrinter edp;


  edg.key_pad_input -> tau.key_pad_input;
  edg.window_sensor_state_1 -> window_sensor_1.window_sensor_state;
  edg.window_sensor_state_2 -> window_sensor_2.window_sensor_state;
  edg.movement_sensor_value -> movement_sensor.movement_sensor_value;
  edg.clock -> thermostat_1.clock;
  edg.clock -> thermostat_2.clock;
  edg.clock -> tau.clock;
  edg.clock -> movement_sensor.clock;
  edg.clock -> edp.clock; 

  window_sensor_1.window_room_state -> thermostat_1.window_room_state;
  window_sensor_2.window_room_state -> thermostat_2.window_room_state;

  movement_sensor.activation_room_1 -> thermostat_1.movement_activation;
  movement_sensor.activation_room_2 -> thermostat_2.movement_activation;

  tau.temp_overwrite_1 -> thermostat_1.temp_overwrite;
  tau.temp_overwrite_2 -> thermostat_2.temp_overwrite;
  tau.configuration -> edp.configuration;

  thermostat_1.temperature_setting -> edp.temperature_setting_1;
  thermostat_2.temperature_setting -> edp.temperature_setting_2;
  
}