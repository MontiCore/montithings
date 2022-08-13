from uuid import UUID, uuid4

class GenericInput:
    payload = {}

    def __init__(self, payload, uuid: UUID) -> None:
        self.payload = payload
        self.uuid = uuid

class GenericResult:
    payload = {}

    def __init__(self, payload) -> None:
        # TODO: map to value0->payload->data
        self.payload = payload
        self.uuid = uuid4()

class IComputable:

    def getInitialValues(self) -> GenericResult:
        raise NotImplementedError()

    def compute(self, input_: GenericInput) -> GenericResult:
        raise NotImplementedError()
