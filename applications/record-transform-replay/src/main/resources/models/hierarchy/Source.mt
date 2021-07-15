// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port in int sensor;
  port out int value;

  long timestamp = 0;

  behavior sensor {
    // now() is nondeterministic, hence usage of nd()
    timestamp = nd(now());

    log("Current timestamp: " + timestamp);
    value = sensor;
  }

  update interval 1s;
}
