#!/bin/sh
ip=$(hostname -i)
exec opendds/bin/DCPSInfoRepo -ORBListenEndpoints iiop://"$ip":12345 "$@"