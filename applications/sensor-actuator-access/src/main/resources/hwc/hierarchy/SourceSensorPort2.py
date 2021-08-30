# (c) https://github.com/MontiCore/monticore
from montithingsconnector import MontiThingsConnector as MTC
import time


# Set up MontiThings port connection
mtc = MTC("second-source-sensor-topic", None)

while True:
    # Send a message to MontiThings (sensor port)
    mtc.send(32)
    time.sleep(2)