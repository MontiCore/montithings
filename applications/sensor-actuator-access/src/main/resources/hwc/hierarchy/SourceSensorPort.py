# (c) https://github.com/MontiCore/monticore
from montithingsconnector import MontiThingsConnector as MTC
import time


if __name__ == '__main__':
    # Set up MontiThings port connection
    mtc = MTC("source-sensor-topic", None, parse_cmd_args=True)

    while True:
        # Send a message to MontiThings (sensor port)
        mtc.send(32)
        time.sleep(2)