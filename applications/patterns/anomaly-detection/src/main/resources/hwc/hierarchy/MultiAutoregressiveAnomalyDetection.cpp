
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

          float inSource2Value = (float)input.getinSource2Value().value();
          float inSourceValue = (float)input.getinSourceValue().value();

          std::vector<float> inputs_0(2);

          inputs_0.push_back(inSource2Value);
          inputs_0.push_back(inSourceValue);

          bool is_anomaly_0 = this->ad->is_anomaly(inputs_0);

          if (!is_anomaly_0)
          {
            result.setoutMiddlemanInput(input.getinSource2Value().value());
            result.setoutMiddlemanInput(input.getinSourceValue().value());
          }
          else
          {
            std::cout << "MultiAutoregressiveAnomalyDetection: Input is anomaly. Block sending." << std::endl;
          }

          return result;
        }
    } // namespace hierarchy
} // namespace montithings