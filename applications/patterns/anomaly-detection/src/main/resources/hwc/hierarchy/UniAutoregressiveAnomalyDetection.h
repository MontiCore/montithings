
#pragma once
#include "UniAutoregressiveAnomalyDetectionImplTOP.h"
#include "AutoregressiveAnomalyDetection.h"

namespace montithings {
    namespace hierarchy {
        class UniAutoregressiveAnomalyDetectionImpl : public UniAutoregressiveAnomalyDetectionImplTOP
        {

        private:
          AutoregressiveAnomalyDetection *ad = new AutoregressiveAnomalyDetection(5, (float)25);

        public:
          using UniAutoregressiveAnomalyDetectionImplTOP::UniAutoregressiveAnomalyDetectionImplTOP;
          UniAutoregressiveAnomalyDetectionResult getInitialValues() override;
          UniAutoregressiveAnomalyDetectionResult compute(UniAutoregressiveAnomalyDetectionInput input) override;
        };
    } // namespace hierarchy
} // namespace montithings