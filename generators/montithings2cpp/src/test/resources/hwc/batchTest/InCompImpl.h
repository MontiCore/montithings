// (c) https://github.com/MontiCore/monticore
#pragma once
#include "InCompInput.h"
#include "InCompResult.h"
#include "IComputable.h"
#include <stdexcept>


	class InCompImpl : IComputable<InCompInput,InCompResult>{
	
	public:
		InCompImpl(){};
		virtual InCompResult getInitialValues() override;
		virtual InCompResult compute(InCompInput input) override;
	};
	
	
	
