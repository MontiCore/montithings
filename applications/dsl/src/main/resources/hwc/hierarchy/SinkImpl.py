# (c) https://github.com/MontiCore/monticore
from SinkImplTOP import SinkImplTOP

class SinkImpl(SinkImplTOP):

    def __init__(self):
        super().__init__(
            client_id="hierarchy.Example.sink", # client ID has to match the fully qualified name in Example.mt
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["ret"].num = 0

    def compute(self, port) -> None:
        print(f"New values on port {port}:")
        print(f"{self._input.ports['value'].text} {self._input.ports['value'].val} | {self._input.ports['value'].num}")
        # manipulate the visitor field
        self._result.ports["ret"].num = self._input.ports["value"].num

        # send the current state of visitor on /ports/unlock/FaceUnlock/faceid/visitor
        self.send_port_ret()