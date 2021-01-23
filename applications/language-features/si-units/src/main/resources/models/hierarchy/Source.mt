// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source (m/s startSpeed) {
  port out km/h value;

  km/h lastValue = startSpeed;

  behavior {
    value = lastValue;
    lastValue = lastValue + 1 km/h;
  }

  update interval 1sec;
}
