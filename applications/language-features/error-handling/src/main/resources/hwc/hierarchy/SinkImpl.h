#pragma once
#include <SinkImplTOP.h>

namespace montithings {
namespace hierarchy {

class SinkImpl : public SinkImplTOP {
public:
	SinkImpl() = default;
	SinkResult getInitialValues() override;
	SinkResult compute(SinkInput input) override;
};

}}