from FaceIDImplTOP import FaceIDImplTOP

class FaceIDImpl(FaceIDImplTOP):

    personDB = {
        0: "Tim",
        1: "Sebastian",
        2: "Andre",
        3: "Danyls",
        4: "Merlin",
    }

    def __init__(self):
        super().__init__(
            client_id="unlock.FaceUnlock.faceid", # client ID has to match the fully qualified name in FaceUnlock.mt
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        self._result["visitor"].visitor_id = 1
        self._result["visitor"].name = "Bert"
        self._result["visitor"].allowed = False

    def compute(self) -> None:
        # manipulate the visitor field
        self._result["visitor"].visitor_id = self._input.payload.personId
        self._result["visitor"].name = self.personDB[self._result["visitor"].visitor_id]
        self._result["visitor"].allowed = self._result["visitor"].name in [ "Sebastian", "Andre", "Tim"]

        print("[FaceID-Python] visitor", self._result["visitor"].name, "authorized" if self._result["visitor"].allowed else "not authorized")
        # send the current state of visitor on /ports/unlock/FaceUnlock/faceid/visitor
        self.send_port_visitor()