# (c) https://github.com/MontiCore/monticore

# config file for set_device_tags.py

# the connection-string of the iot-hub, found under 'shared-access-policies'
IOTHUB_CONNECTION_STRING = "PASTE_CONNECTION_STRING_HERE"

# the name of the device
DEVICE_ID = "myiotdevice"

# enter the new tags in JSON-style here
NEW_TAGS = {
    'location': {
        'building': 'A',
        'floor': '2',
        'room': '002'
    },
    'hardware': ["sensor1", "led1", "motor1"]
}
