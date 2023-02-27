# (c) https://github.com/MontiCore/monticore
<#setting locale="en_US">
<#assign offsets=[64,59,55,50,45,40]>
<#assign totalLength=0>
<#assign beatsPerBar=4.0>

from CalculatorImplTOP import CalculatorImplTOP

class CalculatorImpl(CalculatorImplTOP):

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName,
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["result"].val = 0
        self._result.ports["result"].calc = ""

    def compute(self, port) -> None:
        print(f"New value on port {port}: {self._input.ports['value'].val}")
        
        workingVal = self._input.ports["value"].val
        calculation = ""

        <#list ast.getComputationList() as computes>
            <#if computes.isPresentAdd()>
        workingVal = workingVal + ${computes.getAdd().getValue()}
        calculation = f"{calculation}+ {${computes.getAdd().getValue()}} \n"
            </#if>
            <#if computes.isPresentMult()>
        workingVal = workingVal * ${computes.getMult().getValue()}
        calculation = f"{calculation}* {${computes.getMult().getValue()}} \n"
            </#if>
        </#list>

        self._result.ports["result"].val = workingVal
        self._result.ports["result"].calc = calculation
        self._result.ports["result"].var = self._input.ports["value"].val

        self.send_port_result()





