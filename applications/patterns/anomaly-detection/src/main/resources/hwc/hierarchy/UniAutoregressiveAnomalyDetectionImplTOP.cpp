
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

          float inSource2Value = (float)input.getinSource2Value().value();
          bool is_anomaly_inSource2Value = this->ad->is_anomaly(inSource2Value);

          if (!is_anomaly_inSource2Value)
          {
            result.setoutMiddlemanInput20(input.getinSource2Value().value());
          }
          else
          {
            std::cout << "Port inSource2Value value: " << value << " is anomaly. Block sending." << std::endl;
          }
          float inSourceValue = (float)input.getinSourceValue().value();
          bool is_anomaly_inSourceValue = this->ad->is_anomaly(inSourceValue);

          if (!is_anomaly_inSourceValue)
          {
            result.setoutMiddlemanInput10(input.getinSourceValue().value());
          }
          else
          {
            std::cout << "Port inSourceValue value: " << value << " is anomaly. Block sending." << std::endl;
          }
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