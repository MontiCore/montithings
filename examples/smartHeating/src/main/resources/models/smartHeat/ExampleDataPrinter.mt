// (c) https://github.com/MontiCore/monticore
package smartHeat;

component ExampleDataPrinter {
    port in TemperatureAdjustmentUnitMessages.ModeConfiguration configuration;
    port in ThermostatMessages.Temperature temperature_setting_1;
    port in ThermostatMessages.Temperature temperature_setting_2;
    port in ExampleDataGeneratorMessages.Clock clock;

    behavior configuration{
     log("[TemperatureAdjustmentUnit] configuaration: " + configuration.config);
    }

    behavior temperature_setting_1{
        log("[Thermostat_1] temperature: " + temperature_setting_1.temp);
    }

    behavior temperature_setting_2{
        log("[Thermostat_2] temperature: " + temperature_setting_2.temp);
    }

    behavior clock{
        log("[Clock] " + clock.month + " " + clock.day + " " + clock.time);
    }
}

