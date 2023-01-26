# (c) https://github.com/MontiCore/monticore

from CalculatorImplTOP import CalculatorImplTOP

class CalculatorImpl(CalculatorImplTOP):

    def __init__(self):
        super().__init__(
            client_id="calculationMachine.Machine.calc",
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["result"].val = 0
        self._result.ports["result"].calc = ""

    def compute(self, port) -> None:
        print(f"New value on port {port}: {self._input.ports['value'].val}")
        
        workingVal = self._input.ports["value"].val
        calculation = ""

        self._result.ports["result"].val = workingVal
        self._result.ports["result"].calc = calculation
        self._result.ports["result"].var = self._input.ports["value"].val

        self.send_port_result()