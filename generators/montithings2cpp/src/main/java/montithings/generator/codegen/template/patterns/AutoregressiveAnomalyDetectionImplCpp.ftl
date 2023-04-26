${tc.signature("packageName", "compname", "namesOfInputPorts", "namesOfOutputPorts")}
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
          if (input.get${namesOfInputPorts[i]?cap_first}())
          {
              float ${namesOfInputPorts[i]} = (float)input.get${namesOfInputPorts[i]?cap_first}().value();

              state.add_past_value_${namesOfInputPorts[i]}(${namesOfInputPorts[i]});

              bool is_anomaly_${namesOfInputPorts[i]} = this->ad->is_anomaly(${namesOfInputPorts[i]}, state.get_past_values_${namesOfInputPorts[i]}());

              if (!is_anomaly_${namesOfInputPorts[i]})
              {
                result.set${namesOfOutputPorts[i]?cap_first}(input.get${namesOfInputPorts[i]?cap_first}().value());
                interface.getPort${namesOfOutputPorts[i]?cap_first}()->setNextValue (result.get${namesOfOutputPorts[i]?cap_first}Message());
              }
              else
              {
                std::cout << "Port ${namesOfInputPorts[i]} value: " << ${namesOfInputPorts[i]} << " is anomaly. Block sending." << std::endl;
              }
          }
          </#list>

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings