# Examples

The directory `world` contains facts about some dummy devices and necessary helper functions.

Facts about the example world:
- 3 buildings: `building1` -`building3`
  - each building has 3 floors
  - each floor has rooms
- 2 hardware components available: `sensor_temperature` and `heat_controller`

See [facts.json](./world/facts.json) for the generated facts config which was composed of all device configurations and their states, or see [facts.pl](./world/facts.pl) for the generated prolog world.
Devices are named after their properties. E.g. "raspy_b3_f1_controller_temp_1" is one of two Rasperry Pis, located in `building3`, floor 1, and has a temperature sensor, as well as a heat controller
The [helpers.pl](./world/helpers.pl) file contains helper functions that are handwritten and simplify the code generation.
Finally, the `query.pl` files within the example directories are the generated Prolog codes which compute a distribution according the the `config.json`.
