// (c) https://github.com/MontiCore/monticore
#pragma once
#include <DoorImplTOP.h>

namespace montithings {
namespace unlock {

class DoorImpl : public DoorImplTOP {
public:
	using DoorImplTOP::DoorImplTOP;
	DoorResult getInitialValues() override;
	DoorResult compute(DoorInput input) override;
};

}}