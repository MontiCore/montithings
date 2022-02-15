# (c) https://github.com/MontiCore/monticore

from azure.iot.hub import IoTHubRegistryManager
from azure.iot.hub.models import Twin, TwinProperties
import config


# script to update the tags of a device in an azure-iot-hub
# configure all inputs in tags_config.py


def update_Tags(new_tags, device_id, iothub_connection_string):
    try:
        iothub_registry_manager = IoTHubRegistryManager(iothub_connection_string)

        twin = iothub_registry_manager.get_twin(device_id)
        twin_patch = Twin(tags=new_tags, properties=TwinProperties())
        iothub_registry_manager.update_twin(device_id, twin_patch, twin.etag)
    except Exception as ex:
        print("Unexpected error {0}".format(ex))
        return
    except KeyboardInterrupt:
        print("IoT Hub Device Twin service sample stopped")


if __name__ == '__main__':
    update_Tags(config.NEW_TAGS, config.DEVICE_ID, config.IOTHUB_CONNECTION_STRING)
