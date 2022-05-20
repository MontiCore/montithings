// (c) https://github.com/MontiCore/monticore
package valid.SmartHomeProject;

component LightSwitch {
    port in boolean input;
    boolean lights = false;

    behavior input {
        if (input) {
          lights = true;
        } else {
          lights = false;
        }
    }
}
