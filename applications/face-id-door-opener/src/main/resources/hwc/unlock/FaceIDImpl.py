from random import choice

from FaceIDImplTOP import FaceIDImplTOP, FaceIDInput, FaceIDResult


class FaceIDImpl(FaceIDImplTOP):

    personDB = {
        1: "Sebastian",
        2: "Andre",
        3: "Danyls",
        4: "Merlin",
        5: "Tim"
    }
    def getInitialValues(self) -> FaceIDResult:
        return FaceIDResult()

    def compute(self, _input: FaceIDInput) -> FaceIDResult:
        name = self.personDB[_input.payload.personId]
        result = name in [ "Sebastian", "Andre", "Tim"]

        print("[FaceID-Python] visitor", name, "authorized" if result else "not authorized")
        return FaceIDResult(result)
