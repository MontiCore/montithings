# (c) https://github.com/MontiCore/monticore
from FaceIDImplTOP import FaceIDImplTOP

class FaceIDImpl(FaceIDImplTOP):

    personDB = {
        0: "Alice",
        1: "Bob",
        2: "Charlie",
        3: "Dave",
        4: "Erin",
    }

    def __init__(self):
        super().__init__(
            client_id="unlock.FaceUnlock.faceid", # client ID has to match the fully qualified name in FaceUnlock.mt
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result.ports["visitor"].visitor_id = 1
        self._result.ports["visitor"].name = "Bert"
        self._result.ports["visitor"].allowed = False

    def compute(self, port) -> None:
        print(f"New values on port {port}")
        # manipulate the visitor field
        self._result.ports["visitor"].visitor_id = self._input.ports["image"].person_id
        self._result.ports["visitor"].name = self.personDB[self._result.ports["visitor"].visitor_id]
        self._result.ports["visitor"].allowed = self._result.ports["visitor"].name in [ "Alice", "Bob", "Erin"]

        print("[FaceID-Python] visitor", self._result.ports["visitor"].name, "authorized" if self._result.ports["visitor"].allowed else "not authorized")
        # send the current state of visitor on /ports/unlock/FaceUnlock/faceid/visitor
        self.send_port_visitor()
