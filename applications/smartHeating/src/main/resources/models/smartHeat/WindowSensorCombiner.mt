// (c) https://github.com/MontiCore/monticore
package smartHeat;

component WindowSensorCombiner {
  port in ExampleDataGeneratorMessages.WindowSensorState window_sensor_state;
  port out WindowSensorCombinerMessages.WindowRoomState window_room_state;
}

