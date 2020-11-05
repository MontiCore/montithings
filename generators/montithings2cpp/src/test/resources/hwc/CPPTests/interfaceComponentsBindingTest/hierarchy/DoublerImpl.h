// (c) https://github.com/MontiCore/monticore
#pragma once
#include <DoublerInput.h>
#include <DoublerResult.h>
#include <IComputable.h>

namespace montithings {
namespace hierarchy {

class DoublerImpl : public IComputable<DoublerInput,DoublerResult>{

private:
public:
  DoublerImpl() = default;
  DoublerResult getInitialValues() override;
  DoublerResult compute(DoublerInput input) override;
};

}}