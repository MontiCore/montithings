// (c) https://github.com/MontiCore/monticore
package smartHeat;

component TemperatureAdjustmentUnit {
  port in ExampleDataGeneratorMessages.KeyPadInput key_pad_input;
  port in ExampleDataGeneratorMessages.Clock clock;
  port out TemperatureAdjustmentUnitMessages.TemperatureOverwrite temp_overwrite_1;
  port out TemperatureAdjustmentUnitMessages.TemperatureOverwrite temp_overwrite_2;
  port out TemperatureAdjustmentUnitMessages.ModeConfiguration configuration;
}