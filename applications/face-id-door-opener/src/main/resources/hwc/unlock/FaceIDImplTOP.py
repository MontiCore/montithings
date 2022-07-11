from IComputable import IComputable, GenericResult, GenericInput

class FaceIDInput(GenericInput):
    pass

class FaceIDResult(GenericResult):
    pass

class FaceIDImplTOP(IComputable):

    def getInitialValues(self) -> GenericResult:
        super().getInitialValues()

    def compute(self, _input: GenericInput) -> GenericResult:
        super().compute(_input)