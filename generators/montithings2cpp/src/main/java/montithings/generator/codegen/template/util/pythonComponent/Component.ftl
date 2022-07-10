${tc.signature("protobufModule", "componentName", "publishTopic", "subscribeTopic")}

import sys
import time
import json
from base64 import b64encode, b64decode
from MQTTClient import MQTTConnector

from ${protobufModule} import *

from ${componentName}Impl import ${componentName}Impl
from ${componentName}ImplTOP import ${componentName}Input, ${componentName}Result

PROTO_CLASS = ${portProtoClass}
proto = PROTO_CLASS()

COMPONENT_IMPL = ${componentName}Impl
COMPONENT_INPUT = ${componentName}Input
COMPONENT_RESULT = ${componentName}Result

COMPONENT_PORTS_IN = [
${subscribeTopic},
]
COMPONENT_PORTS_OUT = [
${publishTopic},
]

class ProtoConnector(MQTTConnector, COMPONENT_IMPL):
    ports_in = set(COMPONENT_PORTS_IN)
    ports_out = set(COMPONENT_PORTS_OUT)

    def on_message(self, client, userdata, message):
        decoded_msg = message.payload.decode("utf-8")
        if message.topic.startswith("/connectors/"):
            topic = f"/ports/{decoded_msg}".replace(".", "/")
            print(message.topic, "->", topic)
            self.subscribe(topic, qos=0)
        else:
            payload_msg = json.loads(decoded_msg)["value0"]["payload"]["data"]
            payload_uuid = json.loads(decoded_msg)["value0"]["uuid"]
            result = self.compute(
                COMPONENT_INPUT(self.deserialize(payload_msg), payload_uuid)
            )

            # TODO: only publish on correct port
            for port in self.ports_out:
                self.publish(
                    port,
                    result
                )

    def on_connect(self, client, obj, flags, rc):
        connect = super().on_connect(client, obj, flags, rc)
        # TODO: handle getInitialValues
        return connect

    def deserialize(self, s: str) -> PROTO_CLASS:
        proto.ParseFromString(b64decode(s))
        return proto

    def serialize(self, p: PROTO_CLASS) -> str:
        return b64encode(proto.SerializeToString(p))

if __name__=="__main__":
    connector = Connector()

    # Block forever
    connector.connect()