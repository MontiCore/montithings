${tc.signature("componentName")}

from IComputable import IComputable, GenericResult, GenericInput

class ${componentName}Input(GenericInput):
    pass

class ${componentName}Result(GenericResult):
    pass

class ${componentName}ImplTOP(IComputable):

    def getInitialValues(self) -> GenericResult:
        super().getInitialValues()

    def compute(self, _input: GenericInput) -> GenericResult:
        super().compute(_input)