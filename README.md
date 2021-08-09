# IoT Manager

## Kubernetes (k8s / k3s)
Um Kubernetes Nodes in das Deployment einzuschließen, ist ein Service Account notwendig.
Das folgende Skript legt einen solchen Account an, konfiguriert dessen Zugriffsrechte und gibt anschließend das Access Token aus.

Zu Beachten ist, dass dieses Skript auf einer berechtigten Node ausgeführt werden muss; am besten auf einem Master.

```bash
cd ./setup/kubernetes
./setup.sh
```

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
