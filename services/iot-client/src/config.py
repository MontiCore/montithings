# (c) https://github.com/MontiCore/monticore
import uuid
import json
from pathlib import Path
import os.path

# Warning: This config will be overriden by the MontiThings-Config (if present).
# Path: ~/.montithings/deployment-config.json

DOCKER_COMPOSE_EXECUTABLE = "docker-compose"

# unique id identifying this client
CLIENT_ID = format(uuid.getnode(),'x')

# The client config that is sent to the orchestrator.
CLIENT_CONFIG = {
    "location": {
        "building": "001",
        "floor": "002",
        "room": "042"
    },
    "hardware": [
        "HighPerformanceAdditionComputeUnit",
	    "actuatorTemperature"
    ]
}

# host name / IP address of the MQTT broker
MQTT_BROKER_HOST = "node4.se.rwth-aachen.de"
MQTT_BROKER_PORT = 1883

# seconds between each heartbeat
HEARTBEAT_FREQUENCY = 15

# override with deployment-config.json (if present)
jsonConfigPath = os.path.join(Path.home(), ".montithings", "deployment-config.json")
if os.path.isfile(jsonConfigPath):
    # load file to string
    jsonConfigFile = open(jsonConfigPath)
    jsonConfigStr = jsonConfigFile.read()
    jsonConfigFile.close()

    try:
        # parse json
        jsonConfig = json.loads(jsonConfigStr)
        # apply config
        if jsonConfig["messageBroker"]:
            hostname = jsonConfig["messageBroker"]["hostname"]
            if hostname:
                MQTT_BROKER_HOST = hostname
            port = jsonConfig["messageBroker"]["port"]
            if port:
                MQTT_BROKER_PORT = int(port)
    except:
        print("Found invalid MontiThings deployment config.")
