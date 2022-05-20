// (c) https://github.com/MontiCore/monticore
package valid.SmartHomeProject;

component Sprinkler {
    port in boolean input;
    boolean water = false;

    behavior input {
        if (input) {
          water = true;
        } else {
          water = false;
        }
    }
}
