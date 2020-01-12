// (c) https://github.com/MontiCore/monticore
#pragma once
#include "OutCompInput.h"
#include "OutCompResult.h"
#include "IComputable.h"
#include <stdexcept>


	class OutCompImpl : IComputable<OutCompInput,OutCompResult>{
	
	public:
		OutCompImpl(){};
		virtual OutCompResult getInitialValues() override;
		virtual OutCompResult compute(OutCompInput input) override;
	};
	
	
	
