// (c) https://github.com/MontiCore/monticore
#pragma once
#include "DdcReader.h"
#include "DDCReaderInput.h"
#include "DDCReaderResult.h"
#include "IComputable.h"
#include <stdexcept>


class DDCReaderImpl : IComputable<DDCReaderInput,DDCReaderResult>{

public:
	DDCReaderImpl() = default;
	DDCReaderResult getInitialValues() override;
	DDCReaderResult compute(DDCReaderInput input) override;
};
