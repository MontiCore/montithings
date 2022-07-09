from SinkImplTOP import SinkImplTOP, SinkInput, SinkResult

class SinkImpl(SinkImplTOP):

    def getInitialValues(self) -> SinkResult:
        return SinkResult()

    def compute(self, _input: SinkInput) -> SinkResult:
        print(_input.payload)
        return SinkResult()