#pragma once
#include <ConverterImplTOP.h>

namespace montithings {
namespace hierarchy {

class ConverterImpl : ConverterImplTOP {

private:

public:
  ConverterImpl() = default;
	ConverterResult getInitialValues() override;
	ConverterResult compute(ConverterInput input) override;
};

}}