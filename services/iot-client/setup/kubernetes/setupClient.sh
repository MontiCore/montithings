#!/bin/bash
set -e

KUBECTL="sudo k3s kubectl"

while [ -z "$nodeID" ]; do
    printf "Node-ID: "
    read nodeID
done

echo "Please enter the location of the node."
while [ -z "$building" ]; do
    printf "Building: "
    read building
done
while [ -z "$floor" ]; do
    printf "Floor: "
    read floor
done
while [ -z "$room" ]; do
    printf "Room: "
    read room
done

echo "Please enter the available hardware on the node. When finished, submit empty line."
hwList=()
while : ; do
    printf "Hardware: "
    read hwTmp
    # Break on empty input
    [ -z "$hwTmp" ] && break
    hwList+=($hwTmp)
done

echo "The node $nodeID is located in building $building on floor $floor in room $room."
echo "It is equipped with the following hardware:"
for hw in "${hwList[@]}"; do
  echo "- $hw"
done
echo "Is this correct? (Yes = Press enter, No = Terminate)"
read

echo "Applying configuration..."

$KUBECTL label nodes $nodeID building=$building --overwrite=true
$KUBECTL label nodes $nodeID floor=$floor --overwrite=true
$KUBECTL label nodes $nodeID room=$room --overwrite=true

i=0
for hw in "${hwList[@]}"; do
  $KUBECTL label nodes $nodeID hardware$i=$hw --overwrite=true
  ((i=i+1))
done

echo "Client has been configured successfully."
