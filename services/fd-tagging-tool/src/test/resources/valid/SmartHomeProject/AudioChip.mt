package valid.SmartHomeProject;

component AudioChip {

    boolean value = true;

    every 100s {
    value = !value;
    }
}
