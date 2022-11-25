<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("componentName", "protobufModule", "inPorts", "outPorts")}

<#assign NameHelper = tc.instantiate("montithings.generator.codegen.template.util.pythonComponent.NameHelper")>

import sys, time, json, uuid
from base64 import b64encode, b64decode
from IComputable import IComputable, GenericResult, GenericInput
from ${protobufModule} import *
from MQTTClient import MQTTConnector

class ${componentName}Input(GenericInput):
    # protobuf already provides methods respectively
    # reference: https://developers.google.com/protocol-buffers/docs/pythontutorial - last visited 12.8.22
    # if unclear, use help(xInput.ports[port_name]) or dir(xInput.ports[port_name])
    # note that because the mapping for ports is dynamic (MQTT instructions), a mapping at runtime is much harder in
    # static languages. Therefore the mapping is done with a dict and the port-name
    def __init__(self):
        self.uuid = uuid.uuid4() # initial UUID, can't be used to trace any messages
        self.ports = {}
<#list inPorts as port>
        self.ports["${port.name}"] = ${NameHelper.getLastPart(port.type.getTypeInfo().name)}()
</#list>

class ${componentName}Result(GenericResult):
    # protobuf already provides methods respectively
    # reference: https://developers.google.com/protocol-buffers/docs/pythontutorial - last visited 12.8.22
    # if unclear, use help(xResult.ports[port_name]) or dir(xResult.ports[port_name])
    def __init__(self):
        self.uuid = uuid.uuid4()
        self.ports = {}
<#list outPorts as port>
        self.ports["${port.name}"] = ${NameHelper.getLastPart(port.type.getTypeInfo().name)}()
</#list>

class ${componentName}ImplTOP(IComputable, MQTTConnector):
    # convenience dicts to lookup ports and their respective protobuf-types
    COMPONENT_PORTS_IN = {
<#list inPorts as port>
        "${port.name}": ${NameHelper.getLastPart(port.type.getTypeInfo().name)},
</#list>
    }
    COMPONENT_PORTS_OUT = {
<#list outPorts as port>
        "${port.name}": ${NameHelper.getLastPart(port.type.getTypeInfo().name)},
</#list>
    }

    _input = ${componentName}Input()
    _result = ${componentName}Result()
    serialize = lambda _,x: b64encode(x.SerializeToString()).decode("UTF-8")
    deserialize = lambda _,x: b64decode(x)

    # MQTTConnector implementation

    ports_in = set()
    ports_out = set()
    # after startup the component will receive instructions on which topics a port should listen to
    # especially: one port may listen to several topics
    connectors = {}

    def __init__(self, client_id, **kwargs) -> None:
        self.client_id = client_id # call the constructor of this ImplTOP in your __init__ and set the client_id
        for port in self.COMPONENT_PORTS_IN.keys():
            self.ports_in.add(".".join([client_id, port]))
        super().__init__(client_id=client_id + "_protobuf", **kwargs)

    def on_message(self, client, userdata, message) -> None:
        decoded_msg = message.payload.decode("utf-8")
        in_port = message.topic.split("/")[-1]

        payload_msg = self.deserialize(json.loads(decoded_msg)["value0"]["payload"]["data"]) # b64decode payload
        self._input.uuid = json.loads(decoded_msg)["value0"]["uuid"]
        if self.connectors.get(message.topic, False):
            self.connectors[message.topic].ParseFromString(payload_msg)
            self.compute(in_port)
        else:
            print(f"Received unroutable message on topic {message.topic}")

    def on_connect(self, client, obj, flags, rc) -> None:
        super().on_connect(client, obj, flags, rc)
        self.getInitialValues()
        for port in self.COMPONENT_PORTS_IN.keys():
            topic = f"/protobuf/{client.client_id}/{port}".replace(".", "/")
            print("subscribing to topic ", topic)
            self.subscribe(topic, qos=0)
            self.connectors[topic] = self._input.ports[port]

    # MQTT publish ports
<#list outPorts as port>
    def send_port_${port.name}(self) -> None:
        """publish the current value of _result.${port.name} to MQTT:/protobuf/<client/id>/${port.name}
        Use this in your hand-written-code to publish to the port ${port.name}"""
        self.publish(
            ".".join([self.client_id, "${port.name}"]),
            self._result.ports["${port.name}"]
        )
</#list>