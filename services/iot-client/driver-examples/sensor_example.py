# (c) https://github.com/MontiCore/monticore
from montithingsconnector import MontiThingsConnector as MTC
from random import random
import time


if __name__ == '__main__':
    # Set up MontiThings port connection
    mtc = MTC("pir", None, parse_cmd_args=True)
    while True:
        # Send a message to MontiThings (sensor port)
        mtc.send(random() < 0.3)
        time.sleep(2)
