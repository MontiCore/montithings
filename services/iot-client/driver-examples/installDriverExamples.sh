# (c) https://github.com/MontiCore/monticore

# this script sets up services for a sensor- and an actuator driver.
# it serves as an example on how to set up your own drivers

# download driver examples
git clone --depth 1 --filter=blob:none --no-checkout https://github.com/MontiCore/montithings.git
cd montithings
git checkout develop -- services/iot-client/driver-examples
cd ..
mv ./montithings/services/iot-client/driver-examples/*.py ./python-scripts/
rm -rf ./montithings
export DRIVER_EXAMPLES_PATH=$(pwd)"/python-scripts"

# todo path

# set up sensor_example.py as a service
sudo mkdir /var/log/driver_examples/
sudo bash -c "echo \"[Unit]
Description=sensor driver example for montiThings
After=mosquitto.service
StartLimitIntervalSec=0
[Service]
ExecStart=/usr/bin/python3 -u $DRIVER_EXAMPLES_PATH/sensor_example.py --brokerPort 4230
Type=simple
StandardOutput=file:$DRIVER_EXAMPLES_PATH/sensor_standard.log
StandardError=file:$DRIVER_EXAMPLES_PATH/sensor_error.log
Restart=always
RestartSec=1
[Install]
WantedBy=multi-user.target
\" > /etc/systemd/system/sensor_example.service"

# set up actuator_example.py as a service
sudo bash -c "echo \"[Unit]
Description=actuator driver example for montiThings
After=mosquitto.service
StartLimitIntervalSec=0
[Service]
ExecStart=/usr/bin/python3 -u $DRIVER_EXAMPLES_PATH/actuator_example.py --brokerPort 4230
Type=simple
StandardOutput=file:$DRIVER_EXAMPLES_PATH/actuator_standard.log
StandardError=file:$DRIVER_EXAMPLES_PATH/actuator_error.log
Restart=always
RestartSec=1
[Install]
WantedBy=multi-user.target
\" > /etc/systemd/system/actuator_example.service"

sudo systemctl daemon-reload

sudo systemctl start actuator_example
sudo systemctl enable actuator_example

sudo systemctl start sensor_example
sudo systemctl enable sensor_example
