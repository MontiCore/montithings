#pragma once
#include <SourceImplTOP.h>

namespace montithings {
namespace hierarchy {

class SourceImpl : public SourceImplTOP {

private:
  int lastValue;
public:
  SourceImpl() = default;
  SourceResult getInitialValues() override;
  SourceResult compute(SourceInput input) override;
};

}}