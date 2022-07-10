from IComputable import IComputable, GenericResult, GenericInput

class SinkInput(GenericInput):
    pass

class SinkResult(GenericResult):
    pass

class SinkImplTOP(IComputable):

    def getInitialValues(self) -> GenericResult:
        super().getInitialValues()

    def compute(self, _input: GenericInput) -> GenericResult:
        super().compute(_input)