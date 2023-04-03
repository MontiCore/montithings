# (c) https://github.com/MontiCore/monticore
import paho.mqtt.client as mqtt
import json
import time
import datetime as dt
import ast
import parse_cmd


class CompatibilityBroker:
    def __init__(self, broker_hostname, broker_port):
        self.topics = dict()
        self.waitingForAssignments = dict()
        self.mqttc = mqtt.Client()
        self.connected = False
        self.connect_to_broker(broker_hostname, broker_port)
        self.wait_for_connection()

    def connect_to_broker(self, broker_hostname, broker_port):
        self.mqttc.on_message = self.on_message
        self.mqttc.on_connect = self.on_connect
        self.mqttc.on_disconnect = self.on_disconnect
        self.mqttc.connect(broker_hostname, broker_port)
        self.mqttc.subscribe("/compatibility/#", qos=0)
        self.mqttc.loop_start()

    def on_connect(self, mqttc, obj, flags, rc):
        print("Connected CompatibilityBroker to MQTT broker.")
        self.connected = True

    def on_disconnect(self, client, userdata, rc):
        print("Disconnected CompatibilityBroker from MQTT broker.")
        self.connected = False

    def on_message(self, client, userdata, message):

        # get the type of message that was received
        messageTopic = message.topic.split("/")[2]

        print("Got Message " + message.payload.decode("utf-8") + " on topic " + message.topic)
        json_dict = ast.literal_eval(message.payload.decode("utf-8"))

        if messageTopic == "heartbeat":
            if json_dict["occupiedBy"] != "False":
                # refresh mapping of topic with current time and received component instance
                self.topics[(json_dict["type"],message.topic.split("/")[3])] = (dt.datetime.now(), json_dict["occupiedBy"])

        elif messageTopic == "component":

            requestedType = json_dict['requestedType']
            offeredType = json_dict['offeredType']

            # check whether there exists an uuid for a certain topic already
            exists = False
            for topic in self.topics:
                if requestedType == topic[0]:
                    if not self.topics[topic][1]:
                        # topic is not occupied yet
                        self.send_response(instancePortName, topic[1], topic[0])
                        exists = True
                        break

            if not exists:
                # add component instance to instances waiting for connections
                if requestedType in self.waitingForAssignments:
                    self.waitingForAssignments[requestedType].append(instancePortName)
                else:
                    self.waitingForAssignments[requestedType] = [instancePortName]

        elif messageTopic == "offer":
            topic_uuid = json_dict['topic']
            offeredType = json_dict['spec']['type']

            # add entry to topic mapping with the offered type
            self.topics[(offeredType, topic_uuid)] = (dt.datetime.now(), False)

            # check whether component instances are waiting for assignments with the offered type
            if offeredType in self.waitingForAssignments:
                instancePortName = self.waitingForAssignments[offeredType][0]

                self.waitingForAssignments[offeredType].pop(0)
                if not self.waitingForAssignments[offeredType]:
                    # remove type from dict if there are no component instances waiting for an assignment to this type
                    self.waitingForAssignments.pop(offeredType)

                # send response to component instance which requested connection
                self.mqttc.publish("/sensorActuator/response/" + instancePortName, message.payload, qos=1)
                # update mapping
                self.topics[(offeredType, topic_uuid)] = (dt.datetime.now(), instancePortName)

    def free_topic(self, topic):
        info = self.mqttc.publish(topic, '{"occupiedBy": "False"}', qos=1, retain=True)
        info.wait_for_publish()

    def send_response(self, instancePortName, topic, typeName):
        responseTopic = '/sensorActuator/response/' + instancePortName

        # build message
        spec = '"spec":{"type":"' + typeName + '"}'
        responseMessage = '{"topic": "' + topic + '",' + spec + '}'

        # send response to component instance which requested connection
        self.mqttc.publish(responseTopic, responseMessage, qos=1)

        # update topic mapping
        self.topics[(typeName, topic)] = (dt.datetime.now(), instancePortName)

    def wait_for_connection(self):
        while not self.connected:
            pass


if __name__ == '__main__':
    # Instantiate CompatibilityBroker
    host, port = parse_cmd.parse_cmd_args()
    mtc = CompatibilityBroker(host, port)

    while True:
        time.sleep(5)
        for topic in mtc.topics:
            # check if a new message was sent to this topic in the last 10 seconds even though it is free
            if(((dt.datetime.now() - mtc.topics[topic][0]).total_seconds() >= 10) and (mtc.topics[topic][1] != "False")):
                mtc.topics[topic] = (dt.datetime.now(), False)
                mtc.free_topic("/sensorActuator/heartbeat/" + topic[1])

                # check if someone is waiting for assignment to the type which was freed
                offeredType = topic[0]
                if offeredType in mtc.waitingForAssignments:
                    instancePortName = mtc.waitingForAssignments[offeredType][0]

                    mtc.waitingForAssignments[offeredType].pop(0)
                    if not mtc.waitingForAssignments[offeredType]:
                        # remove type from dict if there are no component instances waiting for an assignment to this type
                        mtc.waitingForAssignments.pop(offeredType)

                    # send response to component instance which is waiting for connection
                    mtc.send_response(instancePortName, topic[1], topic[0])
