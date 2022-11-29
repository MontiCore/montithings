# (c) https://github.com/MontiCore/monticore
from StorageImplTOP import StorageImplTOP


class StorageImpl(StorageImplTOP):
    def __init__(self):
        super().__init__(
            client_id="hierarchy.Storage",
            reconnect_on_failure=True,
        )

    def getInitialValues(self) -> None:
        print("Get initial values")

    def compute(self, port) -> None:
        print(f"New values on port {port}: {self._input.ports['data'].value}")
