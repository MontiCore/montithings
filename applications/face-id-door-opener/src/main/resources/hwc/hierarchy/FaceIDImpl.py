from random import choice

from FaceIDImplTOP import FaceIDImplTOP, FaceIDInput, FaceIDResult

class FaceIDImpl(FaceIDImplTOP):

    def getInitialValues(self) -> FaceIDResult:
        return FaceIDResult()

    def compute(self, _input: FaceIDInput) -> FaceIDResult:
        names = [
            "Sebastian"
            "Andre",
            "Tim"
        ]
        result = _input.payload in names
        return FaceIDResult(result)