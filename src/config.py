import uuid

DOCKER_COMPOSE_EXECUTABLE = "docker-compose"

# unique id identifying this client
CLIENT_ID = format(uuid.getnode(),'x')

# host name / IP address of the MQTT broker
MQTT_BROKER_HOST = "127.0.0.1" # "iot7.se.rwth-aachen.de"

# seconds between each heartbeat
HEARTBEAT_FREQUENCY = 15
