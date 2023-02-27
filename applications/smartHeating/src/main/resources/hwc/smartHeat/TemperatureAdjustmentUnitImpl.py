from TemperatureAdjustmentUnitImplTOP import TemperatureAdjustmentUnitImplTOP

class TemperatureAdjustmentUnitImpl(TemperatureAdjustmentUnitImplTOP):

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["temp_overwrite_1"].temp = 0
        self._result.ports["temp_overwrite_1"].overwrite = False
        self._result.ports["temp_overwrite_2"].temp = 0
        self._result.ports["temp_overwrite_2"].overwrite = False
        self._result.ports["configuration"].config = ""

    def compute(self, port) -> None:
        if(port == "key_pad_input"):
            key = self._input.ports["key_pad_input"].value