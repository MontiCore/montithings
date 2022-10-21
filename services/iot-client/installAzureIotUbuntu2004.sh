CONNECTION_STRING='PASTE_CONNECTION_STRING_HERE'

wget https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
rm packages-microsoft-prod.deb
sudo apt-get update
sudo apt-get -y install moby-engine
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


