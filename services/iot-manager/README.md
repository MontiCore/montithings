<!-- (c) https://github.com/MontiCore/monticore -->
# IoT Manager

## Allgemeines
Der IoT Manager kümmert sich um die interne Orchestrierung des Deployments.
Er kann mittels HTTP und MQTT kommunizieren und gesteuert werden.
Im originalen Anwendungsfall kommuniziert dieser mit dem [MontiGem-Backend](https://git.rwth-aachen.de/se-student/theses/ba-schneider_philipp/exampleapplication).

Alternativ kann der Deployment-Manager auch in andere Java-Anwendungen integriert werden. 
Betrachte dazu den Einstiegspunkt des IoT-Managers.

## Kubernetes (k8s / k3s)
Um Kubernetes Nodes in das Deployment einzuschließen, ist ein Service Account notwendig.
Das folgende Skript legt einen solchen Account an, konfiguriert dessen Zugriffsrechte und gibt anschließend das Access Token aus.

Zu Beachten ist, dass dieses Skript auf einer berechtigten Node ausgeführt werden muss; am besten auf einem Master.

```bash
cd ./setup/kubernetes
./setup.sh
```

Nodes können folgende Zusatzinformationen über Labels zugewiesen werden:
1. Ihre Position (building, floor, room) <b>muss</b> spezifiziert sein.
2. Hardware <b>kann</b> über labels deren Key mit "hardware" beginnt spezifiziert werden.

Hierfür existiert ebenfalls ein Script, welches ebenfalls auf einer berechtigten Node ausgeführt werden muss. Dieses fragt nach und nach die notwendigen Informationen ab.
```bash
cd ./Setup/kubernetes
./setupClient.sh
```
```
Node-ID: iot4
Please enter the location of the node.
Building: 001
Floor: 002
Room: 003
Please enter the available hardware on the node. When finished, submit empty line.
Hardware: actuatorTemperature
Hardware: 
The node iot4 is located in building 001 on floor 002 in room 003.
It is equipped with the following hardware:
- actuatorTemperature
Is this correct? (Yes = Press enter, No = Terminate)
 
Applying configuration...
[...]
Client has been configured successfully.
```

<b>Hinweis:</b> Die Skipts sind auf k3s ausgelegt. Sollte ein anderes kubectl verwendet werden, so kann dies mittels der Variable $KUBECTL angepasst werden.

## GeneSIS
Um GeneSIS Hosts in das Deployment einzuschließen, kann der API-Endpunkt GeneSIS <b>Engine</b> genutzt werden.
Für das Deployment werden ausschließlich Hosts beachtet, welche die folgenden Kriterien erfüllen:
1. Der Host ist vom Typ "/infra/device", wird also per SSH angesprochen.
2. Die Eigenschaft "device_type" des Hosts ist ein JSON-formatierter String mit (mindestens) den folgenden Eingenschaften ("hardware" ist optional):
```JSON
{
    "building": "[...]",
    "floor": "[...]",
    "room": "[...]",
    "hardware": [
        "sensorTemperature"
    ]
}
```

Um die Hosts zu registrieren, kann die grafische Oberfläche von GeneSIS genutzt werden.
Dort kann man unter "Infrastructure Components" > "Device" einen Host hinzufügen.
Hat man dort die gewünschten Geräte hinzugefügt, kann man mittels <i>Push model to server</i> <b>und</b> <i>Deploy model on server</i> die Änderungen anwenden.

Hinweis: Wenn man Änderungen an einer Komponente  vornehmen möchte, muss die Versionsnummer dieser inkrementiert werden, sonst werden ggf. Änderungen nicht angewendet.
