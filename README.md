# IoT Manager

## Kubernetes (k8s / k3s)
Um Kubernetes Nodes in das Deployment einzuschließen, ist ein Service Account notwendig.
Das folgende Skript legt einen solchen Account an, konfiguriert dessen Zugriffsrechte und gibt anschließend das Access Token aus.

Zu Beachten ist, dass dieses Skript auf einer berechtigten Node ausgeführt werden muss; am besten auf einem Master.

```bash
cd ./setup/kubernetes
./setup.sh
```
