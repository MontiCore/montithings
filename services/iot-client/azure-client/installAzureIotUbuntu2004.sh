# (c) https://github.com/MontiCore/monticore
# configure your install here or provide connection string in command line argument $1
CONNECTION_STRING='PASTE_DEVICE_CONNECTION_STRING_HERE'
BROKER_PORT='4230'
IS_RASPBIAN=false # please note: only ARM32v7 is supported by azure iot edge as of now. see https://learn.microsoft.com/en-us/azure/iot-edge/support?view=iotedge-1.4#operating-systems

if [ -n "$1" ]
then
  CONNECTION_STRING="$1"
fi

if $IS_RASPBIAN
then
  # make sure date is correctly set
  sudo date -s "$(wget -qSO- --max-redirect=0 google.com 2>&1 | grep Date: | cut -d' ' -f5-8)Z"

  sudo apt-get update
  sudo apt-get -y install python3-pip

  curl https://packages.microsoft.com/config/debian/11/packages-microsoft-prod.deb > ./packages-microsoft-prod.deb
  sudo apt install ./packages-microsoft-prod.deb
  rm packages-microsoft-prod.deb
else
  wget https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
  sudo dpkg -i packages-microsoft-prod.deb
  rm packages-microsoft-prod.deb
fi

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

# download python scripts
git clone --depth 1 --filter=blob:none --no-checkout https://github.com/MontiCore/montithings.git
cd montithings
git checkout develop -- generators/montithings2cpp/src/main/resources/python
cd ..
mv ./montithings/generators/montithings2cpp/src/main/resources/python ./python-scripts
rm -rf ./montithings
PYTHON_SCRIPTS_PATH=$(pwd)"/python-scripts"

#install required python packages
sudo apt-get -y install python3-pip
sudo pip install -r python-scripts/requirements.txt

# configure docker
sudo bash -c "echo \"{
  \\\"log-driver\\\": \\\"local\\\",
  \\\"dns\\\": [\\\"1.1.1.1\\\"],
  \\\"log-opts\\\": {
      \\\"max-size\\\": \\\"10m\\\",
      \\\"max-file\\\": \\\"3\\\"
  }
}\" > /etc/docker/daemon.json"


# set up sensoractuatormanager.py as a service
sudo mkdir /var/log/sensoractuatormanager/
sudo bash -c "echo \"[Unit]
Description=sensor actuator manager for montiThings
After=mosquitto.service
StartLimitIntervalSec=0
[Service]
ExecStart=/usr/bin/python3 -u $PYTHON_SCRIPTS_PATH/sensoractuatormanager.py --brokerPort $BROKER_PORT
Type=simple
StandardOutput=file:/var/log/sensoractuatormanager/standard.log
StandardError=file:/var/log/sensoractuatormanager/error.log
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
