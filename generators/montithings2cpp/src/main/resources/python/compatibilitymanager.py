import paho.mqtt.client as mqtt
import json
import time
import copy

# List of component types and their requirements
components = {}
# Keep track of time since last message
last_message_time = time.time()

# Define MQTT client and callback functions
client = mqtt.Client()
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.subscribe("component_offer")

def on_message(client, userdata, msg):
    try:
        data = json.loads(msg.payload.decode())
        component_name = data["component_name"]
        component_type = data["component_type"]
        requirements = data["requirements"]
        components[component_name] = {"requirements": requirements, "component_type": component_type}
        print(f"Received component offering: {component_name}: {component_type} - {requirements}")
    except Exception as e:
        print(f"Error parsing message: {str(e)}")

# Connect to MQTT broker and start listening for messages
client.on_connect = on_connect
client.on_message = on_message
client.connect("localhost")
client.loop_start()


old_components = {}
while True:

    if (old_components != components):
        last_message_time = time.time()
        print(f"Set last message time to: {last_message_time}")
    old_components = copy.copy(components)

    # Check for matches between offerings and requirements
    components_to_be_deleted = []
    for component_name, component_data in components.items():
        requirements = component_data["requirements"]
        component_type = component_data["component_type"]
        for offered_name, offered_data in components.items():
            if (component_name == offered_name):
                continue
            offered_type = offered_data["component_type"]

            if offered_type in requirements:
                print(f"Match found between {component_type} and {offered_type}")
                client.publish(f"component_match/{component_name}", f"Match found with {offered_name}")
                components_to_be_deleted.append(component_name)
                components_to_be_deleted.append(offered_name)

    for component_name in components_to_be_deleted:
        del components[component_name]

    # Check if it's been more than a minute since the last message
    if time.time() - last_message_time > 60:
        print(f"Last message time: {last_message_time}")
        print("No messages received for 1 minute, stopping compatibilitymanager")
        break

    # Wait a short time before checking for messages again
    time.sleep(0.1)

# Disconnect from MQTT broker
client.loop_stop()
client.disconnect()