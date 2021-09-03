# IoT Client
The IoT client is the counterpart to the "Basic Deployment Target Provider".
It runs on the IoT devices on which the components are to be executed.

## Dependencies
- Docker Engine & CLI
- Python 3

## Setup
1. Make sure that docker and python are installed.
2. Using ``pip3 install -r requirements.txt`` install all dependencies for the Python application.
3. Customize config file (src/config.py) as desired (set MQTT broker and device properties).
4. The IoT client can now be started using ``./start.sh``.

Example of a successful start:
```
Starting IoT-Client (ClientID=c96a71a5e574)
Connecting to MQTT broker... (node4.se.rwth-aachen.de:1883)
Initializing components...
Finished.
```

## Documentation
The docs are [here](docs/overview.md).
