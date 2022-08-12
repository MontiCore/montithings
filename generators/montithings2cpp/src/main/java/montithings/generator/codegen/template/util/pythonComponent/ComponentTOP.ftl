${tc.signature("componentName", "protobufModule", "inPorts", "outPorts")}
inports= [
    "unlock.FaceUnlock.faceid.visitor"
]

from python.IComputable import IComputable, GenericResult, GenericInput
from python.unlock.${protobufModule} import *
# TODO: get unlock.FaceUnlock parameter

class ${componentName}Input(GenericInput):
    # protobuf already provides methods respectively
    # reference: https://developers.google.com/protocol-buffers/docs/pythontutorial - last visited 12.8.22
    # if unclear, use help(xInput.ports[port_name]) or dir(xInput.ports[port_name])
    # note that because the mapping for ports is dynamic (MQTT instructions), a mapping at runtime is much harder in
    # static languages. Therefore the mapping is done with a dict and the port-name
    def __init__(self):
        ports = {}
<#list inPorts as port>
        ports["${port.name}"] = ${port.type.print()}()
</#list>

class ${componentName}Result(GenericResult):
    # protobuf already provides methods respectively
    # reference: https://developers.google.com/protocol-buffers/docs/pythontutorial - last visited 12.8.22
    # if unclear, use help(xResult.port_name) or dir(xResult.port_name)
    def __init__(self):
        self.uuid = uuid4()
        ports = {}
<#list outPorts as port>
        ports["${port.name}"] = ${port.type.print()}()
</#list>

class ${componentName}ImplTOP(IComputable, MQTTConnector):
    # convenience dicts to lookup ports and their respective protobuf-types
    COMPONENT_PORTS_IN = {
<#list inPorts as port>
        "${port.name}": ${port.type.print()},
</#list>
    }
    COMPONENT_PORTS_OUT = {
<#list outPorts as port>
        "${port.name}": ${port.type.print()},
</#list>
    }

    _input = ${componentName}Input()
    _result = ${componentName}Result()
    serialize = lambda x: b64encode(x.serializeToString()).decode("UTF-8")
    deserialize = lambda x: b64decode(x)

<#list outPorts as port>
    def sendPort${port.name}(self) -> ${componentName}Result:
        """publish the current value of _result.${port.name} to MQTT:/ports/...
        Use this in your hand-written-code to publish to the port ${port.name}"""
        self.publish(
            "${port.getFullName()}",
            _result.ports["${port.name}"]
        )
</#list>

    ports_in = []
    ports_out = COMPONENT_PORTS_IN.keys()
    # after startup the component will receive instructions on which topics a port should listen to
    # especially: one port may listen to several topics
    connectors = {}

    def on_message(self, client, userdata, message):
        decoded_msg = message.payload.decode("utf-8")
        port = {message.topic.split("/")[-1]}
        if message.topic.startswith("/connectors/"): # TODO: only subscribe on correct connectors
            topic = f"/ports/{decoded_msg}".replace(".", "/")
            print(port, "listening on", topic)
            self.subscribe(topic, qos=0)
            self.connectors[topic] = self._input.ports[port]
        else:
            payload_msg = deserialize(json.loads(decoded_msg)["value0"]["payload"]["data"]) # b64decode payload
            payload_uuid = json.loads(decoded_msg)["value0"]["uuid"]
            if self.connectors.get(message.topic, False):
                self.published_on_port = port # possibly racy, when compute is not finished before next message enters
                self.compute(self.connectors[message.topic].deserializeFromString(payload_msg), payload_uuid)
            else:
                print(f"Received unroutable message on topic {message.topic}")

    def on_connect(self, client, obj, flags, rc):
        connect = super().on_connect(client, obj, flags, rc)
        # TODO?: Publish new active component /components unlock/FaceUnlock/faceid
        # TODO: handle getInitialValues
        return connect