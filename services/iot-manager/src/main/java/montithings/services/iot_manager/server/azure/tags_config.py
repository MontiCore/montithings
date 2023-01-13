# (c) https://github.com/MontiCore/monticore

# config file for set_device_tags.py

# the connection-string of the iot-hub, found under 'shared-access-policies'
IOTHUB_CONNECTION_STRING = "PASTE_CONNECTION_STRING_HERE"

# the name of the device
DEVICE_ID = "myiotdevice"

# enter the new tags in JSON-style here
NEW_TAGS = {
    'location': {
        'building': 'C',
        'floor': '7',
        'room': '007'
    },
    'hardware': ["sensor4", "led1", "heating3"],
    "hardwareOD": """objectdiagram VL53L0X {
                     VL53L0X : DistanceSensor {
                       manufacturer = "ST";
                       int versionNo = "2";
                       int partNo = "VL53L0X";
                       latency = :Latency {
                         int milliseconds = 30;
                       };
                       accuracy = :Accuracy {
                         int percent = 97;
                       };
                       range = :Range {
                         int min = 3;
                         int max = 17;
                       };
                     };
                   }"""
}
