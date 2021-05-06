from montithingsconnector import MontiThingsConnector as MTC
import time


# Set up MontiThings port connection
mtc = MTC("example-sensor-topic", None)

while True:
    # Send a message to MontiThings (sensor port)
    mtc.send(32)
    time.sleep(1)