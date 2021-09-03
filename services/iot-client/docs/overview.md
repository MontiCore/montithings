# IoT Client: Dokumentation
Der IoT Client kommuniziert mittels MQTT mit einem Deployment Server, bzw. einem "Basic Deployment Target Provider" auf dem Deployment Server.

Dieser verwaltet die IoT Clients und sendet ihnen Befehle.

## Client ID
Jeder IoT Client besitzt eine eindeutige ID.
Diese kann auf Wunsch manuell festgelegt werden;
Standardmäßig wird allerdings eine MAC-Adresse des ausführenden Computers gewählt.
Über diese ID kann der Deployment Server die einzelnen Clients individuell ansprechen.

## Client Location
Der Standort eines IoT Clients wird mittels dem Tripel <b>building, floor & room</b> beschrieben.
Jede Komponente des Tripels ist dabei ein beliebiger String.

## MQTT Topics
| Topic                             | Sender          | Beschreibung                    |
|-----------------------------------|-----------------|---------------------------------|
| deployment/<i>clientID</i>/status | Client | Status-Updates (s.u.)                    |
| deployment/<i>clientID</i>/config | Client | Hardwarekonfiguration des Clients (s.u.) |
| deployment/<i>clientID</i>/heatbeat| Client| Heartbeat für den Server Watchdog        |
| deployment/<i>clientID</i>/push   | Server | Anweisungen für Deployment auf Client    |
| deployment/poll                   | Server | siehe unten                              |

Ein MQTT Topic wird von allen IoT-Clients abboniert: <b>deployment/poll</b>.
Empfängt ein IoT Client eine Nachricht auf diesem Topic, so sendet er seinen aktuellen Status auf seinem zugehörigen Topic.

## Status
Der Status von IoT Clients wird in einer einfachen JSON Nachricht gesendet. Ein Beispiel:
```json
{"status":"idle"}
```

## Heartbeat
In regelmäßigen Abständen sendet der IoT Client einen Heartbeat um zu signalisieren, dass der noch online und verfügbar ist.
Dabei handelt es sich um eine leere MQTT-Nachricht an das Heartbeat-Topic des Clients.

Wird zu lange kein Heartbeat des Clients empfangen, so wird er von dem Watchdog des Deployment Server als Offline angesehen und aus dem Deployment entfernt.

## Push
Der Deployment Server kann dem IoT Client mit einer Push-Nachricht dazu beauftragen, neue Komponenten zu starten, bzw. laufende Komponenten zu stoppen.
Dazu sendet er eine Nachricht mit einer Docker-Compose Datei.
Diese wird vom Client mittels ```docker-compose``` eingelesen und das aktive Deployment entsprechend angepasst.

## Docker Compose
Eine Docker-Compose Datei beschreibt <b>alle</b> Komponenten, die Zeitgleich auf <b>einem</b> Client als Docker Container ausgeführt werden sollen.
Dabei kann ```docker-compose``` die Differenzen zwischen einer vorherigen Docker-Compose Datei und einer neuen ermitteln und nur diese anwenden.

Beispiel für eine Docker-Compose Datei. Es werden drei Komponenten auf diesem Client gestartet.
```yaml
version: '3.7'
services:
  hierarchy.IOTest.tempSensor:
    command: --name hierarchy.IOTest.tempSensor [...]
    image: registry.git.rwth-aachen.de/.../hierarchy.temperaturesensor
    network_mode: host
    restart: always
  hierarchy.IOTest:
    command: --name hierarchy.IOTest [...]
    image: registry.git.rwth-aachen.de/.../hierarchy.iotest
    network_mode: host
    restart: always
  hierarchy.IOTest.avg:
    command: --name hierarchy.IOTest.avg [...]
    image: registry.git.rwth-aachen.de/.../hierarchy.averager
    network_mode: host
    restart: always
```
