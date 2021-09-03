# IoT Client
Der IoT-Client ist das Gegenstück zum "Basic Deployment Target Provider".
Er läuft auf den IoT-Geräten, auf welchen die Komponenten laufen sollen.

## Abhänigkeiten
- Docker Engine & CLI
- Python 3

## Setup
1. Sicher stellen, dass alle Abhänigkeiten installiert sind.
2. Mittels ```pip3 install -r requirements.txt``` alle Abhängigkeiten für die Python-Anwendung installieren.
3. Config-Datei (src/config.py) nach Wünschen anpassen (MQTT Broker und Geräteeigenschaften festlegen).
4. Der IoT-Client kann nun mittels ```./start.sh``` gestartet werden.

Beispiel für einen erfolgreichen Start:
```
Starting IoT-Client (ClientID=c96a71a5e574)
Connecting to MQTT broker... (node4.se.rwth-aachen.de:1883)
Initializing components...
Finished.
```

## Dokumentation
Die Dokumentation ist [hier](docs/overview.md) zu finden.
