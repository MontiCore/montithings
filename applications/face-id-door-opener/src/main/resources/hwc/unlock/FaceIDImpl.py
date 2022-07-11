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
        result = self.personDB[_input.payload.personId] in [ "Sebastian", "Andre", "Tim"]
        return FaceIDResult(result)
