<!-- (c) https://github.com/MontiCore/monticore -->
# Configuration of the IoT-Manager

## App specific config

TODO

## Client config

Each client (this can be a Rasperry Pi for example) is equipped with certain hardware and is located at a unique location.
In order to include a client in the network, each client has to hold a configuration file which may look like the following:

```javascript
{
  "name": "raspy_b1_f1_temp_1":
  "type": "device",
  "hardware": ["sensor_temperature"],
  "location": {
    "building": "1",
    "floor": "1",
    "room": "101"
  }
}
```

When the client registers itself at the manger, it has to include this configuration.


## Distribution specific config
The IoT-Manager consumes a json configuration file which defines all constraints that need to hold true.
E.g. your infrastructure requires at least two devices in `building1` which are `online` and equipped with a `temperature sensor`.

The configuration is structured as follows:
```javascript
{
  "distribution": {
    "<docker_image>": {
      "distribution_selection":
      [
        [key, value, {0,1}], //conjuction
        [
          [key, value, {0,1}], //disjunction
          [key, value, {0,1}],
          ...
        ],
        ...
      ],
      "distribution_constraints":
      [
        [key, value, {"<=", "==", ">="}, {Int, "all"}],
        ...
      ]
    },
    "<docker_image2>":{...},
    ...
  },
  "incompatibilities": [
    [componentA,componentB],
    ...
  ],
  "dependencies": [
    {
      "type": {"distinct","simple"},
      "dependent": "componentA",
      "dependency": "componentB",
      "amount_at_least": Int>0
    },
    ...
  ]
}

```
- `distribution` property: Within this property, the distribution for all docker images is defined. For each docker image as the key, the distribution is further split into...
    - `distribution_selection`: The selection constraints filter the initial set of possible devices. Nested arrays represent a disjunction.
    - `distribution_constraints`: Set of constrains that are applied to the selection.
- `incompatibilities` property (optional): Define which components should not run on the same device. E.g. `[componentA,componentB]` makes sure that `componentA` and `componentB` is never distributed to the same device.
- `dependencies` property (optional): If a component depends on a different component, you can define dependencies here. There are two types:
    - `simple`: `componentA` requires `amount_at_least` devices that run `componentB`. However, the dependency is not dedicated to `componentA`. Thus, a device running `componentB` can fulfill the dependency of multiple dependents.
    - `distinct`: Each device running `componentA` requires their own `amount_at_least` devices that run `componentB`.

### Syntax of keys and values
The keys and values have to match the scheme which was applied to the device configuration.
E.g. if the devices are configured as in [the example world](./docs/examples/distribution_config/world/facts.json), the following keys can be used:

- `has_hardware` with values `sensor_temperature`, ...
- `location_building` with values `1`,`2`, ...
- `location_floor` with values `1`,`2`, ...
- `location_room` with values `301`, `302`, ...
- `location` with values e.g. `building1_floor3_room301`, `building1_floor3`, `building1_room301`, `building1`, `floor3_room301`, `floor3`, `room301`, ...
- `state` with values `online`, `offline`
- `type` with values `device`

### Examples

Examples are located [here](./docs/examples/distribution_config).
