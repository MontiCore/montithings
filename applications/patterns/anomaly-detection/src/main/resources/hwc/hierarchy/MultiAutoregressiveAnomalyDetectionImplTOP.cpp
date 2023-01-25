
#include "MultivariateAutoregressiveAnomalyDetectionImpl.h"
#include "MultiAutoregressiveAnomalyDetectionImpl.h"
#include <iostream>

namespace montithings {
    namespace hierarchy {
        MultiAutoregressiveAnomalyDetectionResult
        MultiAutoregressiveAnomalyDetectionImpl::getInitialValues()
        {
          return {};
        }

        MultiAutoregressiveAnomalyDetectionResult
        MultiAutoregressiveAnomalyDetectionImpl::compute(MultiAutoregressiveAnomalyDetectionInput input)
        {
          MultiAutoregressiveAnomalyDetectionResult result;

          float inSourceValue = (float)input.getinSourceValue().value();
          float inSource2Value = (float)input.getinSource2Value().value();

          std::vector<float> inputs_0(2);

          inputs_0.push_back(inSourceValue);
          inputs_0.push_back(inSource2Value);

          bool is_anomaly_0 = this->ad->is_anomaly(inputs_0);

          if (!is_anomaly_0)
          {
            result.setoutMiddlemanInput10(input.getinSourceValue().value());
            result.setoutMiddlemanInput21(input.getinSource2Value().value());
          }
          else
          {
            std::cout << "MultiAutoregressiveAnomalyDetection: Input is anomaly. Block sending." << std::endl;
          }

          return result;
        }
    } // namespace hierarchy
} // namespace montithings