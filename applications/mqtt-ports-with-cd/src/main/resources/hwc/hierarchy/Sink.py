import sys
import time

from montithingsconnector import MontiThingsConnector
import Foo_pb2
from IComputable import IComputable, GenericResult, GenericInput


print("sys.path:", sys.path)

_foo = Foo_pb2.Foo()

class SinkResult(GenericResult):
    payload = {}

    def __init__(self, payload) -> None:
        self.payload = payload

class SinkImpl(IComputable):

    def getInitialValues(self) -> SinkResult:
        return {}

    def compute(self, _input: GenericInput) -> SinkResult:
        print(_input.getValue())
        return {}

class Sink(MontiThingsConnector):

    def connect_to_broker(self, receive, broker_hostname, broker_port):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect(broker_hostname, broker_port)
        self.mqttc.subscribe("/ports/hierarchy/Example/source/value", qos=0)
        if receive is None:
            self.mqttc.loop_start()
        else:
            self.mqttc.loop_forever()


def deserialize_and_log(input_):
    print("Hairy furball smells:", _foo.ParseFromString(input_))

if __name__=="__main__":
    mtc = Sink(offered_type=Foo_pb2.Foo, parse_command_args=True)
    while True:
        time.sleep(5) # TODO: <-- wait for SIGTERM