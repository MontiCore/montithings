#!/bin/sh
ip=$(hostname -i)
exec /usr/src/app/DCPSInfoRepo -ORBListenEndpoints iiop://"$ip":12345 "$@"