from random import choice

from FaceIDImplTOP import FaceIDImplTOP, FaceIDInput, FaceIDResult
from FaceUnlock_pb2 import Person


class FaceIDImpl(FaceIDImplTOP):

    personDB = {
        0: "Tim",
        1: "Sebastian",
        2: "Andre",
        3: "Danyls",
        4: "Merlin",
    }
    def getInitialValues(self) -> FaceIDResult:
        return FaceIDResult()

    def compute(self, _input: FaceIDInput) -> FaceIDResult:
        person = Person()
        person.visitor_id = _input.payload.personId
        person.name = self.personDB[person.visitor_id]
        person.allowed = person.name in [ "Sebastian", "Andre", "Tim"]

        print("[FaceID-Python] visitor", person.name, "authorized" if person.allowed else "not authorized")
        return FaceIDResult(person)
