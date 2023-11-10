// (c) https://github.com/MontiCore/monticore
package smartHeat;

component MovementSensor {
  port in ExampleDataGeneratorMessages.MovementSensorValue movement_sensor_value;
  port in ExampleDataGeneratorMessages.Clock clock;
  port out MovementSensorMessages.MovementState activation_room_1;
  port out MovementSensorMessages.MovementState activation_room_2;
}