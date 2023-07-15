import subprocess
import time
import bluetooth
import paho.mqtt.client as mqtt
import re

ip_address = "192.168.0.10"

def get_ip_address(ifname):
    output = subprocess.check_output(["ip", "addr", "show", ifname]).decode()
    match = re.search(r"inet (\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})", output)
    if match:
        ip_address = match.group(1)
        return ip_address
    else:
        return None

while True:
    # Step 1: Check if MQTT broker is running
    mqtt_running = False

    ip_adress_wlan0 = get_ip_address("wlan0")
    # Test if MQTT broker is running
    if (ip_adress_wlan0 is not None):
        mqtt_running = True
        if (ip_adress_wlan0 == ip_address):
            print("Access Point running on this device.")
        else:
            print("MQTT broker already running on different access point, connecting to existing broker...")

    other_device_mac = None
    mac_address = bluetooth.read_local_bdaddr()[0]

    # Step 2: Turn on Bluetooth and wait for other devices
    if (not mqtt_running):
        bluetooth_on = False
        tries = 0
        while not bluetooth_on and tries < 3:
            try:
                # Turn on Bluetooth and start scanning for nearby devices
                subprocess.check_output(["sudo", "hciconfig", "hci0", "piscan"])
                devices = bluetooth.discover_devices(duration=8, lookup_names=True)
                print("Bluetooth on and scanning for nearby devices...")
                bluetooth_on = True
            except bluetooth.btcommon.BluetoothError:
                print("Bluetooth not ready yet, waiting...")
                tries = tries + 1
                time.sleep(5)

        # Step 3: Compare MAC addresses and start MQTT broker if necessary
        if (bluetooth_on):
            for addr, name in devices:
                if name.startswith('iot') and addr < mac_address:
                    other_device_mac = addr
                    break

        if other_device_mac is not None:
            continue
        else:
            print(f"Found no devices with lower MAC address")
            print("Starting Access Point...")

            dhcpcdConfigFile = open('/etc/dhcpcd.conf', 'a')
            dhcpcdConfigFile.write("interface wlan0\n")
            dhcpcdConfigFile.write("static ip_address=192.168.0.10/24\n")
            dhcpcdConfigFile.write("denyinterfaces eth0\n")
            dhcpcdConfigFile.write("denyinterfaces wlan0\n")
            dhcpcdConfigFile.close()

            subprocess.run(["sudo", "systemctl", "start", "hostapd"])
            subprocess.run(["sudo", "systemctl", "restart", "hostapd"])

            # Start Compatibility Manager
            subprocess.run(["python3", "compatibilitymanager.py"])

            # Reset dhcpcd file in order to be able to connect to other access points
            subprocess.run(["sudo", "sed", "-i", "/interface wlan0/,$d", "/etc/dhcpcd.conf"])

            subprocess.run(["sudo", "systemctl", "stop", "hostapd"])

    else:

        # Maybe nothing has to be done here, because we're always sending our heartbeat to the same ip anyways

        # Connect to MQTT broker
        broker_address = ip_address
        client = mqtt.Client()
        client.connect(broker_address)

        # Publish message to broker
        message = f"Hello from device with MAC address {mac_address} and ip {ip_address}"
        topic = "discovery"
        client.publish(topic, message)
        print(f"Message published to topic {topic}: {message}")
        client.disconnect()
        break
