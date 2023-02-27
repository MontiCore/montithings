from MovementSensorImplTOP import MovementSensorImplTOP

class MovementSensorImpl(MovementSensorImplTOP):

    def __init__(self,instanceName):
        super().__init__(
            client_id=instanceName, 
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["activation_room_1"].state = False
        self._result.ports["activation_room_2"].state = False

    def compute(self, port) -> None:
        sensor_id = self._input.ports["movement_sensor_value"].sensor_id
        activation_value = self._input.ports["movement_sensor_value"].activation_value
        if sensor_id == 1 or sensor_id == 2:
            if activation_value > 0.5:
                self._result.ports[f"activation_room_{sensor_id}"].state = True
            else:
                self._result.ports[f"activation_room_{sensor_id}"].state = False

            if sensor_id == 1:
                self.send_port_activation_room_1()
            elif sensor_id == 2:
                self.send_port_activation_room_2()
