// (c) https://github.com/MontiCore/monticore
package smartHeat;

component ExampleDataGenerator {
  port out ExampleDataGeneratorMessages.KeyPadInput key_pad_input;
  port out ExampleDataGeneratorMessages.WindowSensorState window_sensor_state_1;
  port out ExampleDataGeneratorMessages.WindowSensorState window_sensor_state_2;
  port out ExampleDataGeneratorMessages.MovementSensorValue movement_sensor_value;
  port out ExampleDataGeneratorMessages.Clock clock;
}