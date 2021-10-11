# (c) https://github.com/MontiCore/monticore
import paho.mqtt.client as mqtt
import json
import time
import datetime as dt
import ast
import parse_cmd


class SensorActuatorManager:
    def __init__(self, broker_hostname, broker_port):
        self.topics = dict()
        self.mqttc = mqtt.Client()
        self.connected = False
        self.connect_to_broker(broker_hostname, broker_port)
        self.wait_for_connection()

    def connect_to_broker(self, broker_hostname, broker_port):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect(broker_hostname, broker_port)
        self.mqttc.subscribe("/sensorActuator/config/#", qos=0)
        self.mqttc.loop_start()

    def on_connect(self, mqttc, obj, flags, rc):
        print("Connected SensorActuatorManager to MQTT broker.")
        self.connected = True

    def on_disconnect(self, client, userdata, rc):
        print("Disconnected SensorActuatorManager from MQTT broker.")
        self.connected = False

    def on_message(self, client, userdata, message):
        print("Got Message " + message.payload.decode("utf-8") + " on topic " + message.topic)
        json_dict = ast.literal_eval(message.payload.decode("utf-8"))
        if json_dict["occupiedBy"]:
            self.topics[message.topic] = (dt.datetime.now(), json_dict["occupiedBy"])

    def free_topic(self, topic):
        info = self.mqttc.publish(topic, '{"occupiedBy": "False"}', qos=1, retain=True)
        info.wait_for_publish()

    def wait_for_connection(self):
        while not self.connected:
            pass


if __name__ == '__main__':
    # Instantiate SensorActuatorManager
    host, port = parse_cmd.parse_cmd_args()
    mtc = SensorActuatorManager(host, port)

    while True:
        # Send a message to MontiThings (sensor port)
        time.sleep(5)
        for topic in mtc.topics:
            if(((dt.datetime.now() - mtc.topics[topic][0]).total_seconds() >= 10) & (mtc.topics[topic][1] != "False")):
                mtc.topics[topic] = (dt.datetime.now(), False)
                mtc.free_topic(topic)


