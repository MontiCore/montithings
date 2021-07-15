# (c) https://github.com/MontiCore/monticore
import paho.mqtt.client as mqtt
import json


class MontiThingsConnector:
    def __init__(self, topic_name, receive):
        self.topic_name = topic_name
        self.mqttc = mqtt.Client()
        if receive is None:
            self._receive = lambda *args: None
        else:
            self._receive = receive
        self.connected = False
        self.connect_to_broker()
        self.wait_for_connection()

    def connect_to_broker(self):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect("localhost", 1883)
        self.mqttc.subscribe("/sensorActuator/" + self.topic_name, qos=0)
        self.mqttc.loop_start()

    def on_connect(self, mqttc, obj, flags, rc):
        print("Connected MQTT broker.")
        self.connected = True

    def on_disconnect(self, client, userdata, rc):
        self.connected = False

    def on_message(self, client, userdata, message):
        self._receive(message.payload)

    def send(self, msg):
        import uuid
        j = json.dumps({
            'value0': {
                'payload': {
                    'nullopt': False,
                    'data': msg
                },
                'uuid': str(uuid.uuid4())
            }
        })
        info = self.mqttc.publish("/sensorActuator/" + self.topic_name, j, qos=0)
        info.wait_for_publish()

    def wait_for_connection(self):
        while not self.connected:
            pass