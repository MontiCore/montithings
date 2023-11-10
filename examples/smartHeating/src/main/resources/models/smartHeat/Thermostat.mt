// (c) https://github.com/MontiCore/monticore
package smartHeat;

component Thermostat {
  port in TemperatureAdjustmentUnitMessages.TemperatureOverwrite temp_overwrite;
  port in MovementSensorMessages.MovementState movement_activation;
  port in WindowSensorCombinerMessages.WindowRoomState window_room_state;
  port in ExampleDataGeneratorMessages.Clock clock;
  port out ThermostatMessages.Temperature temperature_setting;
}