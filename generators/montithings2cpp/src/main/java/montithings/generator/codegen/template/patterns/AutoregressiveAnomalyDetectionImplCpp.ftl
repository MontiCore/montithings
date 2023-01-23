${tc.signature("packageName", "compname", "namesOfInputPorts", "namesOfOutputPorts", "existsHwc")}
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

          <#list 0..namesOfInputPorts?size-1 as i>
          float ${namesOfInputPorts[i]} = (float)input.get${namesOfInputPorts[i]}().value();
          bool is_anomaly_${namesOfInputPorts[i]} = this->ad->is_anomaly(${namesOfInputPorts[i]});

          if (!is_anomaly_${namesOfInputPorts[i]})
          {
            result.set${namesOfOutputPorts[i]}(input.get${namesOfInputPorts[i]}().value());
          }
          else
          {
            std::cout << "Port ${namesOfInputPorts[i]} value: " << value << " is anomaly. Block sending." << std::endl;
          }
          </#list>

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings