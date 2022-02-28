# (c) https://github.com/MontiCore/monticore

# %%
import helpers
import paho.mqtt.client as mqttc
import config
import json
import time
import threading

# %%
if __name__ == "__main__":
    print(f"Starting IoT-Client (ClientID={config.CLIENT_ID})")

    print(f"Connecting to MQTT broker... ({config.MQTT_BROKER_HOST}:{config.MQTT_BROKER_PORT})")
    mqtt = mqttc.Client(client_id=config.CLIENT_ID)
    while mqtt.connect(config.MQTT_BROKER_HOST, port=config.MQTT_BROKER_PORT) != mqttc.MQTT_ERR_SUCCESS:
        print("Connecting to MQTT-Broker failed. Retrying...")
        pass

    # Define all topics relevant for us. Variables starting with "my" are topics "owned" by this client.
    myTopicStatus = f"deployment/{config.CLIENT_ID}/status"
    myTopicConfig = f"deployment/{config.CLIENT_ID}/config"
    myTopicPush = f"deployment/{config.CLIENT_ID}/push"
    myTopicHeartbeat   = f"deployment/{config.CLIENT_ID}/heartbeat"
    topicPoll   = "deployment/poll"

    print("Initializing components...")
    mngr = helpers.ComposeManager("../run/deployment")

    curStatus = "idle"

    def getStatus():
        return {"status": curStatus}

    def publishStatus(newStatus = curStatus):
        global curStatus
        curStatus = newStatus
        # Send status update. The MQTT-Broker shall retain this message.
        mqtt.publish(myTopicStatus, payload=json.dumps(getStatus()), qos=1, retain=True)

    def publishConfig():
        # Send config update. The MQTT-Broker shall retain this message.
        mqtt.publish(myTopicConfig, payload=json.dumps(config.CLIENT_CONFIG), qos=1, retain=True)
        # We'll  also send a heart-beat, because the sending a config alone shall not imply that the client is online.
        sendHeartbeat()

    def handleDeploy(client, userdata, message:mqttc.MQTTMessage):
        print("Received deployment request!")
        if len(message.payload) > 30:
            # message contains new docker-compose instructions
            publishStatus("starting")
            ymlCompose = str(message.payload,encoding="utf-8")
            mngr.pushCompose(ymlCompose)
            publishStatus("running")
        else:
            # deployment should be revoked
            publishStatus("stopping")
            mngr.undeploy()
            publishStatus("idle")

    def sendHeartbeat():
        mqtt.publish(myTopicHeartbeat, qos=0, retain=False)

    def loopHeartbeat():
        while True:
            sendHeartbeat()
            time.sleep(config.HEARTBEAT_FREQUENCY)

    
    # %%
    def on_message(client, userdata, message:mqttc.MQTTMessage):
        if message.topic == myTopicPush:
            handleDeploy(client,userdata,message)
        elif message.topic == topicPoll:
            sendHeartbeat()
    mqtt.on_message = on_message

    # start sending heartbeats
    threading.Thread(target=loopHeartbeat,daemon=True).start()

    # publish current config & status
    publishConfig()
    publishStatus()

    # %%
    status, test = mqtt.subscribe([
        # Listen for new deployments
        (myTopicPush, 2),
        # Listen for poll
        (topicPoll, 1)
    ])
        
    print("Finished.")
    mqtt.loop_forever()