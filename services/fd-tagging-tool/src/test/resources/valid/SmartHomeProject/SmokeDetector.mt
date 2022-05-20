// (c) https://github.com/MontiCore/monticore
package valid.SmartHomeProject;

component SmokeDetector {
    port out boolean output;

    boolean value = false;

    every 100s{
    output = value;
    value = !value;
    }
}
