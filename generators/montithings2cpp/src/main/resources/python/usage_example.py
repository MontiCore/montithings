# (c) https://github.com/MontiCore/monticore
from montithingsconnector import MontiThingsConnector as MTC
import time


# Handle an incoming message by printing it (actuator port)
def receive(message):
    print(str(message, 'utf-8'))


# Set up MontiThings port connection
mtc = MTC("test", receive)

# Send a message to MontiThings (sensor port)
mtc.send(32)

# Keep the program alive long enough to actually send or receive something
time.sleep(5)
