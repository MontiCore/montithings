    #pragma once
    #include "ImplementationInput.h"
    #include "ImplementationResult.h"
    #include "IComputable.h"
    #include <stdexcept>
    
    
    
class ImplementationImpl : IComputable<ImplementationInput,ImplementationResult>{

public:
	ImplementationImpl() = default;
	ImplementationResult getInitialValues() override;
	ImplementationResult compute(ImplementationInput input) override;
};



