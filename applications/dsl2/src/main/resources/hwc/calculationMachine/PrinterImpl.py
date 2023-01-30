# (c) https://github.com/MontiCore/monticore

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
        
        print(f"The Result of x{self._input.ports['result'].calc} where x is {self._input.ports['result'].var} is {self._input.ports['result'].val}!")
        self._result.ports["text"].text = f"f(X) = {self._input.ports['result'].val} if X = {self._input.ports['result'].var}"

        self.send_port_text()