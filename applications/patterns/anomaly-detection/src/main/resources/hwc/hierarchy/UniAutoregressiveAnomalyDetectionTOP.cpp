
#include "UniAutoregressiveAnomalyDetectionImpl.h"
#include <iostream>

namespace montithings {
    namespace hierarchy {
        UniAutoregressiveAnomalyDetectionResult
        UniAutoregressiveAnomalyDetectionImpl::getInitialValues()
        {
          return {};
        }

        UniAutoregressiveAnomalyDetectionResult
        UniAutoregressiveAnomalyDetectionImpl::compute(UniAutoregressiveAnomalyDetectionInput input)
        {
          UniAutoregressiveAnomalyDetectionResult result;

          float value = (float)input.getin().value();
          bool is_anomaly = this->ad->is_anomaly(value);

          if (!is_anomaly)
          {
            result.setValue(input.getin().value());
          }
          else
          {
            std::cout << "UniAutoregressiveAnomalyDetection: " << value << " is anomaly. Block sending." << std::endl;
          }

          return result;
        }
    } // namespace hierarchy
} // namespace montithings