// (c) https://github.com/MontiCore/monticore
#pragma once
#include <DoubleInput.h>
#include <DoubleResult.h>
#include <IComputable.h>

namespace montithings {
namespace hierarchy {

class DoubleImpl : public IComputable<DoubleInput,DoubleResult>{

private:
public:
  DoubleImpl() = default;
  DoubleResult getInitialValues() override;
  DoubleResult compute(DoubleInput input) override;
};

}}