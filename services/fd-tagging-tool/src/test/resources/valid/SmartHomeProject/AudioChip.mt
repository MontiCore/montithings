// (c) https://github.com/MontiCore/monticore
package valid.SmartHomeProject;

component AudioChip {

    boolean value = true;

    every 100s {
    value = !value;
    }
}
