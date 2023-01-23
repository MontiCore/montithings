${tc.signature("packageName", "compname", "batchesOfNamesOfInputPorts", "batchesOfNamesOfOutputPorts", "existsHwc")}
#include "MultivariateAutoregressiveAnomalyDetectionImpl.h"
#include "${compname}Impl.h"
#include <iostream>

namespace montithings {
    namespace ${packageName} {
        ${compname}Result
        ${compname}Impl::getInitialValues()
        {
          return {};
        }

        ${compname}Result
        ${compname}Impl::compute(${compname}Input input)
        {
          ${compname}Result result;

          <#list 0..batchesOfNamesOfInputPorts?size-1 as i>
          <#list batchesOfNamesOfInputPorts[i] as nameOfInputPort>
          float ${nameOfInputPort} = (float)input.get${nameOfInputPort}().value();
          </#list>

          std::vector<float> inputs_${i}(${batchesOfNamesOfInputPorts[i]?size});

          <#list batchesOfNamesOfInputPorts[i] as nameOfInputPort>
          inputs_${i}.push_back(${nameOfInputPort});
          </#list>

          bool is_anomaly_${batchesOfNamesOfInputPorts[i]} = this->ad->is_anomaly(inputs_${i});

          if (!is_anomaly_${batchesOfNamesOfInputPorts[i]})
          {
            <#list 0..batchesOfNamesOfOutputPorts[i]?size-1 as j>
            result.set${batchesOfNamesOfOutputPorts[i][j]}(input.get${batchesOfNamesOfInputPorts[i][j]}().value());
            </#list>
          }
          else
          {
            std::cout << "${compname}: Input is anomaly. Block sending." << std::endl;
          }
          </#list>

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings