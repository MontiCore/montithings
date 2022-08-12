${tc.signature("componentName")}

import sys
import time
import json
from base64 import b64encode, b64decode
from MQTTClient import MQTTConnector

from ${componentName}Impl import ${componentName}Impl
from ${componentName}ImplTOP import ${componentName}Input, ${componentName}Result

PROTO_CLASS = int
proto = PROTO_CLASS()

COMPONENT_IMPL = ${componentName}Impl
COMPONENT_INPUT = ${componentName}Input
COMPONENT_RESULT = ${componentName}Result

if __name__=="__main__":
    connector = ProtoConnector()

    # Block forever
    connector.connect()