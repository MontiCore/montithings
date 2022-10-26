# Driver Example

## Set up the example
1. Run the `installDrivers.sh` script on your device.
2. The python scripts in `/driver_examples/` are now running as a service on the device and will automatically restart when stopped

## Configuring your own drivers
To implement your own sensor driver, replace `random() < 0.3` in `sensor_example.py:12` with whatever information you want to send from your sensor

To implement your own actuator driver, replace the print statement in `actuator_example.py:7` with code that performs your desired actions on your actuator

In both cases you will also have to replace the mqtt-topics in `mtc = MTC("topic",...)` with your own
