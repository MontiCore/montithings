import uuid

DOCKER_COMPOSE_EXECUTABLE = "docker-compose"

# unique id identifying this client
CLIENT_ID = format(uuid.getnode(),'x')

# The client config that is sent to the orchestrator.
CLIENT_CONFIG = {
    "location": {
        "building": "001",
        "floor": "002",
        "room": "003"
    },
    "hardware": [
        "HighPerformanceAdditionComputeUnit"
    ]
}

# host name / IP address of the MQTT broker
MQTT_BROKER_HOST = "127.0.0.1" # "iot7.se.rwth-aachen.de"

# seconds between each heartbeat
HEARTBEAT_FREQUENCY = 15
