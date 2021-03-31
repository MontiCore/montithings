#!/bin/sh

docker run --rm --net=host -v ${PWD}/docker_recordings:/usr/src/app/services/recorder/recordings monithings.recorder -i localhost:12345
