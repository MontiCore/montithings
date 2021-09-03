#!/bin/bash
ACCOUNT_NAME=iot

KUBECTL="sudo k3s kubectl"

echo "Creating service account '$ACCOUNT_NAME'..."
$KUBECTL create serviceaccount $ACCOUNT_NAME

echo "Extracting token..."
TOKEN=$($KUBECTL describe secrets "$($KUBECTL describe serviceaccount $ACCOUNT_NAME | grep -i Tokens | awk '{print $2}')" | grep token: | awk '{print $2}')

echo "Creating role assigments..."
$KUBECTL apply -f k3s-setup-data.yaml

if [ -n "$TOKEN" ]; then
  echo "Token for service account '$ACCOUNT_NAME':"  
  echo $TOKEN
else
  echo "Token konnte nicht erstellt werden."
fi
