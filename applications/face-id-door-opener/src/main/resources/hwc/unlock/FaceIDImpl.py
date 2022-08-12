from random import choice

from FaceIDImplTOP import FaceIDImplTOP, FaceIDInput, FaceIDResult
from FaceUnlock_pb2 import Person
from MQTTClient import MQTTConnector

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

    def getInitialValues(self) -> FaceIDResult:
        return FaceIDResult()

    def compute(self) -> None:
        # manipulate the visitor field
        _result["visitor"].visitor_id = _input.payload.personId
        _result["visitor"].name = self.personDB[person.visitor_id]
        _result["visitor"].allowed = person.name in [ "Sebastian", "Andre", "Tim"]

        print("[FaceID-Python] visitor", person.name, "authorized" if person.allowed else "not authorized")
        # send the current state of visitor on /ports/unlock/FaceUnlock/faceid/visitor
        self.send_port_visitor()