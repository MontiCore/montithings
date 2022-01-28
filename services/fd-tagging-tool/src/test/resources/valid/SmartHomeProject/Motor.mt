package valid.SmartHomeProject;

component Motor {
    port in boolean input;
    boolean movement = false;

    behavior input {
        if (input) {
          movement = true;
        } else {
          movement = false;
        }
    }
}
