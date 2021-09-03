<!-- (c) https://github.com/MontiCore/monticore -->
This example should test a specific scenario with the following constraints:

- The heat in `building1` should be controlled. Therefore, there should be at least one temperature controller on each floor.
- `room301` is known to be cold in particular, which is why one heat controller should be online in this room.
- Heat controllers require heat sensors to function properly. Therefore, there should be at least one sensor on each floor of the building.
- The maximum amount of heat sensors should be 5 since quite a lot of devices are equipped with such a sensor.
- The docker image for the sensor and controller should not run on the same device since the heat controller is too near to the heater.
