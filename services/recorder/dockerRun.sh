#!/bin/sh

docker run --rm --net montithings -v ${PWD}/docker_recordings:/usr/src/app/services/recorder/recordings monithings.recorder -i dcpsinforepo:12345
