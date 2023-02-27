# (c) https://github.com/MontiCore/monticore
<#setting locale="en_US">
<#assign offsets=[64,59,55,50,45,40]>
<#assign totalLength=0>
<#assign beatsPerBar=4.0>

from SinkImplTOP import SinkImplTOP

class SinkImpl(SinkImplTOP):

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName,
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["ret"].num = 0

    def compute(self, port) -> None:
        print(f"New values on port {port}:")
        print(f"{self._input.ports['value'].text} {self._input.ports['value'].val} | {self._input.ports['value'].num}")
        # manipulate the visitor field  
        workingVal = self._input.ports["value"].num

        <#list ast.getComputationList() as computes>
            <#if computes.isPresentAdd()>
        workingVal = workingVal + ${computes.getAdd().getValue()}
            </#if>
            <#if computes.isPresentMult()>
        workingVal = workingVal * ${computes.getMult().getValue()}
            </#if>
        </#list>

        self._result.ports["ret"].num = workingVal

        # send the current state of visitor on /ports/unlock/FaceUnlock/faceid/visitor
        self.send_port_ret()





