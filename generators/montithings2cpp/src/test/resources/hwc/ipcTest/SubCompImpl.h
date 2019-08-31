    #pragma once
    #include "SubCompInput.h"
    #include "SubCompResult.h"
    #include "IComputable.h"
    #include <stdexcept>
    
    
    
class SubCompImpl : IComputable<SubCompInput,SubCompResult>{

public:
	SubCompImpl() = default;
	SubCompResult getInitialValues() override;
	SubCompResult compute(SubCompInput input) override;
};



