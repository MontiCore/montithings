package valid.SmartHomeProject;

component HeatSensor {
    port out boolean output;
    boolean value = true;

    every 100s {
    output = value;
    value = !value;
    }
}
