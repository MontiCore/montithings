# (c) https://github.com/MontiCore/monticore
from montithingsconnector import MontiThingsConnector as MTC

# Handle an incoming message by printing it (actuator port)
def receive(message):
    # your actuator code here
    print(str(message, 'utf-8'))


mtc = MTC("lightbulb", receive, parse_cmd_args=True)
