# Installing the IoT-Manager



## Deployment
### Interacting with this repo on the server
On student@iotlab.se.rwth-aachen.de a deployment key for this repo is configured at `~/.ssh/id_ed25519_manager_deployment`
To interact with this repository from this server use

`GIT_SSH_COMMAND="ssh -o IdentitiesOnly=yes -i ~/.ssh/id_ed25519_manager_deployment -F /dev/null" git  (pull | ...)`

### Deployment using docker-compose
On student@iotlab.se.rwth-aachen.de a deployment token for this repo with `read_registry` rights for this repository is configured. Docker is authenticated to `registry.git.rwth-aachen.de` with that token. 

To deploy this application run at the root of this repo:
```
$ docker-compose pull
$ docker-compose up -d
```

### Automatic Deployment / CI/CD

In `.gitlab_ci.yml`, scripts to build images of individual services and to deploy the project to a server are configured.

First, the images are built on the Gitlab runner using a shell executor, as access to native docker is required.

Then the contents of this repo are deployed to iotlab.se.rwth-aachen.de.
This is done by executing commands on the server via ssh in the following main steps

1. Stop current services
    - `cd ~/iot_manager_deploy/iot-manager && docker-compose down --remove-orphans`
2. git reset to the current commit on master
    - `git checkout master && export GIT_SSH_COMMAND='ssh -o IdentitiesOnly=yes -i ~/.ssh/id_ed25519_manager_deployment -F /dev/null' && git fetch --all && git reset --hard origin/master`
3. Pull the updated containers from the container registry
    - `docker-compose pull`
4. Start the containers
    - `docker-compose up -d && exit`

The following secrets are required:
- `SSH_KNOWN_HOSTS`: The fingerprint of the server.
- `SSH_PRIVATE_KEY`: A private key that allows access to the account 'student@iotlab.se.rwth-aachen.de'