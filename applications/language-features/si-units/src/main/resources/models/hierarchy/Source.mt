// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source (m/s startSpeed) {
  port out km/h value;

  km/h lastValue = startSpeed;

  every 1s {
    value = lastValue;
    lastValue = lastValue + 1 km/h;
  }
}
