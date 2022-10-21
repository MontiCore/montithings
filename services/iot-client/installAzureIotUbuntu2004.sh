# configure your install here
CONNECTION_STRING='PASTE_DEVICE_CONNECTION_STRING_HERE'
BROKER_PORT='4230'

wget https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
rm packages-microsoft-prod.deb
sudo apt-get update
sudo apt-get -y install moby-engine

# install and configure mosquitto
sudo apt-get -y install mosquitto
sudo mkdir /etc/montithings
sudo bash -c "echo \"
persistence true
persistence_location /var/lib/mosquitto/
listener 4230 0.0.0.0
allow_anonymous true
\" > /etc/mosquitto/mosquitto.conf"
sudo service mosquitto restart

sudo bash -c "echo \"{
  \\\"log-driver\\\": \\\"local\\\",
  \\\"dns\\\": [\\\"1.1.1.1\\\"],
  \\\"log-opts\\\": {
      \\\"max-size\\\": \\\"10m\\\",
      \\\"max-file\\\": \\\"3\\\"
  }
}\" > /etc/docker/daemon.json"


# set up sensoractuatormanager.py as a service
sudo bash -c "echo \"[Unit]
Description=sensor actuator manager for montiThings
After=mosquitto.service
StartLimitIntervalSec=0
[Service]
ExecStart=/usr/bin/python3 $PYTHON_SCRIPTS_PATH/sensoractuatormanager.py --brokerPort $BROKER_PORT
Type=simple
Restart=always
RestartSec=1
[Install]
WantedBy=multi-user.target
\" > /etc/systemd/system/sensoractuatormanager.service"

sudo systemctl daemon-reload
sudo systemctl start sensoractuatormanager
sudo systemctl enable sensoractuatormanager


# install and configure azure iot edge
sudo apt-get -y update
sudo apt-get -y install aziot-edge defender-iot-micro-agent-edge
sudo iotedge config mp --connection-string "$CONNECTION_STRING"
sudo iotedge config apply -c '/etc/aziot/config.toml'
