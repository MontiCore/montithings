#pragma once
#include <DoubleImplTOP.h>

namespace montithings {
namespace hierarchy {

class DoubleImpl : public DoubleImplTOP {

private:
public:
  DoubleImpl() = default;
  DoubleResult getInitialValues() override;
  DoubleResult compute(DoubleInput input) override;
};

}}