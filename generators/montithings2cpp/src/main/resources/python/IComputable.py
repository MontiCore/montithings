import string
from uuid import UUID, uuid4

class GenericInput:
    payload = {}

    def __init__(self, payload, uuid: UUID) -> None:
        self.payload = payload
        self.uuid = uuid

class GenericResult:
    payload = {}

    def __init__(self, payload) -> None:
        self.payload = payload
        self.uuid = uuid4()

class IComputable:

    def getInitialValues(self) -> None:
        raise NotImplementedError()

    def compute(self, in_port: string) -> None:
        raise NotImplementedError()
