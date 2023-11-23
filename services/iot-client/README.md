<!-- (c) https://github.com/MontiCore/monticore -->
# IoT Client
There are multiple ways how you can set up your IoT clients with MontiThings.

You can either use [our IoT Manager](../iot-manager/README.md) and have the devices report themselves to that manager using [our python scripts](./python-client/README.md)

Or you can use an [Azure IoT Hub](https://azure.microsoft.com/en-us/products/iot-hub/) and have the devices report themselves to it using our [Azure setup script for Ubuntu](./azure-client/README.md). 

The [driver-examples](driver-examples) folder provides examples for how to write
hardware drivers that are dynamically connected to components at runtime.