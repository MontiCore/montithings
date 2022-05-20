// (c) https://github.com/MontiCore/monticore
package valid.SmartHomeProject;

component LightSensor {
    port out boolean output;
    boolean value = true;

    every 100s {
    output = value;
    value = !value;
    }

}
