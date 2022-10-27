<!-- (c) https://github.com/MontiCore/monticore -->
# Client for Azure IoT Hub

## Setup
1. Copy the install-script (`installAzureUbuntu2004.sh`) on the device you want to set up
2. Enter your configuration at the top of the script
3. Run the script.

The `sensoractuatormanager` is now running as a service and will automatically restart if the device reboots or the 
process is terminated in another way. It can be stopped with `sudo systemctl stop sensoractuatormanager` if needed. 

You can read the output of `sensoractuatormanager` in its log files at `/var/log/sensoractuatormanager/`

You can now proceed to set up your drivers as explained [here](../driver-examples/README.md)
