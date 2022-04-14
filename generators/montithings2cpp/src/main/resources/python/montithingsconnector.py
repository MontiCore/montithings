# (c) https://github.com/MontiCore/monticore
import paho.mqtt.client as mqtt
import json
import parse_cmd
import uuid


class MontiThingsConnector:
    def __init__(self, topic_name, receive, broker_hostname='localhost', broker_port=1883, parse_cmd_args=False):
        if parse_cmd_args:
            broker_hostname, broker_port = parse_cmd.parse_cmd_args()
        self.topic_name = topic_name + '/' + str(uuid.uuid4()) #add uuid to topic name in order to have unique topic name
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
        self.mqttc.publish("/sensorActuator/config" + self.topic_name, '{"external":"' + self.topic_name + '"', qos=0)
        self.mqttc.subscribe("/sensorActuator/" + self.topic_name, qos=0)
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