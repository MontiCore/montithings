package valid.SmartHomeProject;

component LargeFridge {

    boolean value = true;

    every 100s {
    value = !value;
    }
}
