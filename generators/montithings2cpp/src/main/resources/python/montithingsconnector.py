# (c) https://github.com/MontiCore/monticore
import paho.mqtt.client as mqtt
import json
import parse_cmd
import uuid


class MontiThingsConnector:
    def __init__(self, offered_type, receive, broker_hostname='localhost', broker_port=1883, parse_cmd_args=False):
        if parse_cmd_args:
            broker_hostname, broker_port = parse_cmd.parse_cmd_args()
        self.offered_type = offered_type
        self.uuid = str(uuid.uuid4()) #get uuid in order to send to unique topic
        self.mqttc = mqtt.Client()
        if receive is None:
            self._receive = lambda *args: None
        else:
            self._receive = receive
        self.connected = False
        self.connect_to_broker(receive, broker_hostname, broker_port)
        self.wait_for_connection()

    def connect_to_broker(self, receive, broker_hostname, broker_port):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect(broker_hostname, broker_port)
        self.mqttc.publish("/sensorActuator/offer/" + self.uuid, '{"topic":"' + self.uuid + '", "spec":{"type":"' + self.offered_type + '"}}', qos=0, retain=True)
        self.mqttc.subscribe("/sensorActuator/data/" + self.uuid, qos=0)
        if receive is None:
            self.mqttc.loop_start()
        else:
            self.mqttc.loop_forever()

    def on_connect(self, mqttc, obj, flags, rc):
        print("Connected MQTT broker.")
        self.connected = True

    def on_disconnect(self, client, userdata, rc):
        self.connected = False

    def on_message(self, client, userdata, message):
         m_decode = message.payload.decode("utf-8")
         m_in = json.loads(m_decode) #decode json data
         self._receive(m_in["value0"]["payload"]["data"])

    def send(self, msg):
        j = json.dumps({
            'value0': {
                'payload': {
                    'nullopt': False,
                    'data': msg
                },
                'uuid': str(uuid.uuid4())
            }
        })
        info = self.mqttc.publish("/sensorActuator/data/" + self.uuid, j, qos=0)
        info.wait_for_publish()

    def wait_for_connection(self):
        while not self.connected:
            pass