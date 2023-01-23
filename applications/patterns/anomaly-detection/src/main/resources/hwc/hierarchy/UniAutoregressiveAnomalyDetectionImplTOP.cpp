
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

          float inMiddlemanOutput = (float)input.getinMiddlemanOutput().value();
          bool is_anomaly_inMiddlemanOutput = this->ad->is_anomaly(inMiddlemanOutput);

          if (!is_anomaly_inMiddlemanOutput)
          {
            result.setoutSinkValue0(input.getinMiddlemanOutput().value());
          }
          else
          {
            std::cout << "Port inMiddlemanOutput value: " << value << " is anomaly. Block sending." << std::endl;
          }

          return result;
        }
    } // namespace hierarchy
} // namespace montithings