<!-- (c) https://github.com/MontiCore/monticore -->
# IoT Manager API Endpoints

Endpoints require no authentication.

## Device related

#### Show List of Registered Devices

Get the list of all registered devices

**URL** : `/api/v1/resources/devices/`

**Method** : `GET`

##### Success Response

**Code** : `200 OK`

**Content example**

A json containing an array of dicts is returned.
In this example the list was limited to one device.

```json
[
    {
        "hostname": "raspy_b1_f1_temp_2",
        "mac": "aa:bb:cc:dd:aa",
        "state": "online",
        "running_services": null,
        "ip": "123.134.135.1",
        "location": {
            "building": "1",
            "floor": "1",
            "room": "102"
        },
        "ports": [
            [
                "8080:5001",
                "1337:5002"
            ],
            [
                "8080:5003",
                "1337:5004"
            ]
        ],
        "type": "device",
        "id": 1,
        "last_seen": null,
        "heartbeat": 300,
        "hardware": [
            "sensor_temperature"
        ],
        "is_docker": null
    }
]
```

#### Register a new Device

Register a new device.

##### Note
Devices are also automatically registered when they send their first heartbeat.

**URL** : `/api/v1/resources/devices/`

**Method** : `POST`

##### Success Response

**Code** : `200 OK`

**Content example**

Include the data of the device in the body of the request in the JSON format.

```json
    {
      "mac": "aa:bb:cc:dd:fa",
      "is_docker": 1,
      "ip": "123.134.135.1",
      "hostname": "raspy_b3_f3_controller_temp_2",
      "type": "device",
      "heartbeat": "300",
      "hardware": [
        "sensor_temperature",
        "heat_controller"
      ],
      "location": {
        "building": "3",
        "floor": "3",
        "room": "302"
      }
    }
```

#### Update device by id

**URL** : `/api/v1/resources/devices/<int:id>`

**Method** : `GET`,`PATCH`,`DELETE`

##### Success Response

**Code** : `200 OK`

## Component related

#### Show list of components

Get the list of all registered components

**URL** : `/api/v1/resources/components/`

**Method** : `GET`

##### Success Response

**Code** : `200 OK`

**Content example**

```json
[
    {
        "name": "example",
        "requirements": {
            "has_hardware":"temp_controller"
            }
    }
]
```

#### Register a new Component

Register a new component.

**URL** : `/api/v1/resources/components/`

**Method** : `POST`

##### Success Response

**Code** : `200 OK`

#### Update a component by name

**URL** : `/api/v1/resources/components/<string:name>`

**Method** : `GET`,`PATCH`,`DELETE`

##### Success Response

**Code** : `200 OK`

## Distribution related

#### Show List of deployment configurations

Get the list of all deployment configurations.
Additional data such as the latest distribution is included.

**URL** : `/api/v1/resources/distributions`

**Method** : `GET`

##### Success Response

**Code** : `200 OK`

**Content example**

An array of distributions is returned.
See the example for getting the latest distribution.

#### Update deployment configuration by id

**URL** : `/api/v1/resources/distributions/<int:id>`

**Method** : `GET`,`PATCH`,`DELETE`

##### Success Response

**Code** : `200 OK`

**Content example**

See the example for getting the latest distribution.

#### Get the latest deployment configuration

**URL** : `/api/v1/resources/distributions/latest`

**Method** : `GET`

##### Success Response

**Code** : `200 OK`

**Content example**

```json
{
  "config": {
    "distribution": {
      "registry.git.rwth-aachen.de/se-student/ss20/labs/iot-smart-home/deployment/applications/hierarchy/hierarchy.converter": {
        "distribution_constraints": [
          ["location","room106","==","all"]
        ],
        "distribution_selection": [
          ["location","building1",1]
        ],
        "service": {
          "ports": [8080, 1337]
        }
      },
      "registry.git.rwth-aachen.de/se-student/ss20/labs/iot-smart-home/deployment/applications/hierarchy/hierarchy.double": {
        "distribution_constraints": [
          ["location","room101","==","all"]
        ],
        "distribution_selection": [
          ["location","building1",1]
        ],
        "service": {
          "ports": [8080,1337]
        }
      }
    }
  },
  "distribution": {
    "registry.git.rwth-aachen.de/se-student/ss20/labs/iot-smart-home/deployment/applications/hierarchy/hierarchy.converter": [
      "<devices>"
    ],
    "registry.git.rwth-aachen.de/se-student/ss20/labs/iot-smart-home/deployment/applications/hierarchy/hierarchy.double": [
      "<devices>"
    ]
  },
  "id": 19
}
```

##### Notes

"<devices>" in the example return is a placeholder for a list of populated device objects.


#### Add a new deployment configuration file

Get the list of all deployment configuration files.
Additional data such as the latest distribution is included.

**URL** : `/api/v1/resources/distributions`

**Method** : `POST`

##### Success Response

**Code** : `200 OK`

**Content example**

See e.g. [Example1](../examples/distribution_config/example1/config.json).

#### Get deployment configuration

Returns latest deployment information (list of devices) for a given component.

NOTE: The API endpoint does not refer to the Component model and the naming might be confusing. However, since the endpoint is used in the final version by other groups, we decided to not change it.


**URL** : `/api/v1/components/latest`

**Method** : `GET`

**Parameter** : `&component=`

##### Success Response

**Code** : `200 OK`

#### Trigger a new task which computes distribution

This endpoint triggers a task which builds and runs prolog.
**URL** : `/api/v1/tasks/distribution/add/latest`

**Method** : `GET`

##### Success Response

**Code** : `200 OK`

#### Trigger a new task which computes distribution

This endpoint triggers a task which builds and runs prolog.
**URL** : `/api/v1/tasks/distribution/add/latest`

**Method** : `GET`

##### Success Response

**Code** : `200 OK`
