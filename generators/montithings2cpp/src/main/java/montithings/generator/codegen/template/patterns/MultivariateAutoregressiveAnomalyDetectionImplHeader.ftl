${tc.signature("packageName", "compname", "windowSize", "tolerance", "existsHwc")}
#pragma once
#include "${compname}ImplTOP.h"
#include "MultivariateAutoregressiveAnomalyDetection.h"

namespace montithings {
    namespace ${packageName} {
        class ${compname}Impl : public ${compname}ImplTOP
        {

        private:
          MultivariateAutoregressiveAnomalyDetection *ad = new MultivariateAutoregressiveAnomalyDetection(${windowSize}, (float)${tolerance});

        public:
          using ${compname}ImplTOP::${compname}ImplTOP;
          ${compname}Result getInitialValues() override;
          ${compname}Result compute(${compname}Input input) override;
        };
    } // namespace ${packageName}
} // namespace montithings