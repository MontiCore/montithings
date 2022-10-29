# (c) https://github.com/MontiCore/monticore

############ configuration ###########
DRIVER_NAME="<name>"                 # name of the service that runs your driver
PATH_TO_PYTHON_SCRIPT="<enter path>" # the path to the python script to be run by the service
LOG_DIRECTORY="<enter path>"         # path where the logs of the service will be stored
######################################

if [[ $DRIVER_NAME == *" "* ]]; then
  echo "ERROR: Invalid driver name \"§DRIVER_NAME\""
  echo "driver name must not contain spaces"
  exit
fi

sudo mkdir "$LOG_DIRECTORY"
sudo bash -c "echo \"[Unit]
Description=$DRIVER_NAME
After=mosquitto.service
StartLimitIntervalSec=0
[Service]
ExecStart=/usr/bin/python3 -u $PATH_TO_PYTHON_SCRIPT --brokerPort 4230
Type=simple
StandardOutput=file:$LOG_DIRECTORY/${DRIVER_NAME}_standard.log
StandardError=file:$LOG_DIRECTORY/${DRIVER_NAME}_error.log
Restart=always
RestartSec=1
[Install]
WantedBy=multi-user.target
\" > /etc/systemd/system/${DRIVER_NAME}.service"

sudo systemctl daemon-reload
sudo systemctl start "$DRIVER_NAME"
sudo systemctl enable "$DRIVER_NAME"
