from DoublerImplTOP import DoublerImplTOP, SinkInput, SinkResult, Foo

class DoublerImpl(DoublerImplTOP):

    def getInitialValues(self) -> DoublerResult:
        return DoublerResult()

    def compute(self, input: DoublerInput) -> DoublerResult:
        input
        return DoublerResult()