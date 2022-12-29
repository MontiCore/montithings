<!-- (c) https://github.com/MontiCore/monticore -->
# Driver Example

## Set up the example
1. Run the `installDriverExamples.sh` script on your device.
2. The python scripts in `/driver_examples/` are now running as a service on the device and will automatically restart when stopped

## Configuring your own drivers
To implement your own sensor- or actuator driver, you need to set up the `MontiThingsConnector` with
```python
mtc = MTC("topic", None, parse_cmd_args=True)
```
Where `topic` ist the topic configured in the corresponding `.mtcfg` file.
If you want to receive messages from the IoT-Manager you need to put `receive` instead of `None`

Now you can send messages to the IoT-Manager with `mtc.send("foo")` and handle incoming messages with
```python
def receive(message):
    print(str(message, 'utf-8'))
```


### Set up your driver as a service
To set up your drivers as a service, enter your configuration on top of the `setUpCustomDriver.sh` script and run it.

You can check the status of your driver with `systemctl status <your driver name>` and you can get a more verbose output with `journalctl -r -u <your driver name>` 
