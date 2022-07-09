from msilib.schema import PublishComponent
from paho.mqtt.client import MQTTMessage, Client as MQTTClient
import json
import uuid

def JSONDump(msg, uuid, nullopt=False):
    return json.dumps({
        'value0': {
            'payload': {
                'nullopt': nullopt,
                'data': msg
            },
            'uuid': str(uuid)
        }
    })

class MQTTConnector(MQTTClient):

    def __init__(self, client_id="", clean_session=None, userdata=None, protocol=..., transport="tcp", reconnect_on_failure=True):
        super().__init__(client_id, clean_session, userdata, protocol, transport, reconnect_on_failure)


class MQTTSensorActuator(MQTTClient):

    topic = "/sensorActuator/{}/{}"

    def __init__(self, offered_type="N.A.", client_id="", clean_session=None, userdata=None, protocol=..., transport="tcp", reconnect_on_failure=True):
        super().__init__(client_id, clean_session, userdata, protocol, transport, reconnect_on_failure)
        self.offered_type = "N.A."
        self.uuid = str(uuid.uuid4())

    def on_message(self, client, userdata, message):
        """ Implementation callback:

        For a Sink-Component, Sink.py should implement this callback and
        call the SinkImpl.py implementation of SinkImplTOP.compute on the
        deserialized result.
        If the in-port is connected to an out-port, the result should be published.
        """
        decoded_msg = json.loads(message.payload.decode("utf-8"))["value0"]["payload"]["data"]
        raise NotImplementedError()

    def publish(self, payload, **kwargs) -> MQTTMessage:
        msg = MQTTMessage()
        if self.serialize:
            msg.payload = JSONDump(self.serialize(payload), uuid.uuid4())
        else:
            msg.payload = JSONDump(payload, uuid.uuid4())
        super().publish(self.topic.format("data", self.uuid), payload, **kwargs)

    def on_connect(self, client, obj, flags, rc):
        print("Sensor / Actuator", self.uuid, "connected")
        self.connected = True
        return super().on_disconnect

    def on_disconnect(self, client, userdata, rc):
        print("Sensor / Actuator", self.uuid, "disconnected")

        return super().on_disconnect

    def connect(self, host, port=1883, keepalive=60, bind_address="", bind_port=0, clean_start=..., properties=None):
        super().connect(host, port, keepalive, bind_address, bind_port, clean_start, properties)
        super().publish(
            self.topic.format("offer", self.uuid),
            json.dumps({
                'topic': self.uuid,
                'spec': {
                    'type': self.offered_type
                }
            }),
            qos=0,
            retain=True
        )
        super().subscribe(self.topic.format("data", self.uuid), qos=0)
        super().loop_forever()