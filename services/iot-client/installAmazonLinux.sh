#!/bin/bash
# (c) https://github.com/MontiCore/monticore
sudo yum update -y
sudo yum install python37

sudo amazon-linux-extras install -y epel 
sudo yum install -y mosquitto 
sudo systemctl start mosquitto

sudo amazon-linux-extras install -y docker
sudo systemctl start docker
sudo usermod -a -G docker ec2-user
curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

sudo yum install -y git
git clone --depth 1 --filter=blob:none --sparse https://github.com/monticore/montithings
cd montithings
git sparse-checkout set services/iot-client
cd services/iot-client/
pip3 install -r requirements.txt
