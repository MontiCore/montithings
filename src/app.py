# %%
import helpers
import paho.mqtt.client as mqttc
import config
import json
import time
import threading

# %%
if __name__ == "__main__":
    mqtt = mqttc.Client(client_id=config.CLIENT_ID)

    while mqtt.connect(config.MQTT_BROKER_HOST) != mqttc.MQTT_ERR_SUCCESS:
        print("Connecting to MQTT-Broker failed. Retrying...")
        pass

    myTopicStatus = f"deployment/{config.CLIENT_ID}/status"
    myTopicConfig = f"deployment/{config.CLIENT_ID}/config"
    myTopicPush = f"deployment/{config.CLIENT_ID}/push"
    myTopicStop   = f"deployment/{config.CLIENT_ID}/stop"
    myTopicHeartbeat   = f"deployment/{config.CLIENT_ID}/heartbeat"
    topicPoll   = f"deployment/poll"

    mngr = helpers.ComposeManager("../run/deployment")

    def getStatus():
        return {"status":"idle"}

    def publishStatus():
        # Send status update. The MQTT-Broker shall retain this message.
        mqtt.publish(myTopicStatus, payload=json.dumps(getStatus()), qos=1, retain=True)

    def publishConfig():
        # Send config update. The MQTT-Broker shall retain this message.
        mqtt.publish(myTopicConfig, payload=json.dumps(config.CLIENT_CONFIG), qos=1, retain=True)
        # We'll  also send a heart-beat, because the sending a config alone shall not imply that the client is online.
        sendHeartbeat()

    def handleDeploy(client, userdata, message:mqttc.MQTTMessage):
        print("Received deployment request!")
        # TODO update status to deploying...
        if len(message.payload) > 0:
            # message contains new docker-compose instructions
            publishStatus()
            ymlCompose = str(message.payload,encoding="utf-8")
            mngr.pushCompose(ymlCompose)
            publishStatus()
        else:
            # deployment should be revoked
            publishStatus()
            mngr.undeploy()
            publishStatus()

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
        elif message.topic == myTopicStop:
            pass
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
        # Listen for stopping request
        (myTopicStop,   2),
        # Listen for poll
        (topicPoll, 1)
    ])
        

    mqtt.loop_forever()
    
# %%
