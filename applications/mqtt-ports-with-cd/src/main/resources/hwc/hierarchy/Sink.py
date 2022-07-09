import sys
import time
from base64 import b64decode

from montithingsconnector import MontiThingsConnector
from Foo_pb2 import Foo
from SinkImpl import SinkImpl

print("sys.path:", sys.path)

class Sink(MontiThingsConnector, SinkImpl):

    def connect_to_broker(self, receive, broker_hostname, broker_port):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect(broker_hostname, broker_port)
        self.mqttc.subscribe("/ports/hierarchy/Example/source/value", qos=0)
        self.mqttc.loop_forever()


def deserialize_and_log(input_):
    proto_payload = b64decode(input_)
    foo = Foo()
    foo.ParseFromString(proto_payload)
    print("Hairy furball smells:")
    print(foo)

if __name__=="__main__":
    Sink(offered_type="foo", receive=deserialize_and_log)