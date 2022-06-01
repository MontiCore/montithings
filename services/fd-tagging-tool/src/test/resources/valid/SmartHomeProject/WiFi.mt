// (c) https://github.com/MontiCore/monticore
package valid.SmartHomeProject;

component WiFi {
    boolean output = false;

    boolean value = true;

    every 100s {
    output = value;
    value = !value;
    }
}
