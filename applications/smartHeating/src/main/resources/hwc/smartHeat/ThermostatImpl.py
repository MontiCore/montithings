# (c) https://github.com/MontiCore/monticore
from ThermostatImplTOP import ThermostatImplTOP

class ThermostatImpl(ThermostatImplTOP):

    active = True
    overwrite = False
    overwrite_temp = 0
    movement_active = False

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["temperature_setting"].temp = 0

    def compute(self, port) -> None:
        if port == "clock":
            self._result.ports["temperature_setting"].temp = 0
            self.send_port_temperature_setting()