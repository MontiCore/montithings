from paho.mqtt.client import MQTTMessage, Client as MQTTClient
import json
import uuid

def JSONDump(msg, uuid, nullopt=False):
    return json.dumps({
        'value0': {
            'payload': {
                'nullopt': nullopt,
                'data': str(msg)
            },
            'uuid': str(uuid)
        }
    })

class MQTTConnector(MQTTClient):

    serialize = lambda _,x: x
    deserialize = lambda _,x: x
    # sets of fully qualified port-Strings
    ports_in = set()
    ports_out = set()

    def __init__(self, client_id="", reconnect_on_failure=True):
        super().__init__(client_id, reconnect_on_failure)

    def on_message(self, client, userdata, message):
        """ Implementation callback:

        For a Sink-Component, Sink.py should implement this callback and
        call the SinkImpl.py implementation of SinkImplTOP.compute on the
        deserialized result.
        If the in-port is connected to an out-port, the result should be published.
        """
        raise NotImplementedError()

    def publish(self, port_out, payload, **kwargs) -> MQTTMessage:
        """Takes a fully qualified port, turns it into a "/ports/[port]"-topic and
        publishes the payload under this topic"""
        msg_payload = JSONDump(self.serialize(payload), uuid.uuid4())
        return super().publish(
            f"/ports/{port_out}".replace(".", "/"),
            msg_payload,
            **kwargs
        )

    def on_connect(self, client, obj, flags, rc):
        print(self.ports_in, "connected to MQTT Broker")
        self.connected = True

    def on_disconnect(self, client, userdata, rc):
        print(self.ports_in, "disconnected from MQTT Broker")
        self.connected = False

    def connect(self, host='localhost', port=1883, keepalive=60):
        super().connect(host, port, keepalive)
        for montithings_port in self.ports_in:
            topic = f"/connectors/{montithings_port}".replace(".", "/")
            print(topic, "awaiting connection...")
            self.subscribe(topic, qos=0)
        self.loop_forever()