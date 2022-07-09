class GenericInput:
    payload = {}

    def __init__(self, payload) -> None:
        self.payload = payload

class GenericResult:
    payload = {}

    def __init__(self, payload) -> None:
        self.payload = payload

class IComputable:

    def getInitialValues(self) -> GenericResult:
        raise NotImplementedError()

    def compute(self, input_: GenericInput) -> GenericResult:
        raise NotImplementedError()
