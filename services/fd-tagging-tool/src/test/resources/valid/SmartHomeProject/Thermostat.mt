package valid.SmartHomeProject;

component Thermostat {
    port in boolean input;
    boolean heat = false;

    behavior input {
        if (input) {
          heat = true;
        } else {
          heat = false;
        }
    }
}
