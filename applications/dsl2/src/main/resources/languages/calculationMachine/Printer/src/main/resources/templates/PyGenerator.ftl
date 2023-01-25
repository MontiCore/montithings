# (c) https://github.com/MontiCore/monticore
<#setting locale="en_US">
<#assign offsets=[64,59,55,50,45,40]>
<#assign totalLength=0>
<#assign beatsPerBar=4.0>

from PrinterImplTOP import PrinterImplTOP

class PrinterImpl(PrinterImplTOP):

    def __init__(self):
        super().__init__(
            client_id="calculationMachine.Machine.print", 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["text"].text = ""

    def compute(self, port) -> None:
        print(f"New value on port {port}: {self._input.ports['result'].var}, {self._input.ports['result'].val}, {self._input.ports['result'].calc}")

        text = "";
        variable = "${ast.getVariable().getVariableName()}"
        style = "${ast.getStyle().getChoice()}"

        if(style == "Seperated"):
            text = variable
            text = f"({text} {self._input.ports['result'].calc})"
            text = f"{text} = {self._input.ports['result'].val} if {variable} is {self._input.ports['result'].var}"
        else:
            text = variable
            text = f"({text} {self._input.ports['result'].calc})\n"
            text = f"{text} For {variable} = {self._input.ports['result'].var} this results in {self._input.ports['result'].val}."
        
        print(text)
        self._result.ports["text"].text = text
        self.send_port_text()

        




