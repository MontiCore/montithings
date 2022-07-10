import sys
import time
from base64 import b64encode, b64decode

from montithingsconnector import MontiThingsConnector
from Foo_pb2 import Foo
from DoublerImpl import DoublerImpl

print("sys.path:", sys.path)

class Doubler(MontiThingsConnector):

    def on_message(self, client, userdata, message):
        m_decode = message.payload.decode("utf-8")
        m_in = json.loads(m_decode) #decode json data
        data = b64decode(m_in["value0"]["payload"]["data"])

        foo = Foo()
        foo.ParseFromString(data)

        result = self._receive.compute(foo)

        self.send(b64encode(result.SerializeToString()))

    def connect_to_broker(self, receive, broker_hostname, broker_port):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect(broker_hostname, broker_port)
        self.mqttc.subscribe("/ports/hierarchy/Example/source/value", qos=0)
        self.mqttc.loop_forever()


if __name__=="__main__":
    doubler = DoublerImpl()
    Doubler(offered_type="foo", receive=doubler)