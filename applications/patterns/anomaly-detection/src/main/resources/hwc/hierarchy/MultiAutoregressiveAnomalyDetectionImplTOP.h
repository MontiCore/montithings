
#pragma once
#include "MultiAutoregressiveAnomalyDetectionImplTOP.h"
#include "MultivariateAutoregressivyAnomalyDetection.h"

namespace montithings {
    namespace hierarchy {
        class MultiAutoregressiveAnomalyDetectionImpl : public MultiAutoregressiveAnomalyDetectionImplTOP
        {

        private:
          MultivariateAutoregressivyAnomalyDetection *ad = new MultivariateAutoregressivyAnomalyDetection(5, (float)25);

        public:
          using MultiAutoregressiveAnomalyDetectionImplTOP::MultiAutoregressiveAnomalyDetectionImplTOP;
          MultiAutoregressiveAnomalyDetectionResult getInitialValues() override;
          MultiAutoregressiveAnomalyDetectionResult compute(MultiAutoregressiveAnomalyDetectionInput input) override;
        };
    } // namespace hierarchy
} // namespace montithings