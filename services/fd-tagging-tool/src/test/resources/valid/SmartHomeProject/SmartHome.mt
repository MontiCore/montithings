package valid.SmartHomeProject;

component SmartHome {
    SmartSpeaker alexa;
    LightSensor lightSensor;
    LightSwitch lightSwitch;
    Refrigerator fridge;
    Motor blindsMotor;
    Sprinkler sprinkler;
    SmokeDetector smokeDetector;
    Thermostat thermostat;
    AirConditioning ac;
    HeatSensor heatSensor;

    lightSensor.output -> lightSwitch.input, blindsMotor.input;
    smokeDetector.output -> sprinkler.input;
    heatSensor.output -> thermostat.input;

}
